/*******************************************************************************
 * Copyright (c) 2019 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * 	Ricardo Jose Tejada Garcia (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
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
import eu.stamp.wp4.descartes.wizard.utils.IssuesHtmlProcessor;
import eu.stamp.descartes.jira.DescartesJiraTracker;
/**
 * A wizard to open a Jira ticket, the wizard will be opened by the
 * link in the Descartes Issues view
 * @see eu.stamp.wp4.descartes.view.DescartesAbstractView
 * @see eu.stamp.wp4.descartes.view.DescartesIssuesView
 */
public class DescartesJiraWizard extends Wizard {
	/**
	 * the tracker uses the Jira Java Api to provide information
	 * about the accounts and to create issues
	 */
	private DescartesJiraTracker tracker;
	
	private String title,description,summary;
	
	private boolean errorFlag;
	
	public DescartesJiraWizard() throws StorageException {
		// load data
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
	/**
	 * @return : the title of the issue
	 */
	public String getTitle() { return title; }
	/**
	 * @return : the description of the issue
	 */
	public String getDescription() { 
		return description; 
		}
	/**
	 * @param title : a title for the issue
	 */
	public void setTitle(String title) { 
		this.title = title;
		if(tracker != null) tracker.setTitle(title); 
		}
	/**
	 * convert an HTML description to Jira wiki mark-up
	 * @param description : the HTML string describing the issue
	 **/
	public void parseDescription(String description) {
		this.description = description;
		if(tracker != null) tracker.setDescription(
				IssuesHtmlProcessor.h2mu(description));
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
	public boolean needsPreviousAndNextButtons() { return false; }
	
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