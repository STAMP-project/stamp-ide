package eu.stamp.eclipse.dspot.plugin.context;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

public class DSpotContext implements IDSpotContext {
	
	private IJavaProject project;
	
	private List<String> noTestFolders;
	
	private List<String> testFolders;
	
	private List<DSpotTargetSource> targets;
	
	private Boolean compiledFound;

	public DSpotContext(IJavaProject project) {
		loadProject(project);
	}
	
	@Override
	public IJavaProject getProject() { return project; }

	@Override
	public List<String> getNoTestSourceFolders() { return noTestFolders; }

	@Override
	public List<String> getTestSourceFolders() { return testFolders; }

	@Override
	public List<File> getTestFiles() {
        List<File> result = new ArrayList<File>(targets.size());
        for(DSpotTargetSource target : targets)if(target.isTest)
        	result.add(target.file);
		return result;
	}

	@Override
	public String getFullName(String partialName) {
		partialName = partialName.replaceAll(".java","");
		for(DSpotTargetSource target : targets)
			if(target.fullName.endsWith(partialName))
				return target.fullName;
		return null;
	}
	
	@Override
	public List<String> getTestMethods() {
		List<String> result = new LinkedList<String>();
		for(DSpotTargetSource target : targets) {
			String name = target.fullName;
			List<String> methods = target.testMethods;
			for(String method : methods)
				result.add(name + "/" + method);
		}
		return result;
	}
	
	private List<File> search(File folder, String end) { // recursive
		
		File[] files = folder.listFiles();
		List<File> result = new LinkedList<File>();
		
		List<File> provisionalList;
		for(File file : files) {
			if(file.isDirectory()) {
				provisionalList = search(file,end);
				for(File provisionalFile : provisionalList)
					result.add(provisionalFile);
			}
			else if(file.getPath().endsWith(end))
				result.add(file);
		}
		
		return result;
	}

	@Override
	public void loadProject(IJavaProject project) {
       
		this.project = project;
		
		noTestFolders = new LinkedList<String>();
		testFolders = new LinkedList<String>();
		targets = new LinkedList<DSpotTargetSource>();
		compiledFound = false;
		
		try {
			IClasspathEntry[] entries = project.getRawClasspath();
			DSpotTargetFolder folder;
			for(IClasspathEntry entry : entries)
				if(entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					folder = new DSpotTargetFolder(entry);
					if(folder.containsTests)
						testFolders.add(folder.sourceFolder.getAbsolutePath());
					if(folder.containsNoTests) noTestFolders.add(folder.sourceFolder.getAbsolutePath());
					List<DSpotTargetSource> list = folder.targetSources;
					for(DSpotTargetSource element : list)
						targets.add(element);
				}		
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	public boolean compiledFilesFound() { return compiledFound; }
	
	private class DSpotTargetFolder {
		
		File sourceFolder;
		File outputFolder;
		List<DSpotTargetSource> targetSources;
		boolean containsTests;
		boolean containsNoTests;
		
		// entry must be of kind source
		DSpotTargetFolder(IClasspathEntry entry){
			
			// get the folders
			sourceFolder = new File(project.getProject().getLocation().toString()
					+ "/" + entry.getPath().removeFirstSegments(1).toString());
			outputFolder = new File(project.getProject().getLocation().toString()
					+ "/" + entry.getOutputLocation().removeFirstSegments(1).toString());
			
			//get the source and compiled files lists
			List<File> sources = search(sourceFolder,".java");
			List<File> compiledOnes = search(outputFolder,".class");
			if(!compiledOnes.isEmpty()) compiledFound = true;
			
			// generate DSpotTargetSourceObjects
			targetSources = 
					new ArrayList<DSpotTargetSource>(compiledOnes.size());
			for(File source : sources)
				createDSpotTargetSource(source,compiledOnes);
		}
		
       void createDSpotTargetSource(File source,List<File> compiledOnes) {
    	   String name = source.getName().replaceAll(".java","");
    	   for(File compiledOne : compiledOnes)
    		   if(compiledOne.getName().replaceAll(".class","")
    				   .equalsIgnoreCase(name)) {
    		DSpotTargetSource targetSource =
    				new DSpotTargetSource(source,compiledOne,sourceFolder);
    			  if(targetSource.isTest) containsTests = true;
    			  else containsNoTests = true;
    			  if(targetSource.sucess) targetSources.add(targetSource);
    		      break;
    		   }		   
       }
		
	}
	
	private class DSpotTargetSource {
		
		File file;
		String fullName;
		boolean isTest;
		boolean sucess;
		List<String> testMethods;
		
		DSpotTargetSource(File source,File compiled, File folder){
			this.file = source;
			// TODO check
			String folderPath = folder.getAbsolutePath();
			fullName = source.getAbsolutePath();
			if(fullName.contains("\\")) {
				fullName = fullName.replace('\\','/');
				folderPath = folderPath.replace('\\','/');
			}
			fullName = fullName.replaceFirst(folderPath,"");
			if(fullName.contains("/")) fullName = fullName.replaceAll("/",".");
			fullName = fullName.replaceAll(".java","");
			if(fullName.charAt(0) == '.' && fullName.length() > 1) 
				fullName = fullName.substring(1);
			
			DSpotCompiledFileInfo info = 
					new DSpotCompiledFileInfo(project,fullName);
			isTest = info.containsTests();
			testMethods = info.getTestMethods();
			sucess = info.itsOk();
		}
		
	}
	

}
