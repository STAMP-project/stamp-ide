/*******************************************************************************
 * Copyright (c) 2018 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ricardo José Tejada García (Atos) - main developer
 * Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.eclipse.botsing.wizard;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

import eu.stamp.eclipse.botsing.dialog.BotsingAdvancedOptionsDialog;
import eu.stamp.eclipse.botsing.interfaces.IBotsingConfigurablePart;
import eu.stamp.eclipse.botsing.interfaces.IBotsingInfoSource;
import eu.stamp.eclipse.botsing.interfaces.IProjectRelated;
import eu.stamp.eclipse.botsing.launch.BotsingPartialInfo;
import eu.stamp.eclipse.botsing.properties.AbstractBotsingProperty;
import eu.stamp.eclipse.botsing.properties.BotsingSpinnerProperty;
import eu.stamp.eclipse.botsing.properties.ClassPathProperty;
import eu.stamp.eclipse.botsing.properties.OutputTraceProperty;
import eu.stamp.eclipse.botsing.properties.StackTraceProperty;
import eu.stamp.eclipse.botsing.properties.TestDirectoryProperty;

public class BotsingWizardPage extends WizardPage 
             implements IBotsingConfigurablePart, IProjectRelated, IBotsingInfoSource {
    
	/**
	 * List with the properties in this page
	 */
	private List<AbstractBotsingProperty> botsingProperties;
	
	private String configurationName;
    
	/**
	 * Access to the wizard object is required in order to load configurations
	 */
	private BotsingWizard wizard;
	
	private final BotsingAdvancedOptionsDialog dialog;
	
	protected BotsingWizardPage(
			BotsingWizard wizard,BotsingAdvancedOptionsDialog dialog) {
		super("First page"); 
		setTitle("First Page");
		setDescription("Botsing Configuration");
		configurationName = "new_configuration";
		this.wizard = wizard;
		this.dialog = dialog;
		botsingProperties = new LinkedList<AbstractBotsingProperty>();
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
	/*
	 *  Load Configuration
	 */
    Label loadLabel = new Label(composite,SWT.NONE);
    loadLabel.setText("Load configuration : ");
    
    Combo loadCombo = new Combo(composite,SWT.READ_ONLY | SWT.BORDER);
    String[] names = wizard.getConfigurationNames();
    for(String name : names) loadCombo.add(name);
    loadCombo.add("");
    loadCombo.setEnabled(false);
    
    GridData loadComboData = new GridData(SWT.FILL,SWT.FILL,true,false);
    loadComboData.horizontalSpan = n - 1;
    loadCombo.setLayoutData(loadComboData);
    
    loadCombo.addSelectionListener(new SelectionAdapter() {
    	@Override
    	public void widgetSelected(SelectionEvent e) {
    		configurationName = loadCombo.getText();
    		wizard.reconfigure(loadCombo.getText());
    	}
    });
    
	/*
	 *  New configuration
	 */
	Label newConfigurationLabel = new Label(composite,SWT.NONE);
	newConfigurationLabel.setText("Create a new configuration : ");
	
	Button newConfigurationButton = new Button(composite,SWT.CHECK);
	newConfigurationButton.setSelection(true);
	
	Text newConfigurationText = new Text(composite,SWT.BORDER);
	newConfigurationText.setText("new_configuration");
	
	GridData newConfigurationTextData = 
			new GridData(SWT.FILL,SWT.FILL,true,false);
	newConfigurationTextData.horizontalSpan = n - 2;
	newConfigurationText.setLayoutData(newConfigurationTextData);

	newConfigurationText.addKeyListener(new KeyListener() {
		@Override
		public void keyPressed(KeyEvent e) {}

		@Override
		public void keyReleased(KeyEvent e) {
			configurationName = newConfigurationText.getText();
		}	
	});
	
	newConfigurationButton.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			boolean selected = newConfigurationButton.getSelection();
			if(selected) {
				newConfigurationText.setEnabled(true);
				newConfigurationText.setText("new_configuration");
				loadCombo.setText("");
				loadCombo.setEnabled(false);
			} else {
				loadCombo.setEnabled(true);
				newConfigurationText.setEnabled(false);
				newConfigurationText.setText("");
			}
		}
	});
	
	 //Field for the tests directory
	TestDirectoryProperty testDirectory = 
			new TestDirectoryProperty("","-Dtest_dir","Test directory : ");
	testDirectory.createControl(composite,false); // no only read
	botsingProperties.add(testDirectory);
	
	// Field for the log directory
	StackTraceProperty stackProperty = 
			new StackTraceProperty("","-crash_log","Log file : ");
	stackProperty.createControl(composite);
	botsingProperties.add(stackProperty);
	
	// Spinner for the frame level
	BotsingSpinnerProperty frameLevel = 
    new BotsingSpinnerProperty("2","-target_frame","Frame level : ",true);
	frameLevel.createControl(composite);
	botsingProperties.add(frameLevel);
    
	// Field for the classpath
	ClassPathProperty classPathProperty = 
			new ClassPathProperty("","-projectCP","Class Path : ");
	classPathProperty.createControl(composite);
	botsingProperties.add(classPathProperty);
	
	// Field for the trace output file
	OutputTraceProperty outputTraceProperty =
			new OutputTraceProperty("","Output folder : ",false);
	outputTraceProperty.createControl(composite);
	botsingProperties.add(outputTraceProperty);
	
	// dialog link
	Link link = new Link(composite,SWT.NONE);
	link.setText("<A>Advanced options</A>");
	link.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			dialog.open();
		}
	});

	// required
	setControl(composite);
	setPageComplete(true);	
	}

	@Override
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy configuration) {
		for(AbstractBotsingProperty property : botsingProperties)
		     property.appendToConfiguration(configuration);
	}

	@Override
	public void load(ILaunchConfigurationWorkingCopy configuration) {
		for(AbstractBotsingProperty property : botsingProperties)
			property.load(configuration);
	}

	@Override
	public void projectChanged(IJavaProject newProject) {
		for(AbstractBotsingProperty property : botsingProperties)
			if(property instanceof IProjectRelated)
				((IProjectRelated)property).projectChanged(newProject);
	}

	@Override
	public BotsingPartialInfo getInfo() {
		return new BotsingPartialInfo(configurationName,botsingProperties);
	}
}
