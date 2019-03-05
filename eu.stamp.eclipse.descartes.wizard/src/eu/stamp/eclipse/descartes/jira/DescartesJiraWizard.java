package eu.stamp.eclipse.descartes.jira;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;

import eu.stamp.wp4.descartes.wizard.utils.DescartesWizardConstants;
import eu.stamp.descartes.jira.DescartesJiraTracker;

public class DescartesJiraWizard extends Wizard {
	
	private ISecurePreferences preferences;
	
	private DescartesJiraTracker tracker;
	
	public DescartesJiraWizard() throws StorageException {
		// preferences
		preferences = SecurePreferencesFactory.getDefault()
				.node(DescartesJiraPreferencePage.PREFERENCES_KEY);
		// tracker
        tracker = new DescartesJiraTracker(
        		preferences.get(DescartesJiraPreferencePage.URL_KEY,""),
        		preferences.get(DescartesJiraPreferencePage.USER_KEY,""),
        		preferences.get(DescartesJiraPreferencePage.PASSWORD_KEY,""));
	}
	
	public void setTitle(String title) { tracker.setTitle(title); }
	
	public void parseDescription(String description) {
		tracker.parseDescription(description);
	}
	
	public DescartesJiraTracker getTracker() { return tracker; }
	
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
    	tracker.createIssue();
		return true;
	}
}