package eu.stamp.wp4.descartes.wizard.execution;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.m2e.internal.launch.MavenLaunchDelegate;

import eu.stamp.wp4.descartes.wizard.utils.DescartesHtmlManager;

@SuppressWarnings("restriction")
/**
 *  this delegate calls their super class, the MavenLaunchDelegate to execute Descartes and
 *  after the execution displays the html reports in the Descartes Eclipse view
 */
public class DescartesDelegate extends MavenLaunchDelegate {
	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
		      throws CoreException {
         super.launch(configuration, mode, launch, monitor);
         
         // after finishing the process show the Descartes view for the html summaries
         while(!launch.isTerminated());
         DescartesHtmlManager htmlManager = new DescartesHtmlManager(
        		 configuration.getAttribute(ATTR_POM_DIR,"") + "/target/pit-reports");
         htmlManager.openBrowsers(); 
	}
}
