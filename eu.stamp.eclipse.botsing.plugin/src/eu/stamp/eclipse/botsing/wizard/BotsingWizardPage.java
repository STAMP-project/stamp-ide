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

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.wizard.WizardDialog;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.richclientgui.toolbox.validation.IFieldErrorMessageHandler;
import com.richclientgui.toolbox.validation.string.StringValidationToolkit;

import eu.stamp.eclipse.botsing.constants.BotsingPluginConstants;
import eu.stamp.eclipse.botsing.dialog.BotsingAdvancedOptionsDialog;
import eu.stamp.eclipse.botsing.interfaces.IBotsingConfigurablePart;
import eu.stamp.eclipse.botsing.interfaces.IBotsingInfoSource;
import eu.stamp.eclipse.botsing.interfaces.IBotsingProperty;
import eu.stamp.eclipse.botsing.interfaces.IProjectRelated;
import eu.stamp.eclipse.botsing.launch.BotsingPartialInfo;
import eu.stamp.eclipse.botsing.listeners.IPropertyDataListener;
import eu.stamp.eclipse.botsing.log.LogReader;
import eu.stamp.eclipse.botsing.properties.BotsingSpinnerProperty;
import eu.stamp.eclipse.botsing.properties.ClassPathProperty;
import eu.stamp.eclipse.botsing.properties.ModelProperty;
import eu.stamp.eclipse.botsing.properties.MultipleProperty;
import eu.stamp.eclipse.botsing.properties.OutputTraceProperty;
import eu.stamp.eclipse.botsing.properties.PropertyWithText;
import eu.stamp.eclipse.botsing.properties.StackTraceProperty;
import eu.stamp.eclipse.botsing.properties.TestDirectoryProperty;
import eu.stamp.eclipse.text.validation.StampTextFieldErrorHandler;
import eu.stamp.eclipse.general.validation.IValidationPage;

import eu.stamp.eclipse.botsing.model.generation.wizard.ModelGenerationWizard;

