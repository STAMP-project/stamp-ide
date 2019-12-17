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

package eu.stamp.eclipse.plugin.dspot.launch;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import eu.stamp.eclipse.plugin.dspot.context.DSpotContext;
import eu.stamp.eclipse.plugin.dspot.files.DSpotFileUtils;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;
import eu.stamp.eclipse.plugin.dspot.properties.DSpotProperties;
import eu.stamp.eclipse.plugin.dspot.view.DSpotView;

/**
 * 
 */
@SuppressWarnings("restriction")
public class DSpotJob extends Job {

	public DSpotJob(String name) {
		super(name);
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
        
		String path = DSpotContext.getInstance().getProject().getProject().getLocation().toString();
        path += "/dspot_properties";
        File folder = new File(path);
        folder.mkdirs();
        DSpotFileUtils.writePropertiesFile(folder);
       
        String command = "eu.stamp-project:dspot-maven:2.2.1:amplify-unit-tests" + 
        DSpotMapping.getInstance().getCommand();
        System.out.println(command);
		
        try {
        	String name = DSpotMapping.getInstance().getConfigurationName();
			ILaunchConfigurationWorkingCopy copy = DebugPlugin.getDefault().getLaunchManager()
			.getLaunchConfigurationType(DSpotProperties.CONFIGURATION_ID)
			.newInstance(null,name);
			//ILaunchConfigurationWorkingCopy copy = conf.getWorkingCopy();
			
			String projectPath = DSpotContext.getInstance().getProject()
					.getProject().getLocation().toString();
			copy.setAttribute(MavenLaunchConstants.ATTR_POM_DIR,projectPath);
			copy.setAttribute(MavenLaunchConstants.ATTR_GOALS,command);
			copy.setAttribute(MavenLaunchConstants.PLUGIN_ID,DSpotProperties.PLUGIN_ID);
			DSpotMapping.getInstance().prepareConfiguration(copy);
			
			ILaunchConfiguration configuration = copy.doSave();
			ILaunch launch = configuration.launch(ILaunchManager.RUN_MODE,null);
			
			while(!launch.isTerminated()) Thread.sleep(100);
				
			// look for errors
			boolean dspotError = checkProcesses(launch);
			if(dspotError) {
				showErrorDialog(false);
				return Status.OK_STATUS;
			}
			String outputFolder = DSpotContext.getInstance().getProject()
					.getProject().getLocation().toString() + "/target/dspot/output";
			File outFolder = new File(outputFolder);
			if(!outFolder.exists() || outFolder.listFiles() == null
					|| outFolder.listFiles().length < 1) {
				showErrorDialog(true);
				return Status.OK_STATUS;
			}
			
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					try {
					DSpotView view = (DSpotView)PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().showView(DSpotView.ID);
					view.parseJSON(outputFolder);
					} catch(IOException | PartInitException e) {
						e.printStackTrace();
						showErrorDialog(true);
					}
				}
			});
			
		} catch (CoreException | InterruptedException e) {
		    e.printStackTrace();
		    showErrorDialog(true);
		}
        
        return Status.OK_STATUS;
	}
	/**
	 * @param launch : the launch object related to the last DSpot launch
	 * @return : true if a process has a non zero exit (Error)
	 */
	private boolean checkProcesses(ILaunch launch) {
		IProcess[] processes = launch.getProcesses();
		try {
		for(IProcess process : processes)
			if(process.getExitValue() != 0) return true;
		} catch(DebugException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	private void showErrorDialog(boolean errorInPlugin) {
       String message, title;
       if(errorInPlugin) {
    	   title = "DSpot Plugin Error";
    	   message = "Error in DSpot Eclipse plugin, please check the trace,"
       		+ " you can report it in : https://github.com/STAMP-project/stamp-ide/issues";
       }
       else {
    	   title = "DSpot Error";
    	   message = "Error during DSpot launch, please check the trace, "
       		+ "you can report it in : https://github.com/STAMP-project/dspot/issues";
	   }
       Display.getDefault().asyncExec(new Runnable() {
		  @Override
		  public void run() {
			  MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					  .getShell(),title,message);
		  }
	  });
	}
}