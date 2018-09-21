package eu.stamp.eclipse.botsing.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
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
	
	public BostingJob(BootsingLaunchInfo info) {
		super("Bosting working");
        this.info = info;
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
				info.appendToConfiguration(wc);
				wc.doSave();
				
				String[] command = info.getCommand();
				for(String sr : command) System.out.println(sr);
				/*
				 *  Execute Botsing
				 */
				Botsing botsing = new Botsing();
				botsing.parseCommandLine(command);
				
			} catch (CoreException e) {
				e.printStackTrace();
			}
		
		return Status.OK_STATUS;
	}

}
