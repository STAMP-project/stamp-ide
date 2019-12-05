/*******************************************************************************
 * Copyright (c) 2019 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * 	Ricardo Jose Tejada Garcia (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.eclipse.plugin.dspot.context;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class DSpotContext implements IDSpotContext {
	
	private IJavaProject project;
	
	private List<String> noTestFolders;
	
	private List<String> testFolders;
	
	private List<DSpotTargetSource> targets;
	
	private Boolean compiledFound;
	
	private Shell shell;
	
	private static DSpotContext INSTANCE;
	
	public static DSpotContext getInstance() {
		if(INSTANCE == null) INSTANCE = new DSpotContext();
		return INSTANCE;
	}

	private DSpotContext() {}
	
	@Override
	public IJavaProject getProject() { return project; }

	@Override
	public String[] getNoTestSourceFolders() { 
		return noTestFolders.toArray(new String[noTestFolders.size()]); 
		} // TODO

	@Override
	public String[] getTestSourceFolders() { 
		return testFolders.toArray(new String[testFolders.size()]); 
		}

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
	public String[] getFullNames() {
		String[] result = new String[targets.size()];
        int i = 0;
        for(DSpotTargetSource target : targets) {
        	result[i] = target.fullName;
        	i++;
        }
		return result;
	}
	
	public String[] getTestFullNames() {
		List<String> result = new ArrayList<String>(targets.size());
        for(DSpotTargetSource target : targets)if(target.isTest)
        	result.add(target.fullName);
		return result.toArray(new String[result.size()]);
	}
	
	@Override
	public String[] getTestMethods() {
		List<String> result = new LinkedList<String>();
		for(DSpotTargetSource target : targets) {
			String name = target.fullName;
			List<String> methods = target.testMethods;
			for(String method : methods)
				result.add(name + "/" + method);
		}
		return result.toArray(new String[result.size()]);
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
		
		Display.getDefault().syncExec(new Runnable(){
			@Override
			public void run() {
				shell = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell();
			}
		});
		
		try {
			IClasspathEntry[] entries = project.getRawClasspath();
			
			ProgressMonitorDialog progressWindow = 
					new ProgressMonitorDialog(shell);
			// TODO make cancelable
	    try {
		   progressWindow.run(true, false, new IRunnableWithProgress() {
					
			@Override
		    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			
			monitor.beginTask("Inspecting " + project.getProject().getName() + " ClassPath entries",entries.length);
		    
			DSpotTargetFolder folder;
		    IPath default_output_location = null;	
		    
			try {
			default_output_location = project.getOutputLocation();
			} catch (JavaModelException e) {
		   e.printStackTrace();
		   monitor.done();
			}
							
		   for(IClasspathEntry entry : entries) {
			 
			 if(entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
			 folder = new DSpotTargetFolder(entry, default_output_location);
			 if(folder.containsTests)
			 testFolders.add(folder.sourceFolder.getAbsolutePath());
			 if(folder.containsNoTests) noTestFolders.add(folder.sourceFolder.getAbsolutePath());
			 List<DSpotTargetSource> list = folder.targetSources;
			 for(DSpotTargetSource element : list)
			 targets.add(element);
									}
			 monitor.worked(1);
								}
		   monitor.done();
					} // end of run
					
				});
			} catch (InvocationTargetException | InterruptedException e1) {
				e1.printStackTrace();
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
		DSpotTargetFolder(IClasspathEntry entry, IPath default_output_location){
			outputFolder = new File(project.getProject().getLocation().toString()
					+ "/" + default_output_location.removeFirstSegments(1).toString());
			
			// get the folders
			sourceFolder = new File(project.getProject().getLocation().toString()
					+ "/" + entry.getPath().removeFirstSegments(1).toString());
			if (entry.getOutputLocation()!= null)
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
   public static void reset() {
	   INSTANCE = null;
   }
}
