package eu.stamp.eclipse.botsing.launch;

import java.util.LinkedList;
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
		List<String> resultList = new LinkedList<String>();
		for(IBotsingProperty property : properties) {
			String[] strings = property.getPropertyString();
			for(String sr : strings) resultList.add(sr);
		}
		String[] result = new String[resultList.size()];
	    int i = 0;
	    for(String sr : resultList) {
	    	result[i] = sr;
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
