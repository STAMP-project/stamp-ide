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

public class BostingJob extends Job {
    
	private final BootsingLaunchInfo info;
	
	public BostingJob(BootsingLaunchInfo info) {
		super("Bosting working");
        this.info = info;
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
				info.appendToConfiguration(wc);
				
				String[] infoCommand = info.getCommand();
				String[] extraCommand = {
						        "population=100",
								"search_budget=1800",
								"max_recursion=30",
								"test_dir=/home/ricardo/Tests/crash"
				};
				String[] command = 
						new String[infoCommand.length + extraCommand.length];
                for(int i = 0; i < extraCommand.length; i++) 
                	command[i] = extraCommand[i];
                int j = 0;
                for(int i = extraCommand.length; i < command.length; i++) {
                	command[i] = infoCommand[j];
                	j++;
                }
                
                String line = command[0];
				for(int i = 1; i < command.length; i++) {
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
