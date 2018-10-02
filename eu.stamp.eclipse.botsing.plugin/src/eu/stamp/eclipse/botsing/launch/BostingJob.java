package eu.stamp.eclipse.botsing.launch;

import java.io.File;
import java.io.IOException;

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
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import eu.stamp.eclipse.botsing.constants.BotsingPluginConstants;
import eu.stamp.eclipse.botsing.invocation.Invocation;
import eu.stamp.eclipse.botsing.wizard.BotsingWizard;

public class BostingJob extends Job {
    
	private final BootsingLaunchInfo info;
	
	private final BotsingWizard wizard;
	
	public BostingJob(BootsingLaunchInfo info,BotsingWizard wizard) {
		super("Bosting working");
        this.info = info;
        this.wizard = wizard;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		/*  
         * getting the launch configuration type
         */
		DebugPlugin plugin = DebugPlugin.getDefault();
		ILaunchManager manager = plugin.getLaunchManager();
		ILaunchConfigurationType launchType = manager
	               .getLaunchConfigurationType(
	            		   BotsingPluginConstants.BOTSING_LAUNCH_ID);
	          
        
				// create an ILaunchConfigurationWorkingCopy
			try {
				ILaunchConfigurationWorkingCopy wc = launchType.newInstance(
					        null, info.getName());
				wizard.appendToConfiguration(wc);
				
				String[] command = info.getCommand();

                String line = command[0];
               // System.out.println(command[0]);
				for(int i = 1; i < command.length; i++) {
					// System.out.println(command[i]);
					line += Invocation.INVOCATION_SEPARATOR + command[i];
				}
				/*
				 *  Execute Botsing
				 */	
				String userDir = "";
				for(String sr : command)if(sr.contains("crash_log")) {
					userDir = sr.substring(sr.lastIndexOf("=")+1);
					break;
				}
				if(userDir.contains("\\")) userDir.replaceAll("\\","/");
				userDir = userDir.substring(0,userDir.lastIndexOf("/"));
				
				/*
				 *  Avoid file not found exception
				 */
				//System.out.println(userDir);
				File config = new File(userDir +
						"/src/main/java/eu/stamp/botsing");
				
				if(!config.exists()) config.mkdirs();
				config = new File(config.getAbsolutePath()
						+  "/" + "config.properties");
				if(!config.exists()) config.createNewFile();
				
			  //  System.out.println("User dir : " + userDir);
			    
				wc.setAttribute(
						IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
						userDir);
				wc.setAttribute(
						IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, 
						BotsingPluginConstants.BOTSING_MAIN);
				System.out.println(line);
				wc.setAttribute(
						IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, 
						line);
				ILaunchConfiguration configuration = wc.doSave();
				
				configuration.launch(ILaunchManager.RUN_MODE, null);
				
			} catch (CoreException | IOException e) {
				e.printStackTrace();
			} 
		return Status.OK_STATUS;
	}

}
