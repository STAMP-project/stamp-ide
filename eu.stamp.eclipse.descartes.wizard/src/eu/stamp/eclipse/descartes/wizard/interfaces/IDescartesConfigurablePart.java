package eu.stamp.eclipse.descartes.wizard.interfaces;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

public interface IDescartesConfigurablePart {

	public void appendToConfiguration(ILaunchConfigurationWorkingCopy copy);
	
	public void load(ILaunchConfigurationWorkingCopy copy);
}
