package eu.stamp.eclipse.descartes.jira;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SegmentEvent;
import org.eclipse.swt.events.SegmentListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.atlassian.jira.rest.client.api.RestClientException;

import eu.stamp.descartes.jira.DescartesJiraTracker;
import eu.stamp.wp4.descartes.wizard.utils.IssuesHtmlProcessor;

public class DescartesJiraIssuePage1 extends WizardPage{
	
	private TrackerPseudoProxy trackerProxy;
	
	protected DescartesJiraIssuePage1(String pageName) { super(pageName); }
	
	private void checkPage(Map<String,Control> map) {
		Set<String> names = map.keySet();
		boolean ok = true;
		for(String name : names) ok = ok && checkNoEmpty(map.get(name),name);
		setPageComplete(ok);
		if(ok) setErrorMessage(null); // clear
	}
	
	private boolean checkNoEmpty(Control control, String name) {
	  String content = "0";
	  if(control instanceof Text) content = ((Text)control).getText();
	  else if(control instanceof Combo) content = ((Combo)control).getText();
	  if(content == null || content.isEmpty()) {
		  setErrorMessage(name + " is empty");
		  return false;
	  }
	  return true;
	}

	@Override
	public void createControl(Composite parent) {
		
		// create the composite
		Composite composite = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();    // the layout of composite
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		composite.setLayout(layout);
		
		DescartesJiraAccountsManager manager = DescartesJiraAccountsManager.getInstance();
		
		final DescartesJiraWizard wizard = (DescartesJiraWizard)getWizard();
		if(manager.empty()) {
			Label errorLabel = new Label(composite,SWT.NONE);
			errorLabel.setText("No Jira accounts found, please go to the Descartes Jira "
					+ "preferences page in Window > Preferences and set a new jira account");
			setControl(composite);
			setPageComplete(true);
			return;
		}
		
		trackerProxy = new TrackerPseudoProxy(wizard.getTracker());
		Map<String,Control> controlsToCheck = new HashMap<String,Control>();
		
		// Accounts
		Label accountsLabel = new Label(composite,SWT.BORDER);
		accountsLabel.setText("Jira account : ");
		
		Combo accountCombo = new Combo(composite,SWT.READ_ONLY | SWT.BORDER);
        GridDataFactory.fillDefaults().span(2,1).grab(true,false).applyTo(accountCombo);
        	List<String> accounts = manager.getAccounts();
        	for(String account : accounts) accountCombo.add(account);
        	if(accounts.size() > 0) accountCombo.select(0);
		    controlsToCheck.put("Accounts combo",accountCombo);
        	
		// Project
		Label projectLabel = new Label(composite,SWT.NONE);
		projectLabel.setText("Select a project : ");
		GridDataFactory.swtDefaults().indent(0,8).applyTo(projectLabel);
		
		Combo projectCombo = new Combo(composite,SWT.BORDER | SWT.READ_ONLY);
		String initialSelection = "";
		Set<String> projects = trackerProxy.getProjects();
		for(String project : projects) {
			if(initialSelection.isEmpty()) initialSelection = project;
			projectCombo.add(project);
		}
		controlsToCheck.put("Project combo",projectCombo);
		trackerProxy.setProject(initialSelection);
		GridDataFactory.fillDefaults().span(2,1).grab(true,false)
		.indent(0,8).applyTo(projectCombo);
		projectCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				trackerProxy.setProject(projectCombo.getText());
				checkPage(controlsToCheck);
			}
		});
	   trackerProxy.setProject(projectCombo.getText());
		
		// Title
		Label titleLabel = new Label(composite,SWT.NONE);
		titleLabel.setText("Title : ");
		GridDataFactory.swtDefaults().indent(0,6).applyTo(titleLabel);
		
		Text titleText = new Text(composite,SWT.BORDER);
		GridDataFactory.fillDefaults().span(2,1).grab(true,false)
		.indent(0,6).applyTo(titleText);
		titleText.setText(trackerProxy.getTitle());
		controlsToCheck.put("Title",titleText);
		titleText.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
				trackerProxy.setTitle(titleText.getText());
				checkPage(controlsToCheck);
			}
		});
		
		// IssueType
		Label typeLabel = new Label(composite,SWT.NONE);
		typeLabel.setText("Issue type : ");
		GridDataFactory.swtDefaults().indent(0,6).applyTo(typeLabel);
		
		Combo typeCombo = new Combo(composite,SWT.READ_ONLY | SWT.BORDER);
		Set<String> types = trackerProxy.getIssueTypes();
		for(String type : types) typeCombo.add(type);
		typeCombo.setText("Bug");
		GridDataFactory.fillDefaults().span(2,1).grab(true,false)
		.indent(0,6).applyTo(typeCombo);
		typeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				trackerProxy.setIssueType(typeCombo.getText());
			}
		});
		trackerProxy.setIssueType(typeCombo.getText());
		
		// Description
		Label descriptionLabel = new Label(composite,SWT.NONE);
		descriptionLabel.setText("Description : ");
		GridDataFactory.swtDefaults().span(3,1).indent(0,6).applyTo(descriptionLabel);
		
		Text descriptionText = new Text(composite,SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		controlsToCheck.put("Description",descriptionText);
		GridDataFactory.fillDefaults().span(3,3).grab(true,true)
		.minSize(100,120).applyTo(descriptionText);
		descriptionText.setText(trackerProxy
				.getDescription());
		descriptionText.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
				trackerProxy.setDescription(descriptionText.getText());
				checkPage(controlsToCheck);
			}	
		});
		trackerProxy.setDescription(descriptionText.getText());
		
		accountCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				manager.setSelection(accountCombo.getText());
				// update page
				if(manager.getUrl() != null && !manager.getUrl().isEmpty())
					try{
					trackerProxy = new TrackerPseudoProxy(
					new DescartesJiraTracker(manager.getUrl(),manager.getUser(),manager.getPassword()));
				} catch(RestClientException exception) {
			      MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			    		  .getShell(),"Rest client exception","The account " + 
			      manager.getUser() + " : " + manager.getUrl() + 
			      " is not correct, please check it in the Descartes Jira preferences page in Window > Preferences");
				}
				Set<String> projects = trackerProxy.getProjects();
				projectCombo.removeAll();
				for(String project : projects) projectCombo.add(project);
				if(projects.size() > 0) projectCombo.select(0);
				
				trackerProxy.setTitle(titleText.getText());
			    trackerProxy.setDescription(descriptionText.getText());
			    trackerProxy.setIssueType(typeCombo.getText());
			    trackerProxy.setProject(projectCombo.getText());
				wizard.setTracker(trackerProxy.tracker); // remember java is pass by value
				
			    checkPage(controlsToCheck);
			}
		});
		accountCombo.notifyListeners(SWT.Selection,new Event());
		
		if(descriptionText.getText() == null || descriptionText.getText().isEmpty()) {
			String text = wizard.getDescription();
			if(text != null) descriptionText.setText(
					IssuesHtmlProcessor.h2mu(text));
					//.replaceAll("[Back]",""));
		}
		if(titleText.getText() == null || titleText.getText().isEmpty()) {
			String text = wizard.getTitle();
			if(text != null) titleText.setText(text);
		}
		// required
		setControl(composite);
		setPageComplete(true);
	}
	
	// Nested classes
	private class TrackerPseudoProxy {

		DescartesJiraTracker tracker;
		
		TrackerPseudoProxy(DescartesJiraTracker tracker){
			this.tracker = tracker;
		}
		
		/*
		 *   Getters
		 */
		Set<String> getIssueTypes() { 
			if(tracker == null) return new HashSet<String>();
			return tracker.getIssueTypes();
		}
		
		Set<String> getProjects() { 
			if(tracker == null) return new HashSet<String>();
			return tracker.getProjects();
		}
		
		String getTitle() { 
			if(tracker == null) return ""; 
			return tracker.getTitle();
			}
		
		String getDescription() { 
			if(tracker == null) return "";
			return ""; 
			}
		
		/*
		 *   Setters
		 */
		void setIssueType(String type) {
			if(tracker != null) tracker.setIssueType(type);
		}
		
		void setProject(String projectName) {
			if(tracker != null) tracker.setProject(projectName);
		}
		
		void setTitle(String title) {
			if(tracker != null) tracker.setTitle(title);
		}
		void setDescription(String description) {
			if(tracker != null) tracker.setDescription(
					IssuesHtmlProcessor.h2mu(description));
		}
	}
}