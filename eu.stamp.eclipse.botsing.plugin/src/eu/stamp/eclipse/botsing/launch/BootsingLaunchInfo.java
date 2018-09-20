package eu.stamp.eclipse.botsing.launch;

import java.util.List;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import eu.stamp.eclipse.botsing.interfaces.IBotsingProperty;
import eu.stamp.eclipse.botsing.properties.AbstractBotsingProperty;

public class BootsingLaunchInfo {
	
	private final String name;

	private final List<AbstractBotsingProperty> properties;
	
	public BootsingLaunchInfo(String name,List<AbstractBotsingProperty> properties) {
		this.name = name;
		this.properties = properties;
	}
	
	public String[] getCommand() {
		String[] result = new String[properties.size()];
		int i = 0;
		for(IBotsingProperty property : properties) {
			result[i] = property.getPropertyString();
			i++;
		}
		return result;
	}

	public void appendToConfiguration(ILaunchConfigurationWorkingCopy configuration) {
		for(AbstractBotsingProperty property : properties)
			property.appendToConfiguration(configuration);
	}
	
	public String getName() { return name; }
}
