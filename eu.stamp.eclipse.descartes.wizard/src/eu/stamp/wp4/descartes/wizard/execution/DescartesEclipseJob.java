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

import eu.stamp.wp4.descartes.wizard.utils.DescartesWizardConstants;

@SuppressWarnings("restriction")
public class DescartesEclipseJob extends Job {
	
	private String projectPath;
	private String pomName;

	public DescartesEclipseJob(String projectPath,String pomName) {
		super("Descartes working");
		this.projectPath = projectPath;
		this.pomName = pomName;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		DebugPlugin plugin = DebugPlugin.getDefault();
		ILaunchManager lm = plugin.getLaunchManager();
		ILaunchConfigurationType t = lm.getLaunchConfigurationType(
				DescartesWizardConstants.LAUNCH_CONFIGURATION_DESCARTES_ID);
		 try {
			ILaunchConfigurationWorkingCopy wc = t.newInstance(
				        null, "Descartes Launch");
            wc.setAttribute(MavenLaunchConstants.ATTR_POM_DIR,projectPath);
            wc.setAttribute(MavenLaunchConstants.ATTR_GOALS, "clean package org.pitest:pitest-maven:mutationCoverage -f "+pomName);
            wc.setAttribute(MavenLaunchConstants.PLUGIN_ID, DescartesWizardConstants.DESCARTES_PLUGIN_ID);
            
            ILaunchConfiguration config = wc.doSave();   
  	        config.launch(ILaunchManager.RUN_MODE, null);
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return Status.OK_STATUS;
	}
}
