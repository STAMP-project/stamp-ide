package eu.stamp.eclipse.botsing.wizard;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import eu.stamp.eclipse.botsing.constants.BotsingPluginConstants;
import eu.stamp.eclipse.botsing.dialog.BotsingAdvancedOptionsDialog;
import eu.stamp.eclipse.botsing.interfaces.IBotsingConfigurablePart;
import eu.stamp.eclipse.botsing.interfaces.IBotsingInfoSource;
import eu.stamp.eclipse.botsing.launch.BootsingLaunchInfo;
import eu.stamp.eclipse.botsing.launch.BostingJob;
import eu.stamp.eclipse.botsing.launch.BotsingPartialInfo;
import eu.stamp.eclipse.botsing.launch.ConfigurationsManager;

public class BotsingWizard extends Wizard
                      implements IBotsingConfigurablePart {

	protected BotsingWizardPage page; 
	
	private List<IBotsingConfigurablePart> configurableParts;
	
	private final ConfigurationsManager configurationsManager;
	
	public BotsingWizard() {
		configurableParts = new LinkedList<IBotsingConfigurablePart>();
		configurationsManager = new ConfigurationsManager();
	}
	
	@Override
	public void addPages() {
		BotsingAdvancedOptionsDialog dialog = 
				new BotsingAdvancedOptionsDialog(
						PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell());
		configurableParts.add(dialog);
		
		page = new BotsingWizardPage(this,dialog);
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
		List<BotsingPartialInfo> partialInfos = 
				new LinkedList<BotsingPartialInfo>();
		
        for(IBotsingConfigurablePart part : configurableParts)
        	if(part instanceof IBotsingInfoSource)
        	   partialInfos.add(((IBotsingInfoSource)part).getInfo());
        		
		BostingJob job = new BostingJob(
				new BootsingLaunchInfo(partialInfos),this);
		job.schedule();
		return true;
	}
	
	public void reconfigure(String configurationName) {
		configurationsManager.setConfigurationInUse(configurationName);
		ILaunchConfigurationWorkingCopy copy =
				configurationsManager.getCopy();
        this.load(copy);
	}
	
	public String[] getConfigurationNames() { 
		return configurationsManager.getConfigurationNames();
	}

	@Override
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy configuration) {
		for(IBotsingConfigurablePart part : configurableParts)
			part.appendToConfiguration(configuration);
	}

	@Override
	public void load(ILaunchConfigurationWorkingCopy configuration) {
		for(IBotsingConfigurablePart part : configurableParts)
			part.load(configuration);
	}
}