public class BotsingWizardPage extends WizardPage 
             implements IBotsingConfigurablePart, IProjectRelated,
             IBotsingInfoSource, IValidationPage {
    
	/**
	 * List with the properties in this page
	 */
	private List<IBotsingProperty> botsingProperties;
	
	private String configurationName;
    
	/**
	 * Access to the wizard object is required in order to load configurations
	 */
	private BotsingWizard wizard;
	
	private final BotsingAdvancedOptionsDialog dialog;
	
	private Properties properties;
	private boolean propertiesLoaded;
	
	protected BotsingWizardPage(
			BotsingWizard wizard,BotsingAdvancedOptionsDialog dialog) {
		super("First page"); 
		setTitle("Botsing");
		setDescription("Botsing Configuration");
		configurationName = "new_configuration";
		this.wizard = wizard;
		this.dialog = dialog;
		botsingProperties = new LinkedList<IBotsingProperty>();
		URL url = FileLocator.find(
				Platform.getBundle(BotsingPluginConstants.BOTSING_PLUGIN_ID),
				new Path("files/botsing_page1.properties"),null);
	  properties = new Properties();
	  try {
		properties.load(url.openStream());
		propertiesLoaded = true;
	} catch (IOException e) {
		e.printStackTrace();
		propertiesLoaded = false;
	}
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
    if(propertiesLoaded) loadLabel.setToolTipText(properties
    		.getProperty("load_configuration"));
    
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
    		for(IBotsingProperty property : botsingProperties)
    		    property.callListeners();
    	}
    });
    
	/*
	 *  New configuration
	 */
	Label newConfigurationLabel = new Label(composite,SWT.NONE);
	newConfigurationLabel.setText("Create a new configuration : ");
	if(propertiesLoaded) newConfigurationLabel.setToolTipText(
			properties.getProperty("new_configuration"));
	
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
	
	// validation kit
	IFieldErrorMessageHandler errorHandler = new StampTextFieldErrorHandler(this);
	StringValidationToolkit kit = 
			new StringValidationToolkit(SWT.LEFT | SWT.TOP,1,true);
	kit.setDefaultErrorMessageHandler(errorHandler);
	
	 //Field for the tests directory
	TestDirectoryProperty testDirectory = 
			new TestDirectoryProperty("","-Dtest_dir","Test directory : ",kit);
	testDirectory.createControl(composite,false); // no only read
	if(propertiesLoaded) testDirectory
	.setTooltip(properties.getProperty("test_directory"));
	botsingProperties.add(testDirectory);
	
	// Field for the log file
	StackTraceProperty stackProperty = 
			new StackTraceProperty("","-crash_log","Execution log file : ",kit);
	stackProperty.createControl(composite);
	if(propertiesLoaded) stackProperty
	.setTooltip(properties.getProperty("log_file"));
	botsingProperties.add(stackProperty);
	
	// Spinner for the frame level
	BotsingSpinnerProperty frameLevel = 
    new BotsingSpinnerProperty("2","-target_frame","Exception frame level : ",true);
	frameLevel.createControl(composite);
	if(propertiesLoaded) frameLevel.setTooltip(properties.getProperty("frame_level"));
	botsingProperties.add(frameLevel);	
	
	// the maximun of the spinner is the frame level
	stackProperty.addDataListener(new IPropertyDataListener() {
		@Override
		public void dataChange(String newData){
			LogReader reader = new LogReader();
			reader.setFile(newData);
			int level = reader.getFrameLevel();
			if(level > 0) {
				frameLevel.setMaximun(level);
			if(Integer.parseInt(frameLevel.getData()) > level) {
				frameLevel.getSpinner().setSelection(level);
				frameLevel.getSpinner().notifyListeners(SWT.Selection,new Event());
			}
			}
		}
	});
    
	// Field for the classpath
	ClassPathProperty classPathProperty = 
			new ClassPathProperty("","-project_cp","Execution class Path : ",kit);
	classPathProperty.createControl(composite);
	if(propertiesLoaded) classPathProperty.setTooltip(
			properties.getProperty("class_path"));
	botsingProperties.add(classPathProperty);
	
	// Field for the trace output file
	OutputTraceProperty outputTraceProperty =
			new OutputTraceProperty("","Botsing log output folder : ",false,kit);
	outputTraceProperty.createControl(composite);
	if(propertiesLoaded) outputTraceProperty
	.setTooltip(properties.getProperty("output_folder"));
	botsingProperties.add(outputTraceProperty);
	
	// field of the model and Object pool must included or not together
	MultipleProperty modelAndPool = new MultipleProperty();
	
	// Field for the model
	ModelProperty modelProperty = new ModelProperty(kit);
	modelProperty.createControl(composite);
	if(propertiesLoaded) modelProperty.setTooltip("model");
	modelAndPool.addProperty(modelProperty);
	
	// object pool
	BotsingSpinnerProperty poolProperty = new BotsingSpinnerProperty("10","-Dp_object_pool","Object pool",1,1,10,false,1);
    poolProperty.createControl(composite);
    if(propertiesLoaded) poolProperty.setTooltip("percentaje of use of the model");
    modelAndPool.addProperty(poolProperty);
    
    // add Object and pool property to the list
	botsingProperties.add(modelAndPool);
	
	// dialog link
	Link link = new Link(composite,SWT.NONE);
	link.setText("<A>Advanced options</A>");
	link.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			dialog.open();
		}
	});
	
	// Botsing model generation wizard link
	Link modelWizardLink = new Link(composite,SWT.NONE);
	modelWizardLink.setText("<A>Model Generation</A>");
    modelWizardLink.addSelectionListener(new SelectionAdapter() {
    	@Override
    	public void widgetSelected(SelectionEvent e) {
    	  Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() { // TODO check
	    		Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	    		ModelGenerationWizard modelGenerationWizard = 
	    				new ModelGenerationWizard(wizard.getProject());
	    		WizardDialog diag = new WizardDialog(activeShell,modelGenerationWizard);
	    		diag.open();
			}  
    	  });
    	}
    });
	
	// required
	setControl(composite);
	setPageComplete(true);	
	}

	@Override
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy configuration) {
		for(IBotsingProperty property : botsingProperties)
		     property.appendToConfiguration(configuration);
	}

	@Override
	public void load(ILaunchConfigurationWorkingCopy configuration) {
		for(IBotsingProperty property : botsingProperties)
			property.load(configuration);
	}

	@Override
	public void projectChanged(IJavaProject newProject) {
		for(IBotsingProperty property : botsingProperties)
			if(property instanceof IProjectRelated)
				((IProjectRelated)property).projectChanged(newProject);
	}

	@Override
	public BotsingPartialInfo getInfo() {
		return new BotsingPartialInfo(configurationName,botsingProperties);
	}

	@Override
	public void error(String arg) { 
		setErrorMessage(arg); 
	    setPageComplete(false);
		}

	@Override
	public void message(String arg0, int arg1) { setMessage(arg0); }

	@Override
	public void cleanError() { 
		boolean ok = true;
		for(IBotsingProperty property : botsingProperties)
			if(property instanceof PropertyWithText)
				ok = ok && ((PropertyWithText)property).ok();
		setPageComplete(ok); 
		}
}
