package eu.stamp.eclipse.botsing.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

import eu.stamp.botsing.Botsing;
import eu.stamp.eclipse.botsing.constants.BotsingPluginConstants;

public class BostingJob extends Job {
    
	private final BootsingLaunchInfo info;
	
	private final String separator;
	
	public BostingJob(BootsingLaunchInfo info) {
		super("Bosting working");
        this.info = info;
        if(System.getProperty("os.name").contains("indows")) separator = "\\";
        else separator = "/";
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
				wc.doSave();
				
				String[] infoCommand = info.getCommand();
				String[] extraCommand = {
						        "population = 100",
								"search_budget = 1800",
								"max_recursion = 30",
								"test_dir = crash_Reproduction_Tests"
				};
				String[] command = 
						new String[infoCommand.length + extraCommand.length];
                for(int i = 0; i < infoCommand.length; i++) 
                	command[i] = infoCommand[i];
                int j = 0;
                for(int i = infoCommand.length; i < command.length; i++) {
                	command[i] = extraCommand[j];
                	j++;
                }
				for(String sr : command) System.out.println(sr);
				/*
				 *  Execute Botsing
				 */		
				Botsing botsing = new Botsing();
				botsing.parseCommandLine(command);

				
			} catch (CoreException e) {
				e.printStackTrace();
			}
		System.setProperty("user.dir",userDir);
		return Status.OK_STATUS;
	}

}
