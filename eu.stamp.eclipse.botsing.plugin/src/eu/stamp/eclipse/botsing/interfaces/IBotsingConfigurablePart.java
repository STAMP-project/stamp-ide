package eu.stamp.eclipse.botsing.interfaces;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

public interface IBotsingConfigurablePart {
    
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy configuration);
	
	public void load(ILaunchConfigurationWorkingCopy configuration);
}
