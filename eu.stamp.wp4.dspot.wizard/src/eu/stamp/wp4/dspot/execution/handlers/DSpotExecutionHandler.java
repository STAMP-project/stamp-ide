/*******************************************************************************
 * Copyright (c) 2018 Atos
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
package eu.stamp.wp4.dspot.execution.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.stamp.wp4.dspot.execution.launch.DSpotProperties;
import eu.stamp.wp4.dspot.wizard.utils.WizardConfiguration;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class. 
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class DSpotExecutionHandler extends AbstractHandler {
	
	private WizardConfiguration conf;
	private static String arguments;
	
	public DSpotExecutionHandler(WizardConfiguration conf,String arguments) {
		super();
		this.conf = conf;
		DSpotExecutionHandler.arguments = arguments;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {
			executeDSpotInJDTLauncher(conf.getPro(), event);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return null;
	}


	private static void executeDSpotInJDTLauncher(IJavaProject javaProject, ExecutionEvent event) throws CoreException, ExecutionException {
		if (javaProject == null) {
			 IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			 MessageDialog.openInformation(
			 window.getShell(),
			 "Execute DSpot",
			 "Please, select a Java Project in the Package Explorer");
			return;
		}
						
		DebugPlugin plugin = DebugPlugin.getDefault();
	      ILaunchManager lm = plugin.getLaunchManager();
	      ILaunchConfigurationType t = lm.getLaunchConfigurationType(DSpotProperties.LAUNCH_CONF_ID);
	      ILaunchConfigurationWorkingCopy wc = t.newInstance(
	        null, DSpotProperties.LAUNCH_CONF_NAME);
	      wc.setAttribute(
	        IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, 
	        javaProject.getElementName());
	      wc.setAttribute(
	        IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, DSpotProperties.MAIN_CLASS);
	      wc.setAttribute(
	  	        IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, arguments);
	      ILaunchConfiguration config = wc.doSave();   
	      config.launch(ILaunchManager.RUN_MODE, null);
	}

}
