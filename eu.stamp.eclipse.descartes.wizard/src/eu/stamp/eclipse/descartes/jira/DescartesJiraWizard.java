package eu.stamp.eclipse.descartes.jira;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.atlassian.jira.rest.client.api.RestClientException;

import eu.stamp.wp4.descartes.wizard.utils.DescartesWizardConstants;
import eu.stamp.descartes.jira.DescartesJiraTracker;

public class DescartesJiraWizard extends Wizard {
	
	private DescartesJiraTracker tracker;
	
	private String title;
	
	private String description;
	
	private String summary;
	
	private boolean errorFlag;
	
	public DescartesJiraWizard() throws StorageException {
		DescartesJiraAccountsManager manager = DescartesJiraAccountsManager.getInstance();
		errorFlag = manager.empty();
        if(!errorFlag)try { 
        	tracker = new DescartesJiraTracker(
                manager.getUrl(),manager.getUser(),manager.getPassword());
        } catch(RestClientException e) {
        	summary = manager.getUser() + " : " + manager.getUrl();
        	tracker = null;
        }
	}
	
	public boolean error() { return errorFlag; }
	
	public String getTitle() { return title; }
	
	public String getDescription() { 
		return description; 
		}
	
	public void setTitle(String title) { 
		this.title = title;
		if(tracker != null) tracker.setTitle(title); 
		}
	
	public void parseDescription(String description) {
		this.description = description;
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
    	if(tracker == null) {
    		Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
		MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
				,"Rest client error","The account " + summary + 
				" is not correct, please check it in the Descartes Jira preference page in Window > Preferences");
				}
    		});
    		return true;
    	}
		if(!errorFlag) {
			String result = tracker.createIssue();
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openInformation(PlatformUI
							.getWorkbench().getActiveWorkbenchWindow().getShell()
							,"Jira Issue created",result);
				}
			});
		}
		return true;
	}
}