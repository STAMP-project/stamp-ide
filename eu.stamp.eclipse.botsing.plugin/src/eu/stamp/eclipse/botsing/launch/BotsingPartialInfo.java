package eu.stamp.eclipse.botsing.launch;

import java.util.List;

import eu.stamp.eclipse.botsing.properties.AbstractBotsingProperty;

public class BotsingPartialInfo {

	private final String name;

	private final List<AbstractBotsingProperty> properties;
	
	public BotsingPartialInfo(List<AbstractBotsingProperty> properties) {
		this(null,properties);
	}
	
	public BotsingPartialInfo(String name,List<AbstractBotsingProperty> properties) {
		this.name = name;
		this.properties = properties;
	}
	
	public boolean nameIsSet() { return name != null; }
	
	public String getName() { return name; }
	
	public  List<AbstractBotsingProperty> getProperties(){
		return properties;
	}
	
}
