package eu.stamp.eclipse.botsing.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

import eu.stamp.eclipse.botsing.constants.BotsingPluginConstants;


public class ConfigurationsManager {
	
	private ILaunchConfiguration[] configurations;
	
	private int configurationInUse;

	public ConfigurationsManager() {
	
		  // Find configurations
		try {
		ILaunchManager manager = DebugPlugin.getDefault()
				.getLaunchManager();
		ILaunchConfiguration[] provisionalConfigurations = 
				manager.getLaunchConfigurations(
				manager.getLaunchConfigurationType(
						BotsingPluginConstants.BOTSING_LAUNCH_ID));
		
		for(int i = 0; i < provisionalConfigurations.length; i++)
				configurations[i] = 
				provisionalConfigurations[i].getWorkingCopy();
			
		} catch (CoreException e) {
				e.printStackTrace();
			}
		
		configurationInUse = 0;
	}
	public String[] getConfigurationNames() {
		if(configurations == null) return new String[] {""};
		if(configurations.length < 1) return new String[] {""};
		
		String[] result = new String[configurations.length];
		for(int i = 0; i < configurations.length; i++)
            result[i] = configurations[i].getName();
		return result;
	}
	
	public void setConfigurationInUse(String name) {
       for(int i = 0; i < configurations.length; i++) 
    	   if(configurations[i].getName().equalsIgnoreCase(name)) {
    	   configurationInUse = i;
    	   return;
       }
	}
	
	public ILaunchConfigurationWorkingCopy getCopy() {
		try {
			return configurations[configurationInUse].getWorkingCopy();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

}
