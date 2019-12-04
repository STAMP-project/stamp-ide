package eu.stamp.eclipse.botsing.model.generation.wizard;

import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import eu.stamp.eclipse.botsing.model.generation.constants.ModelGenerationLaunchConstants;
import eu.stamp.eclipse.botsing.model.generation.controls.ModelGenerationControlsFactory;

public class ModelGenerationWizardPage extends WizardPage {
	
	private final Map<String,String> map;

	protected ModelGenerationWizardPage(Map<String,String> map) {
		super("Model Generation configuration");
		this.map = map;
	}

	@Override
	public void createControl(Composite parent) {
		
		// create the composite
		Composite composite = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();    // the layout of composite
		int n = 4;
		layout.numColumns = n;
		layout.makeColumnsEqualWidth = true;
		composite.setLayout(layout);
		
		// class path
		ModelGenerationControlsFactory.getFactory().setMap(map).setKey(
				ModelGenerationLaunchConstants.PROJECT_CLASS_PATH).setLabelText("Class path : ")
		.createText(composite);
		
		// project prefix
		ModelGenerationControlsFactory.getFactory().setMap(map)
		  .setKey(ModelGenerationLaunchConstants.PROJECT_PREFIX).setLabelText("Project Prefix : ")
		  .createText(composite);
		
		// output directory
		ModelGenerationControlsFactory.getFactory().setMap(map)
		.setKey(ModelGenerationLaunchConstants.OUT_DIR).setLabelText("Output directory : ")
		.createText(composite);
		
		setControl(composite);
		setPageComplete(true);
	}
}
