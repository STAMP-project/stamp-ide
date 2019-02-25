package eu.stamp.eclipse.dspot.plugin.interfaces;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

public interface IDSpotConfigurablePart extends IDSpotElement {
	
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy copy);
	
	public void load(ILaunchConfigurationWorkingCopy copy);
}
