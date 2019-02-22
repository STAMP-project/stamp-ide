
package eu.stamp.eclipse.plugin.dspot.launch;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
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
       
        String command = "eu.stamp-project:dspot-maven:LATEST:amplify-unit-tests" + 
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
				
			String outputFolder = DSpotContext.getInstance().getProject()
					.getProject().getLocation().toString() + "/target/dspot/output";
			
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					try {
					DSpotView view = (DSpotView)PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().showView(DSpotView.ID);
					view.parseJSON(outputFolder);
					} catch(IOException | PartInitException e) {
						e.printStackTrace();
					}
				}
			});
			
		} catch (CoreException | InterruptedException e) {
		    e.printStackTrace();
		}
        
        return Status.OK_STATUS;
	}
	
}