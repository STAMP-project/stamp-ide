package eu.stamp.eclipse.botsing.properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.widgets.Composite;

import eu.stamp.eclipse.botsing.interfaces.IBotsingConfigurablePart;
import eu.stamp.eclipse.botsing.interfaces.IBotsingProperty;

public abstract class AbstractBotsingProperty 
     implements IBotsingProperty, IBotsingConfigurablePart {
	
	protected final String defaultValue;
	
	protected final String key;
	
	protected final String name;
	
	protected String data;
	
	protected AbstractBotsingProperty(String defaultValue,String key,String name) {
		this.defaultValue = defaultValue;
		this.key = key;
		this.name = name;
		this.data = defaultValue;
	}
	
	@Override
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy configuration) {
     configuration.setAttribute(key, getData());
	}

	@Override
	public void load(ILaunchConfigurationWorkingCopy configuration) {
     try {
		String value = configuration.getAttribute(key,defaultValue);
		setData(value);
	} catch (CoreException e) { 
		e.printStackTrace(); 
		}
	}

	@Override
	public String[] getPropertyString() {
		return new String[] {key + "=" + getData()};
	}
	
	protected String getKey() { return key; }
	
	protected String getName() { return name; }
	
	protected String getDefaultValue() { return defaultValue; }
	
	protected abstract String getData();
	
	protected abstract void setData(String data);
	
	public abstract void createControl(Composite composite);

}
