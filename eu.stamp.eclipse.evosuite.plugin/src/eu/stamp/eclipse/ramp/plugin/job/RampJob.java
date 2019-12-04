/*******************************************************************************
 * Copyright (c) 2019 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Ricardo Jose Tejada Garcia (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.eclipse.ramp.plugin.job;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import eu.stamp.eclipse.botsing.model.generation.launch.SequentialJob;
import eu.stamp.eclipse.ramp.plugin.constants.RampPluginConstants;

public class RampJob extends SequentialJob {
	
	private final int place;
	
	private final String launchString;
	
	private final String projectName;
	
	private ILaunchConfiguration launch;

	public RampJob(String className, String launchString,String projectName,int place) {
		super("Evosuite running on class" + className);
		this.launchString = launchString;
		this.projectName = projectName;
		this.place = place;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		try {
			
		ILaunchConfigurationWorkingCopy launchCopy = 
				DebugPlugin.getDefault().getLaunchManager()
		.getLaunchConfigurationType(RampPluginConstants.EVOUITE_LAUNCH_ID)
		.newInstance(null,"Evosuite Launch");
		
		launchCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
				"-Xmx4000m");
		
		launchCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, 
				projectName);
		
		launchCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, 
				RampPluginConstants.MAIN_CLASS);
		
		launchCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
				launchString);
		
		// System.out.println("Launch String : " + launchString); // logs
		launch = launchCopy.doSave();
		ILaunch launching = launch.launch(ILaunchManager.RUN_MODE,null);
	    processes = launching.getProcesses();
		}catch(CoreException e) {
			e.printStackTrace();
		}
		return Status.OK_STATUS;
	}
	
	/**
	 * @return the launch configuration object, null if the job has not be launched
	 */
	public ILaunchConfiguration getLaunch() { return launch; }

	public int getPlace() { return place; }
}
