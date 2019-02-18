
package eu.stamp.eclipse.plugin.dspot.launch;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.m2e.actions.MavenLaunchConstants;

import eu.stamp.eclipse.plugin.dspot.context.DSpotContext;
import eu.stamp.eclipse.plugin.dspot.files.DSpotFileUtils;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;
import eu.stamp.eclipse.plugin.dspot.properties.DSpotProperties;

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
			configuration.launch(ILaunchManager.RUN_MODE,null);
		} catch (CoreException e) {
		    e.printStackTrace();
		}
        
        return Status.OK_STATUS;
	}
	
}