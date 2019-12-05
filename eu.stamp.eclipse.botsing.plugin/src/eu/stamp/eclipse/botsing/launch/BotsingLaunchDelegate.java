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
package eu.stamp.eclipse.botsing.launch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IModuleDescription;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;

import eu.stamp.eclipse.botsing.call.DoublePrintStream;
import eu.stamp.eclipse.botsing.constants.BotsingPluginConstants;

public class BotsingLaunchDelegate extends JavaLaunchDelegate{
	
	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
	throws CoreException {
		
	PrintStream out = System.out;
	try {
		DoublePrintStream myDoubleStream = new DoublePrintStream(out,BostingJob.outputFile);
		System.setOut(myDoubleStream);
		System.setProperty("eclipse.consoleLog","true");
			} catch (FileNotFoundException e2) { e2.printStackTrace(); }
		
	if (monitor == null) {
	monitor = new NullProgressMonitor(); 
	}

	monitor.beginTask(NLS.bind("{0}...", new String[]{configuration.getName()}), 3); //$NON-NLS-1$
	// check for cancellation
	if (monitor.isCanceled()) {
	return;
	}
	try {
	monitor.subTask("Executing Botsing: verifying launch attributes"); 

	String mainTypeName = verifyMainTypeName(configuration);
	IVMRunner runner = getVMRunner(configuration, mode);

	File workingDir = verifyWorkingDirectory(configuration);
	String workingDirName = null;
	if (workingDir != null) {
	workingDirName = workingDir.getAbsolutePath();
	}

	// Environment variables
	String[] envp= getEnvironment(configuration);

	// Program & VM arguments
	String pgmArgs = getProgramArguments(configuration);
	String vmArgs = getVMArguments(configuration);
	ExecutionArguments execArgs = new ExecutionArguments(vmArgs, pgmArgs);

	// VM-specific attributes
	Map<String, Object> vmAttributesMap = getVMSpecificAttributesMap(configuration);

	// Bug 522333 :to be used for modulepath only for 4.7.*
	String[][] paths = getClasspathAndModulepath(configuration);

	// Create VM config
	String[] classpath = getClasspath(configuration);
	List<String> collection = new LinkedList<String>();
	for(String entry : classpath) {
		collection.add(entry);
	}

	//Add Botsing library
	URL url = new URL(BotsingPluginConstants.BOTSING_JAR_URL);
	collection.add (FileLocator.toFileURL(url).getPath());
	
	url = new URL(BotsingPluginConstants.JUNIT_JAR_URL);
	collection.add(FileLocator.toFileURL(url).getPath());
	
	url = new URL(BotsingPluginConstants.HAMCREST_JAR_URL);
	collection.add(FileLocator.toFileURL(url).getPath());
	
	url = new URL(BotsingPluginConstants.MOCKITO_JAR_URL);
	collection.add(FileLocator.toFileURL(url).getPath());
	
	url = new URL(BotsingPluginConstants.BOTSING_INVOCATION_JAR_URL);
	collection.add(FileLocator.toFileURL(url).getPath());

	classpath = new String[collection.size()];
	classpath = collection.toArray(classpath);


	VMRunnerConfiguration runConfig = new VMRunnerConfiguration(mainTypeName, classpath);
	runConfig.setProgramArguments(execArgs.getProgramArgumentsArray());
	runConfig.setEnvironment(envp);
	runConfig.setVMArguments(execArgs.getVMArgumentsArray());
	runConfig.setWorkingDirectory(workingDirName);
	runConfig.setVMSpecificAttributesMap(vmAttributesMap);
	// current module name, if so
	try {
	IJavaProject proj = JavaRuntime.getJavaProject(configuration);
	if (proj != null) {
	IModuleDescription module = proj == null ? null : proj.getModuleDescription();
	String modName = module == null ? null : module.getElementName();
	if (modName != null) {
	runConfig.setModuleDescription(modName);
	}
	}
	}
	catch (CoreException e) {
	// Not a java Project so no need to set module description
	}

	if (!JavaRuntime.isModularConfiguration(configuration)) {
	// Bootpath
	runConfig.setBootClassPath(getBootpath(configuration));
	} else {
	// module path
	runConfig.setModulepath(paths[1]);
	}

	// check for cancellation
	if (monitor.isCanceled()) {
	return;
	}

	// stop in main
	prepareStopInMain(configuration);

	// done the verification phase
	monitor.worked(1);

	monitor.subTask("\"Executing Botsing : creating source locator");
	// set the default source locator if required
	setDefaultSourceLocator(launch, configuration);
	monitor.worked(1);

	// Launch the configuration - 1 unit of work
	runner.run(runConfig, launch, monitor);

	// check for cancellation
	if (monitor.isCanceled()) {
	return;
	}
	} catch (MalformedURLException e1) {
	e1.printStackTrace();
	} catch (IOException e1) {
	e1.printStackTrace();
	}
	System.setOut(out);
	    }

	public static String getPluginDir(String pluginId)
	{
	/* get bundle with the specified id */
	Bundle bundle = Platform.getBundle(pluginId);
	if( bundle == null )
	throw new RuntimeException("Could not resolve plugin: " + pluginId + "\r\n" +
	"Probably the plugin has not been correctly installed.\r\n" +
	"Running eclipse from shell with -clean option may rectify installation.");

	/* resolve Bundle::getEntry to local URL */
	URL pluginURL = null;
	try {
	//pluginURL = Platform.resolve(bundle.getEntry("/"));
	pluginURL = FileLocator.resolve(bundle.getEntry("/"));
	} catch (IOException e) {
	throw new RuntimeException("Could not get installation directory of the plugin: " + pluginId);
	}
	String pluginInstallDir = pluginURL.getPath().trim();
	if( pluginInstallDir.length() == 0 )
	throw new RuntimeException("Could not get installation directory of the plugin: " + pluginId);

	/* since path returned by URL::getPath starts with a forward slash, that
	 * is not suitable to run commandlines on Windows-OS, but for Unix-based
	 * OSes it is needed. So strip one character for windows. There seems
	 * to be no other clean way of doing this. */
	if( Platform.getOS().compareTo(Platform.OS_WIN32) == 0 )
	pluginInstallDir = pluginInstallDir.substring(1);

	return pluginInstallDir;
	}


}
