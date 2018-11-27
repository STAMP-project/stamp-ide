/*******************************************************************************
 * Copyright (c) 2018 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Ricardo José Tejada García (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.wp4.descartes.wizard.execution;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

import org.eclipse.m2e.actions.MavenLaunchConstants;

import eu.stamp.wp4.descartes.wizard.DescartesWizard;
import eu.stamp.wp4.descartes.wizard.utils.DescartesWizardConstants;

@SuppressWarnings("restriction")
public class DescartesEclipseJob extends Job {
	
	private String projectPath;
	private String pomName;
	private String configurationName;
	private String profileID;
	
	private DescartesWizard wizard;

	public DescartesEclipseJob(String projectPath,String pomName,
			String configurationName,DescartesWizard wizard,String profileID) {
		super("Descartes working");
		this.wizard = wizard;
		this.projectPath = projectPath;
		this.pomName = pomName;
		this.configurationName = configurationName;
		this.profileID = profileID;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
        /*  
         * getting the launch configuration type
         */
		DebugPlugin plugin = DebugPlugin.getDefault();
		ILaunchManager lm = plugin.getLaunchManager();
		ILaunchConfigurationType t = lm.getLaunchConfigurationType(
				DescartesWizardConstants.LAUNCH_CONFIGURATION_DESCARTES_ID);
		
		 try {
				/*  create an ILaunchConfiguration object using the implementation of
				 *  the  Descartes launch configuration type
				 */
			ILaunchConfigurationWorkingCopy wc = t.newInstance(
				        null, configurationName);
			
			/*
			 *  append parts data to configuration
			 */
			wizard.appendToConfiguration(wc);
			/* give the parameters of this Descartes launch 
			 * to the ILaunchConfigurationWorkingCopy object
			 */
            wc.setAttribute(MavenLaunchConstants.ATTR_POM_DIR,projectPath);
            wc.setAttribute(MavenLaunchConstants.ATTR_GOALS, 
            		"clean package org.pitest:pitest-maven:mutationCoverage -DmutationEngine=descartes -f "+pomName);
            wc.setAttribute(MavenLaunchConstants.PLUGIN_ID, DescartesWizardConstants.DESCARTES_PLUGIN_ID);
            wc.setAttribute(DescartesWizardConstants.POM_NAME_LAUNCH_CONSTANT, pomName);
            wc.setAttribute(MavenLaunchConstants.ATTR_PROFILES, profileID);
            
            // save the configuration and start to run
            ILaunchConfiguration config = wc.doSave(); 
  	        config.launch(ILaunchManager.RUN_MODE, null);

		} catch (CoreException e) {
			e.printStackTrace();
		}	
		return Status.OK_STATUS;
	}
}
