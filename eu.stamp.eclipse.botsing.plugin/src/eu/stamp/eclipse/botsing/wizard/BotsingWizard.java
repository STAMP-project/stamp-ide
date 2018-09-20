package eu.stamp.eclipse.botsing.wizard;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import eu.stamp.eclipse.botsing.constants.BotsingPluginConstants;
import eu.stamp.eclipse.botsing.interfaces.IBotsingConfigurablePart;
import eu.stamp.eclipse.botsing.launch.BostingJob;
import eu.stamp.eclipse.botsing.launch.ConfigurationsManager;

public class BotsingWizard extends Wizard{

	protected BotsingWizardPage page; 
	
	private List<IBotsingConfigurablePart> configurableParts;
	
	private final ConfigurationsManager configurationsManager;
	
	public BotsingWizard() {
		configurableParts = new LinkedList<IBotsingConfigurablePart>();
		configurationsManager = new ConfigurationsManager();
	}
	
	@Override
	public void addPages() {
		page = new BotsingWizardPage(this);
		addPage(page);
		configurableParts.add(page);
	}
	@Override
	public String getWindowTitle() { return " Bootsing Wizard "; }
	
	@Override
	public Image getDefaultPageImage() {
		final URL iconStampURL = FileLocator.find(
				Platform.getBundle(BotsingPluginConstants.BOTSING_PLUGIN_ID),
				new Path("images/stamp.png"),null);
		ImageDescriptor descriptor = ImageDescriptor.createFromURL(iconStampURL);
		Image image = descriptor.createImage();
		return image;
	}
	
	@Override
	public boolean performFinish() {
		BostingJob job = new BostingJob(page.generateBotsingLaunchInfo());
		job.schedule();;
		return true;
	}
	
	public void reconfigure(String configurationName) {
		configurationsManager.setConfigurationInUse(configurationName);
		ILaunchConfigurationWorkingCopy copy =
				configurationsManager.getCopy();
		for(IBotsingConfigurablePart part : configurableParts)
			part.load(copy);
	}
	
	public String[] getConfigurationNames() { 
		return configurationsManager.getConfigurationNames();
	}

}
