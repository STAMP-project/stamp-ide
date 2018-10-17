package eu.stamp.eclipse.botsing.launch.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class BotsingLaunchConfigurationTabGroup 
             extends AbstractLaunchConfigurationTabGroup {
	
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		setTabs(new ILaunchConfigurationTab[]{ 
				new BotsingLaunchConfigurationTab(),new CommonTab()});
	}
}
