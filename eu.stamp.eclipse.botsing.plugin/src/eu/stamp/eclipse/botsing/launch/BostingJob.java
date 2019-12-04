/*******************************************************************************
 * Copyright (c) 2018 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ricardo José Tejada García (Atos) - main developer
 * Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.eclipse.botsing.launch;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import eu.stamp.eclipse.botsing.call.InputManager;
import eu.stamp.eclipse.botsing.constants.BotsingPluginConstants;
import eu.stamp.eclipse.botsing.dialog.BotsingExecutionErrorDialog;
import eu.stamp.eclipse.botsing.properties.OutputTraceProperty;
import eu.stamp.eclipse.botsing.wizard.BotsingWizard;

public class BostingJob extends Job {
    
	private final BotsingLaunchInfo info;
	
	private final BotsingWizard wizard;
	
	private ILaunch launch;
	
	public static File outputFile;
	
	private String modelConfiguration;
	
	public BostingJob(BotsingLaunchInfo info,BotsingWizard wizard) {
		super("Bosting working");
        this.info = info;
        this.wizard = wizard;
	}
	
	public void setModelConfiguration(String modelConfiguration) {
		this.modelConfiguration = modelConfiguration;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		/*  
         *  getting the launch configuration type
         */
		DebugPlugin plugin = DebugPlugin.getDefault();
		ILaunchManager manager = plugin.getLaunchManager();
		ILaunchConfigurationType launchType = manager
	               .getLaunchConfigurationType(
	            		   BotsingPluginConstants.BOTSING_LAUNCH_ID);
        
				// create an ILaunchConfigurationWorkingCopy
			try {
				ILaunchConfigurationWorkingCopy wc = launchType.newInstance(
					        null, info.getName());
				wizard.appendToConfiguration(wc);
				
				String[] command = info.getCommand();
				SimpleDateFormat format = new SimpleDateFormat("-dd-MM-yy_hh-mm-ss");
				String path = wc.getAttribute(OutputTraceProperty.KEY,"");
				InputManager input;
				if(path != null) {
					if(!path.isEmpty()) {
					    path += "/botsing_execution_output" + format.format(new Date()) 
						+ ".txt";
						input = new InputManager(command,path);
					}
					else input = new InputManager(command);
				}
				else input = new InputManager(command);
                String line = input.serializeToString();

				String userDir = "";
				boolean setUserDir = false;
				for(String sr : command) {
					if(setUserDir) {
						userDir = sr;
						break;
					}
					if(sr.contains("crash_log"))
						setUserDir = true;
				}
				// Windows (a path can contain both \\ and /)
				if(userDir.contains("\\")) {
					int n = userDir.lastIndexOf("\\");
					if(userDir.contains("/"))
						if(userDir.lastIndexOf("/") > n) n = userDir.lastIndexOf("/");
					userDir = userDir.substring(0,n);
				}
				else userDir = userDir.substring(0,userDir.lastIndexOf("/"));
				
				/*
				 *  Avoid file not found exception
				 */
				//System.out.println(userDir);

				
				File config = new File(userDir +
						"/src/main/java/eu/stamp/botsing");
				
				if(!config.exists()) config.mkdirs();
				config = new File(config.getAbsolutePath()
						+  "/" + "config.properties");
				if(!config.exists()) config.createNewFile();
			    
				wc.setAttribute(
						IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
						userDir);
				wc.setAttribute(
						IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, 
						BotsingPluginConstants.BOTSING_MAIN);
				
				if(modelConfiguration != null)
				       wc.setAttribute(BotsingPluginConstants.ATTR_MODEL_SER,modelConfiguration);
				else wc.setAttribute(BotsingPluginConstants.ATTR_MODEL_SER,"");
				
				String[] arguments = prepareLaunch(new String[] {line});
				StringBuilder builder = new StringBuilder();
				for(String sr : arguments)builder.append(sr).append(' ');
				builder.append(outputFile.getAbsolutePath());
				String argument = builder.toString();		
				wc.setAttribute(
						IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,argument);
				String vmArgs;
			    if(System.getProperty("java.version").contains("8")) vmArgs = "-d64 -Xmx4000m";
			    else vmArgs = "-Xmx4000m";
				wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,vmArgs);
				
				ILaunchConfiguration configuration = wc.doSave();
				launch = configuration.launch(ILaunchManager.RUN_MODE, null);
				
				while(!launch.isTerminated()) { Thread.sleep(50); }
				
				
				if(isToolError(launch))
					showErrorDialog(true);
				
			} catch (CoreException | IOException | InterruptedException e) {
				e.printStackTrace();
			} 
		return Status.OK_STATUS;
	}
	
	public ILaunch getLaunch() { return launch; }
	
    public boolean isToolError(ILaunch launch) {
    	if(launch == null) return false;
	    IProcess[] processes = launch.getProcesses();
	     try {
           for(IProcess process : processes)
				if(process.getExitValue() != 0) return true;
			} catch (DebugException e) {
				e.printStackTrace();
			}
	    return false;
    }
    
    public void showErrorDialog(boolean toolError) {
    	Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				Shell shell = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell();
				
                     BotsingExecutionErrorDialog dialog = 
						new BotsingExecutionErrorDialog(shell,toolError);
                     dialog.open();
			}
    	});
    }
    
  private String[] prepareLaunch(String[] args) {  
	String argument;
	
	if(args.length != 1) {
		StringBuilder builder = new StringBuilder();
		for(String arg : args) builder.append(' ').append(arg);
        argument = builder.toString();
	} else {
		argument = args[0];
	}
	
       InputManager input = new InputManager();
       input.loadFromString(argument);
       
       String[] command = input.getCommand();
       
       if(!input.outputFileSet()) {
    	   return command;
       }
       
       String path = input.getoutputFilePath();
	
	if(path != null)if(!path.isEmpty()){
    outputFile = new File(path);
	if(outputFile.getParentFile() != null)
		if(!outputFile.getParentFile().exists()) outputFile.getParentFile().mkdirs();
		
		try {
	    if(!outputFile.exists())outputFile.createNewFile();
		
		System.out.println("\n----- COMMAND -----\n");
		for(String sr : command) System.out.println(sr);
		System.out.println("\n---END---\n");
		
		return command;
		
	} catch (IOException e) {
		e.printStackTrace();
	}
	}
	return command;
  }
}
