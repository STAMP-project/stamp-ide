package eu.stamp.eclipse.botsing.launch;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

import eu.stamp.botsing.Botsing;
import eu.stamp.eclipse.botsing.constants.BotsingPluginConstants;

public class BotsingAlternativeJob {

	private final BootsingLaunchInfo info;
	
	private final String separator;
	
	public BotsingAlternativeJob(BootsingLaunchInfo info){
		this.info = info;
		if(System.getProperty("os.name").contains("indows"))
			separator = "\\";
		else separator = "/";
	}
	public void run() {
		
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
				
				String[] command = info.getCommand();
				for(String sr : command) System.out.println(sr);
				/*
				 *  Execute Botsing
				 */
			    String path = Platform.getBundle(BotsingPluginConstants.BOTSING_PLUGIN_ID).getLocation();
			    path = path.substring(path.indexOf(separator),path.length());
				System.setProperty("user.dir",path);
				String sr = System.getProperty("user.dir");
				System.out.println(sr);
				
				Botsing botsing = new Botsing();
				botsing.parseCommandLine(command);

				
			} catch (CoreException e) {
				e.printStackTrace();
			}
			System.setProperty("user.dir",userDir);
	}
}
