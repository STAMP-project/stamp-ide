package eu.stamp.wp4.descartes.wizard;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.stamp.wp4.descartes.wizard.configuration.DescartesWizardConfiguration;
import eu.stamp.wp4.descartes.wizard.configuration.IDescartesWizardPart;

public class DescartesWizardPage1 extends WizardPage implements IDescartesWizardPart{
	
	private DescartesWizard wizard;
	private String[] mutatorsTexts;

	public DescartesWizardPage1(DescartesWizard wizard) {
		super("First page");
		this.wizard = wizard;
		setTitle("First page");
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
		/*
		 *   ROW 1
		 */
		Label projectLabel = new Label(composite,SWT.NONE);
		projectLabel.setText("path of the project : ");
		GridDataFactory.swtDefaults().grab(false, false).applyTo(projectLabel);
		
		projectText = new Text(composite,SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(projectLabel);
		projectText.setText(projectPath);
		
		Button projectButton = new Button(composite,SWT.PUSH);
		projectButton.setText("Select a Project");
		GridDataFactory.swtDefaults().applyTo(projectButton);
		/*
		 *   ROW 2
		 */
		Label mutatorsLabel = new Label(composite,SWT.NONE);
		mutatorsLabel.setText("Mutators");
		GridDataFactory.fillDefaults().span(3, 1).applyTo(mutatorsLabel);
		/*
		 *   ROW 3
		 */
        Composite gr1 = new Composite(composite,SWT.BORDER);
        GridData gd = new GridData(SWT.FILL,SWT.FILL,true,true);
        gd.horizontalSpan = 3;
        gd.verticalSpan = 5;
        gd.minimumWidth = 250;
        gr1.setLayoutData(gd);
        gr1.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        GridLayout Layforgr1 = new GridLayout();
        gr1.setLayout(Layforgr1);
        

		Label[] mutatorsLabels = new Label[mutatorsTexts.length];
		for(int i = 0; i < mutatorsLabels.length; i++) {
			mutatorsLabels[i] = new Label(gr1,SWT.NONE);
			mutatorsLabels[i].setText(mutatorsTexts[i]);
		}
        
		// required to avoid an error in the System
		setControl(composite);
		setPageComplete(true);	
	
		}

	@Override
	public void updateDescartesWizardPart(DescartesWizardConfiguration wConf) {
		projectPath = wConf.getProjectPath();
		if(projectText != null) { if(!projectText.isDisposed()) projectText.setText(projectPath);}
		mutatorsTexts = wConf.getMutatorsTexts();
	}
	@Override
	public void updateWizardReference(DescartesWizard wizard) {
		this.wizard = wizard;
	}

}
