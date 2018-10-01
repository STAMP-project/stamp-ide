package eu.stamp.eclipse.botsing.launch;

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
	
		
		String userDir = System.getProperty("user.dir");
	
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
                System.out.println(command[0]);
				for(int i = 1; i < command.length; i++) {
					System.out.println(command[i]);
					line += Invocation.INVOCATION_SEPARATOR + command[i];
				}
				/*
				 *  Execute Botsing
				 */	
				wc.setAttribute(
						IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, 
						BotsingPluginConstants.BOTSING_MAIN);
				System.out.println(line);
				wc.setAttribute(
						IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, 
						line);
				ILaunchConfiguration configuration = wc.doSave();
				System.setProperty(
						"user.dir","/home/ricardo/Repositorios/BotsingPlugin/stamp-gui/eu.stamp.eclipse.botsing.plugin");
				
				configuration.launch(ILaunchManager.RUN_MODE, null);
				
			} catch (CoreException e) {
				e.printStackTrace();
			}
		System.setProperty("user.dir",userDir);
		return Status.OK_STATUS;
	}

}
