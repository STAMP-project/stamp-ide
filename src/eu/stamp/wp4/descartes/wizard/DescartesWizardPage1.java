package eu.stamp.wp4.descartes.wizard;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.stamp.wp4.descartes.wizard.configuration.DescartesWizardConfiguration;
import eu.stamp.wp4.descartes.wizard.configuration.IDescartesWizardPart;

public class DescartesWizardPage1 extends WizardPage implements IDescartesWizardPart{
	
	DescartesWizard wizard;

	public DescartesWizardPage1(DescartesWizard wizard) {
		super("First page");
		this.wizard = wizard;
	}
	
	// widgets
	Text projectText;
	
    private String projectPath;
	
	@Override
	public void createControl(Composite parent) {
		
		// create the composite
		Composite composite = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();    // the layout of composite
		layout.numColumns = 3;
		composite.setLayout(layout);
		
		Label projectLabel = new Label(composite,SWT.NONE);
		projectLabel.setText("path of the project");
		GridDataFactory.swtDefaults().applyTo(projectLabel);
		
		projectText = new Text(composite,SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(projectLabel);
		projectText.setText(projectPath);
		
		Button projectButton = new Button(composite,SWT.PUSH);
		GridDataFactory.swtDefaults().applyTo(projectButton);
		
		// required to avoid an error in the System
		setControl(composite);
		setPageComplete(true);	
		}

	@Override
	public void updateDescartesWizardPart(DescartesWizardConfiguration wConf) {
		projectPath = wConf.getProjectPath();
		if(projectText != null) { if(!projectText.isDisposed()) projectText.setText(projectPath);}
	}
	@Override
	public void updateWizardReference(DescartesWizard wizard) {
		this.wizard = wizard;
	}

}
