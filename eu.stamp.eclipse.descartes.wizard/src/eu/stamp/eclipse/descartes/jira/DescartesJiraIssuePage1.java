package eu.stamp.eclipse.descartes.jira;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import eu.stamp.descartes.jira.DescartesJiraTracker;

public class DescartesJiraIssuePage1 extends WizardPage{
	
	private DescartesJiraTracker tracker;
	
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
		
		tracker = wizard.getTracker();
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
		Set<String> projects = tracker.getProjects();
		String initialSelection = null;
		for(String project : projects) {
			if(initialSelection == null) initialSelection = project;
			projectCombo.add(project);
		}
		controlsToCheck.put("Project combo",projectCombo);
		tracker.setProject(initialSelection);
		GridDataFactory.fillDefaults().span(2,1).grab(true,false)
		.indent(0,8).applyTo(projectCombo);
		projectCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tracker.setProject(projectCombo.getText());
				checkPage(controlsToCheck);
			}
		});
		tracker.setProject(projectCombo.getText());
		
		// Title
		Label titleLabel = new Label(composite,SWT.NONE);
		titleLabel.setText("Title : ");
		GridDataFactory.swtDefaults().indent(0,6).applyTo(titleLabel);
		
		Text titleText = new Text(composite,SWT.BORDER);
		GridDataFactory.fillDefaults().span(2,1).grab(true,false)
		.indent(0,6).applyTo(titleText);
		titleText.setText(tracker.getTitle());
		controlsToCheck.put("Title",titleText);
		titleText.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
				tracker.setTitle(titleText.getText());
				checkPage(controlsToCheck);
			}
		});
		
		// IssueType
		Label typeLabel = new Label(composite,SWT.NONE);
		typeLabel.setText("Issue type : ");
		GridDataFactory.swtDefaults().indent(0,6).applyTo(typeLabel);
		
		Combo typeCombo = new Combo(composite,SWT.READ_ONLY | SWT.BORDER);
		Set<String> types = tracker.getIssueTypes();
		for(String type : types) typeCombo.add(type);
		typeCombo.setText("Bug");
		GridDataFactory.fillDefaults().span(2,1).grab(true,false)
		.indent(0,6).applyTo(typeCombo);
		typeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tracker.setIssueType(typeCombo.getText());
			}
		});
		tracker.setIssueType(typeCombo.getText());
		
		// Description
		Label descriptionLabel = new Label(composite,SWT.NONE);
		descriptionLabel.setText("Description : ");
		GridDataFactory.swtDefaults().span(3,1).indent(0,6).applyTo(descriptionLabel);
		
		Text descriptionText = new Text(composite,SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		controlsToCheck.put("Description",descriptionText);
		GridDataFactory.fillDefaults().span(3,3).grab(true,true)
		.minSize(100,120).applyTo(descriptionText);
		descriptionText.setText(tracker.getDescription());
		descriptionText.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
				tracker.setDescription(descriptionText.getText());
				checkPage(controlsToCheck);
			}	
		});
		tracker.setDescription(descriptionText.getText());
		
		accountCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				manager.setSelection(accountCombo.getText());
				// update page
				if(manager.getUrl() != null && !manager.getUrl().isEmpty())
				tracker = new DescartesJiraTracker(manager.getUrl(),manager.getUser(),manager.getPassword());
				Set<String> projects = tracker.getProjects();
				projectCombo.removeAll();
				for(String project : projects) projectCombo.add(project);
				if(projects.size() > 0) projectCombo.select(0);
				
				tracker.setTitle(titleText.getText());
			    tracker.setDescription(descriptionText.getText());
			    tracker.setIssueType(typeCombo.getText());
			    tracker.setProject(projectCombo.getText());
				wizard.setTracker(tracker); // remember java is pass by value
				
			    checkPage(controlsToCheck);
			}
		});
		accountCombo.notifyListeners(SWT.Selection,new Event());
		
		// required
		setControl(composite);
		setPageComplete(true);
	}
}