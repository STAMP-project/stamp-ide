package eu.stamp.eclipse.dspot.test.detection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;

public class DSpotPluginSourcesLocator {

	private List<String> sourcesFolders;
	private List<String> testsFolders;
	
	private List<String> testNames;
	
	private final String separator;
	
	public DSpotPluginSourcesLocator() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win") || os.contains("Win")) separator = "\\";
		else separator = "/";
	}
	
	public List<String> getSources() { return sourcesFolders; }
	
	public List<String> getTests() { return testsFolders; }
	
	public void inspectProject(IJavaProject project) {
		
		sourcesFolders = new LinkedList<String>();
		testsFolders = new LinkedList<String>();
		testNames = new LinkedList<String>();
		
		File projectFolder = project.getProject().getLocation().toFile();
		
		List<File> javaFiles = getJavaFiles(projectFolder);
		List<SourceFile> provisionalList = new LinkedList<SourceFile>();
		for(File javaFile : javaFiles) {
			try {
				provisionalList.add(
						new SourceFile(javaFile,getFullName(javaFile)));
			} catch(IOException e) { e.printStackTrace();}
		}
		List<SourceFolder> folders = generateSourceFolders(provisionalList);
		for(SourceFolder folder : folders) {
			// put the test classes names in the test names list
			for(String sr : folder.qNames) {
				String[] fragments = sr.split(".");
				sr = fragments[fragments.length-1];
				System.out.println(sr); // TODO remove this
				testNames.add(sr);
			}
			// put in test folders list
			folder.putInCorrectPlace(
				new DSpotPluginSourcesInspector(project));
	}
        // TODO eliminate true false
	}
	/**
	 * @param javaFile
	 * @return the full qualified name for the class of this java file
	 * @throws IOException
	 */
	private String getFullName(File javaFile) throws IOException {
		    // get the class name
            String fullName = javaFile.getName();
            if(fullName.endsWith(".java")) {
            	int n = fullName.length() - ".java".length();
            	fullName = fullName.substring(0,n);
            }
            // read the file to find the package declaration
            BufferedReader reader = new BufferedReader(new FileReader(javaFile));
            String line;
            while((line = reader.readLine()) != null) {
            	line = line.replaceAll(" ","");
            	if(line.startsWith("package")) {
            		line = line.replaceAll(";", "");
            		line = line.replaceAll("package","");
            		fullName = line + "." + fullName;
            		System.out.println(fullName);
            		break;
            	}
            	else if(line.startsWith("import")) break; // after this no possible package declaration
            	else if(line.contains("{")) break;
            }
            reader.close();
            return fullName;
	}
	/**
	 * @param folder, root folder
	 * @return a list with the .java files in the folder subsystem (infinite deep)
	 */
	private List<File> getJavaFiles(File folder){
		List<File> javaFiles = new LinkedList<File>();
		File[] elements = folder.listFiles();
		
		for(File element : elements) {
			if(element.isDirectory()) {
				List<File> javaFilesInElement = getJavaFiles(element);
				for(File javaFile : javaFilesInElement) javaFiles.add(javaFile);
			}
			else if(element.getName().contains(".java")){
				javaFiles.add(element);
			}
		}
		return javaFiles;
	}
private List<SourceFolder> generateSourceFolders(List<SourceFile> files) {
	
	List<SourceFolder> result = new LinkedList<SourceFolder>();
	result.add(new SourceFolder(
			new File(files.get(0).getSuperFolderPath()),files.get(0).qName));
	
	boolean set;
	for(SourceFile file : files) {
	   set = false;
	   String sr1 = file.getSuperFolderPath();
	   for(SourceFolder folder : result) {
		   String sr2 = folder.folder.getAbsolutePath();
		   if(sr1.equalsIgnoreCase(sr2)) {
			   folder.addQName(file.qName);
			   set = true;
			   break;
		   }
	   }
	   if(!set)
		result.add(new SourceFolder(
				new File(file.getSuperFolderPath()),file.qName));
	}
	return result;
}
	public List<String> getTestsNames() { return testNames; }
	private class SourceFile {
		File file;
		String qName;
		
		SourceFile(File file,String qName){
			this.file = file;
			this.qName = qName;
		}
		
		String getSuperFolderPath() {
			String path = file.getAbsolutePath();
			String name = separator + qName.replaceAll("\\.", separator) + "\\." + "java";
			return path.replaceAll(name,"");
		}
	}
	
	private class SourceFolder {
		
		File folder;
		List<String> qNames;
		
		SourceFolder(File folder,String qName){
			this.folder = folder;
			qNames = new LinkedList<String>();
			qNames.add(qName);
		}
		
		void addQName(String qName) { qNames.add(qName); }
		
		void putInCorrectPlace(DSpotPluginSourcesInspector inspector) {
			for(String qName : qNames)if(inspector.isTest(qName)) {
				testsFolders.add(folder.getPath());
				return;
			}
			sourcesFolders.add(folder.getPath());	
		}
	}
	
}
