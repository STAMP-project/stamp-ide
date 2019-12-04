package eu.stamp.eclipse.botsing.model.generation.classpth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import eu.stamp.eclipse.botsing.model.generation.constants.ModelgenerationPluginConstants;

public class ClassPathCreator implements IRunnableWithProgress {
	
	private String projectLocation;
	
	private ProgressMonitorDialog monitorDialog;
	
	private String classPath;
	
	private boolean maven;
	
	public ClassPathCreator(IJavaProject project,Shell shell) {
		try {
			if(project.getProject().hasNature(ModelgenerationPluginConstants.MAVEN_NATURE)) {
				projectLocation = project.getProject().getLocation().toString();
				monitorDialog = new ProgressMonitorDialog(shell);
				maven = true;
			} else {
				classPath = "";
				maven = false;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
    public void createClassPathFile() {
    	if(maven) {
		try {
			monitorDialog.run(true, false, this);
		} catch (InvocationTargetException | InterruptedException e) { e.printStackTrace(); }
    	}
    }
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		
		monitor.beginTask("computing classpath",11);
		
		// copy dependencies
		monitor.subTask("Copy dependencies");
		System.out.println("---------- COPY DEPENDENCIES TASK STARTS ---------"); // TODO remove logs
        executeMavenCommand("org.apache.maven.plugins:maven-dependency-plugin:copy-dependencies");
		System.out.println("-------------- COPY TASK FINISH -----------"); // TODO remove logs
		monitor.worked(5);
		
		// package
		monitor.subTask("maven package");
		System.out.print("------- PACKAGE TASK STARTS -------");
		executeMavenCommand("package","-DskipTests");
		System.out.println("------- PACKAGE TASK FINISH --------");
		monitor.worked(5);
		
		// write the file
		monitor.subTask("Writing classpath.txt file");
		// get location for the file
		File classPathFile = new File(projectLocation + "/classpath.txt");
		
		// Build classpath string
				String separator = System.getProperty("path.separator");
				StringBuilder builder = new StringBuilder();
				builder.append("./target/classes").append(separator).append("./target/test-classes");
				// look for dependencies jars
				File dependenciesFolder = new File(projectLocation + "/target/dependency");
				if(dependenciesFolder.exists() && dependenciesFolder.isDirectory()) {
					Set<File> jars = getJars(dependenciesFolder,0);
					for(File jar : jars)builder.append(separator).append("target/dependency/").append(jar.getName());
				}
					
				
				// create and write file
				try {
					classPathFile.createNewFile();
					BufferedWriter writer = new BufferedWriter(new FileWriter(classPathFile));
					classPath = builder.toString();
					writer.write(classPath);
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		
		monitor.done();
	}
	
	public String getClassPathString() { return classPath; }
	
	/**
	 * Recursive search for the jars in a folder, there is a maximun recursion level (1000)
	 * @param folder where looking for the jars
	 * @return a Set containing the jars in the folder
	 */
	private Set<File> getJars(File folder,int level){
		if(level > 1000) return new HashSet<File>();
		level++;
		Set<File> result = new HashSet<File>();
		File[] files = folder.listFiles();
		for(File file : files) {
			if(file.getPath().endsWith(".jar")) result.add(file);
			else if(file.isDirectory()) result.addAll(getJars(file,level)); //recursive call
		}
		return result;
	}
	
	private void executeMavenCommand(String command) { 
		executeMavenCommand(command,null); 
		}
	
	private void executeMavenCommand(String command, String arguments) {
		
		String mavenExec;
		if(System.getProperty("os.name").contains("indow")) mavenExec = "mvn.bat";
		else mavenExec = "mvn";
		
		ProcessBuilder processBuilder;
		if(arguments == null) processBuilder = new ProcessBuilder(mavenExec,command);
		else processBuilder = new ProcessBuilder(mavenExec,command,arguments);
		processBuilder.directory(new File(projectLocation));
		
		try {
			Process process = processBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while((line = reader.readLine()) != null) {
				System.out.println(line); // TODO check where to print lines
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
