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
package eu.stamp.eclipse.botsing.model.generation.wizard;

import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;

import eu.stamp.eclipse.botsing.model.generation.constants.ModelGenerationLaunchConstants;
import eu.stamp.eclipse.botsing.model.generation.controls.ModelGenerationControlsFactory;
import eu.stamp.eclipse.botsing.model.generation.load.GenerationConfigurationLoader;

public class ModelGenerationWizardPage extends WizardPage {
	
	private final Map<String,String> map;
	
	private final ModelGenerationWizard wizard;

	protected ModelGenerationWizardPage(Map<String,String> map,ModelGenerationWizard wizard) {
		super("Model Generation configuration");
		this.map = map;
		this.wizard = wizard;
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
		
		GenerationConfigurationLoader loader = wizard.getLoader();
		
		// class path
		Text classPathText = ModelGenerationControlsFactory.getFactory().setMap(map).setKey(
				ModelGenerationLaunchConstants.PROJECT_CLASS_PATH).setLabelText("Class path : ")
		.createText(composite,loader);
		
		if(!classPathText.getText().isEmpty()) {
			classPathText.setEnabled(false);
			classPathText.notifyListeners(SWT.Segments,new Event());
		}
		
		// project prefix
		ModelGenerationControlsFactory.getFactory().setMap(map)
		  .setKey(ModelGenerationLaunchConstants.PROJECT_PREFIX).setLabelText("Project Prefix : ")
		  .createText(composite,loader);
		
		// output directory
		ModelGenerationControlsFactory.getFactory().setMap(map)
		.setKey(ModelGenerationLaunchConstants.OUT_DIR).setLabelText("Output directory : ")
		.createText(composite,loader);
		
		wizard.getLoader().load();
		
		setControl(composite);
		setPageComplete(true);
	}
}
