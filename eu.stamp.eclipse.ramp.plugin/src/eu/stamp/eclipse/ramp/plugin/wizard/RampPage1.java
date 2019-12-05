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
package eu.stamp.eclipse.ramp.plugin.wizard;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.Workbench;

import eu.stamp.eclipse.botsing.model.generation.load.GenerationConfigurationLoader;
import eu.stamp.eclipse.botsing.model.generation.wizard.ModelGenerationWizard;
import eu.stamp.eclipse.ramp.plugin.constants.RampLaunchConstants;
import eu.stamp.eclipse.ramp.plugin.controls.ControlsFactory;
import eu.stamp.eclipse.ramp.plugin.job.RampConfiguration;

@SuppressWarnings("restriction")
public class RampPage1 extends WizardPage {
	
	private final RampConfiguration evosuiteConfiguration;
	
	private Text classpathText;
	
	private GenerationConfigurationLoader modelLoader;
	
	private final RampWizard wizard;

	protected RampPage1(String pageName, RampConfiguration evosuiteConfiguration,RampWizard wizard) {
		super(pageName);
		this.evosuiteConfiguration = evosuiteConfiguration;
		this.wizard = wizard;
	}

	@Override
	public void createControl(Composite parent) {
		
		// prepare composite
		Composite composite = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);
		
		evosuiteConfiguration.setProperty(RampLaunchConstants.CLASS_PROPERTY,wizard.getClassesList());
        ControlsFactory.getFactory(evosuiteConfiguration).setPropertyKey(
        		RampLaunchConstants.CLASS_PROPERTY).setLabelText("Class : ")
                .createList(composite);
        
        classpathText = ControlsFactory.getFactory(evosuiteConfiguration).setPropertyKey(
        		RampLaunchConstants.PROJECT_CP).setLabelText("Project class path : ")
                .createText(composite);
        
        String classPath = classpathText.getText();
        if(classPath != null && !classPath.isEmpty()) classpathText.setEnabled(false);
        
        ControlsFactory.getFactory(evosuiteConfiguration).setPropertyKey(
        		RampLaunchConstants.SEARCH_BUDGET).setLabelText("Search budget : ")
                .setSpinnerMin(10).setSpinnerMax(100).setSpinnerStep(5)
                .createSpinner(composite);
        
        ControlsFactory.getFactory(evosuiteConfiguration).setPropertyKey(
        		RampLaunchConstants.SEED_CLONE).setLabelText("Seed clone : ")
                .setSpinnerDigits(1).setSpinnerMin(1).setSpinnerMax(10)
                .createSpinner(composite);
        
        // Check button for enabling the use of the models, the listener is after the model path field
        Button useModelsButton = new Button(composite,SWT.CHECK);
        useModelsButton.setTextDirection(SWT.LEFT);
        useModelsButton.setText("Use models : ");
        useModelsButton.setToolTipText("Enable the use of the Botsing generated models to improve Evosuite launch");
        GridDataFactory.fillDefaults().span(3,1).grab(true,false).indent(0,6).applyTo(useModelsButton);
        
        Text modelPathText = ControlsFactory.getFactory(evosuiteConfiguration).setPropertyKey(
        		RampLaunchConstants.MODEL_PATH).setLabelText("Model path : ")
        .createText(composite);
        
        // listener for the check use models
        useModelsButton.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent event) {
        		if(useModelsButton.getSelection()) {
        			modelPathText.setEnabled(true);
        			modelPathText.setText("model/");
        		} else {
        			modelPathText.setEnabled(false);
        			modelPathText.setText("");
        		}
        		modelPathText.notifyListeners(SWT.Segments,new Event());
        	}
        });
        useModelsButton.setSelection(false);
        useModelsButton.notifyListeners(SWT.Selection,new Event());
        
        Link modelLink = new Link(composite,SWT.None);
        modelLink.setText("<A>Model generation</A>");
        modelLink.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent event) {
        		Display.getDefault().syncExec(new Runnable() {
        			@Override
        			public void run() {
        			  Shell shell = Workbench.getInstance().getActiveWorkbenchWindow().getShell();
        			  ModelGenerationWizard modelWizard = 
        					  new ModelGenerationWizard(evosuiteConfiguration.getProject(),classpathText.getText());
        			  if(modelLoader != null) modelWizard.setLoader(modelLoader);
        			  WizardDialog diag = new WizardDialog(shell,modelWizard);
        			  diag.setBlockOnOpen(true);
        			  diag.open();
        			  evosuiteConfiguration.setModelGenerationJob(modelWizard.getJob());
        			  modelLoader = modelWizard.getLoader();
        			}
        		});
        	}
        });
		setControl(composite);
		setPageComplete(true);
	}
}
