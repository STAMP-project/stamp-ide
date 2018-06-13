package eu.stamp.wp4.descartes.wizard.execution;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

import org.eclipse.m2e.actions.MavenLaunchConstants;

import eu.stamp.wp4.descartes.wizard.utils.DescartesHtmlManager;
import eu.stamp.wp4.descartes.wizard.utils.DescartesWizardConstants;

@SuppressWarnings("restriction")
public class DescartesEclipseJob extends Job {
	
	private String projectPath;
	private String pomName;
	private String configurationName;

	public DescartesEclipseJob(String projectPath,String pomName,String configurationName) {
		super("Descartes working");
		this.projectPath = projectPath;
		this.pomName = pomName;
		this.configurationName = configurationName;
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
			
			/* give the parameters of this Descartes launch 
			 * to the ILaunchConfigurationWorkingCopy object
			 */
            wc.setAttribute(MavenLaunchConstants.ATTR_POM_DIR,projectPath);
            wc.setAttribute(MavenLaunchConstants.ATTR_GOALS, 
            		"clean package org.pitest:pitest-maven:mutationCoverage -DmutationEngine=descartes -f "+pomName);
            wc.setAttribute(MavenLaunchConstants.PLUGIN_ID, DescartesWizardConstants.DESCARTES_PLUGIN_ID);
            wc.setAttribute(DescartesWizardConstants.POM_NAME_LAUNCH_CONSTANT, pomName);
            
            // save the configuration and start to run
            ILaunchConfiguration config = wc.doSave(); 
  	        ILaunch launch = config.launch(ILaunchManager.RUN_MODE, null);
  	        
  	        // after finishing the process show the Descartes view for the html summaries
  	        while(!launch.isTerminated());
            DescartesHtmlManager htmlManager = new DescartesHtmlManager(projectPath + "/target/pit-reports");
            htmlManager.openBrowsers();       
			
		} catch (CoreException e) {
			e.printStackTrace();
		}	
		return Status.OK_STATUS;
	}
}
