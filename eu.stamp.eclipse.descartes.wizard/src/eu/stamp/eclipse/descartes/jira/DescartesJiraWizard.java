package eu.stamp.eclipse.descartes.jira;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;

import eu.stamp.wp4.descartes.wizard.utils.DescartesWizardConstants;
import eu.stamp.descartes.jira.DescartesJiraTracker;

public class DescartesJiraWizard extends Wizard {
	
	private DescartesJiraTracker tracker;
	
	private DescartesJiraAccountsManager2 manager;
	
	private boolean errorFlag;
	
	public DescartesJiraWizard() throws StorageException {
		manager = new DescartesJiraAccountsManager2();
		errorFlag = manager.empty();
        if(!errorFlag) tracker = new DescartesJiraTracker(
        		manager.getUrl(),manager.getUser(),manager.getPassword());
	}
	
	public boolean error() { return errorFlag; }
	
	public DescartesJiraAccountsManager2 getmanager() { return manager; }
	
	public void setManager(DescartesJiraAccountsManager2 manager) {
		this.manager = manager;
	}
	
	public void setTitle(String title) { 
		if(tracker != null) tracker.setTitle(title); 
		}
	
	public void parseDescription(String description) {
		if(tracker != null) tracker.parseDescription(description);
	}
	
	public DescartesJiraTracker getTracker() { return tracker; }
	
	public void setTracker(DescartesJiraTracker tracker) {
		this.tracker = tracker;
	}
	
	@Override
	public void addPages() {
		addPage(new DescartesJiraIssuePage1("Create jira ticket"));
	}
	
	@Override
	public String getWindowTitle() { return "Jira Issue"; }
	
	@Override
	public Image getDefaultPageImage() {
		final URL iconStampURL = FileLocator.find(Platform.getBundle(
				DescartesWizardConstants.DESCARTES_PLUGIN_ID),new Path("images/stamp.png"),null);
		ImageDescriptor descriptor = ImageDescriptor.createFromURL(iconStampURL);
		Image image = descriptor.createImage();
		return image;
	}
	
	@Override
	public boolean performFinish() {
    	if(!errorFlag) tracker.createIssue();
		return true;
	}
}