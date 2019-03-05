package eu.stamp.eclipse.descartes.jira;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.stamp.descartes.jira.DescartesJiraTracker;

public class DescartesJiraIssuePage1 extends WizardPage{

	protected DescartesJiraIssuePage1(String pageName) { super(pageName); }

	@Override
	public void createControl(Composite parent) {
		
		// create the composite
		Composite composite = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();    // the layout of composite
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		composite.setLayout(layout);
		
		DescartesJiraTracker tracker = ((DescartesJiraWizard)getWizard()).getTracker();
		
		// Project
		Label projectLabel = new Label(composite,SWT.NONE);
		projectLabel.setText("Select a project : ");
		
		Combo projectCombo = new Combo(composite,SWT.BORDER | SWT.READ_ONLY);
		Set<String> projects = tracker.getProjects();
		String initialSelection = null;
		for(String project : projects) {
			if(initialSelection == null) initialSelection = project;
			projectCombo.add(project);
		}
		tracker.setProject(initialSelection);
		GridDataFactory.fillDefaults().span(2,1).grab(true,false).applyTo(projectCombo);
		projectCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tracker.setProject(projectCombo.getText());
			}
		});
		
		// Title
		Label titleLabel = new Label(composite,SWT.NONE);
		titleLabel.setText("Title : ");
		GridDataFactory.swtDefaults().indent(0,6).applyTo(titleLabel);
		
		Text titleText = new Text(composite,SWT.BORDER);
		GridDataFactory.fillDefaults().span(2,1).grab(true,false)
		.indent(0,6).applyTo(titleText);
		titleText.setText(tracker.getTitle());
		titleText.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
				tracker.setTitle(titleText.getText());
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
		
		// Description
		Label descriptionLabel = new Label(composite,SWT.NONE);
		descriptionLabel.setText("Description : ");
		GridDataFactory.swtDefaults().span(3,1).indent(0,6).applyTo(descriptionLabel);
		
		Text descriptionText = new Text(composite,SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().span(3,3).grab(true,true)
		.minSize(100,120).applyTo(descriptionText);
		descriptionText.setText(tracker.getDescription());
		descriptionText.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
				tracker.setDescription(descriptionText.getText());
			}	
		});
		
		// required
		setControl(composite);
		setPageComplete(true);
	}
}