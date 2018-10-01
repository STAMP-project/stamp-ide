package eu.stamp.eclipse.botsing.properties;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.widgets.Composite;

public class BotsingDialogProperty extends AbstractBotsingProperty {

	private AbstractBotsingProperty property;
	
	public BotsingDialogProperty(AbstractBotsingProperty property) {
		super(property.getDefaultValue(),property.getKey(),property.getName());
		this.property = property;
	}

	@Override
	public String getData() {
		return this.data;
	}

	@Override
	public void setData(String data) {
        property.setData(data);
	}

	@Override
	public void createControl(Composite composite) {
        property.createControl(composite);
	}
	
	@Override
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy configuration) {
		property.appendToConfiguration(configuration);
	}
	
	@Override
	public void load(ILaunchConfigurationWorkingCopy configuration) {
		super.load(configuration);
		save();
		property.load(configuration);
	}
	
	public void save() { this.data = property.getData(); }
	
	public AbstractBotsingProperty getCoreProperty() {
		return property;
	}

}
