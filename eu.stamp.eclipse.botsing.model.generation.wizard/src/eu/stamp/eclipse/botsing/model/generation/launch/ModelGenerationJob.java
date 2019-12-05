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
package eu.stamp.eclipse.botsing.model.generation.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import eu.stamp.eclipse.botsing.model.generation.constants.ModelgenerationPluginConstants;

public class ModelGenerationJob extends SequentialJob {
	
	private final String arguments;
	
	private final IJavaProject project;

	public ModelGenerationJob(String arguments,IJavaProject project) {
		super("Botsing models generation");
		this.arguments = arguments;
		this.project = project;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		try {
			ILaunchConfigurationWorkingCopy launchCopy = 
					DebugPlugin.getDefault().getLaunchManager()
			.getLaunchConfigurationType(ModelgenerationPluginConstants.LAUNCH_TYPE_ID)
			.newInstance(null,"Model generation");
			
			launchCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
					"-Xmx4000m");
			
			launchCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,project.getElementName());
			
			launchCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
					project.getProject().getLocation().toString());
			
			launchCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
					ModelgenerationPluginConstants.GENERATION_MAIN);
			
			launchCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
					arguments);
			
			launchConfiguration = launchCopy.doSave();
			ILaunch launch = launchConfiguration.launch(ILaunchManager.RUN_MODE,null);
			processes = launch.getProcesses();
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return Status.OK_STATUS;
	} 
	
}
