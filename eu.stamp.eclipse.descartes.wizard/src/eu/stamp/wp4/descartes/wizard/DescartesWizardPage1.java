/*******************************************************************************
 * Copyright (c) 2018 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Ricardo Jose Tejada Garcia (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.wp4.descartes.wizard;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import eu.stamp.wp4.descartes.wizard.configuration.DescartesWizardConfiguration;
import eu.stamp.wp4.descartes.wizard.configuration.IDescartesWizardPart;
import eu.stamp.wp4.descartes.wizard.utils.DescartesWizardConstants;

@SuppressWarnings("restriction")
public class DescartesWizardPage1 extends WizardPage implements IDescartesWizardPart{
	
	/**
	 *  An instance for the wizard to call the update method 
	 *  and get access to the only DescartesWizardConfiguration object
	 */
	private DescartesWizard wizard;
	
	/**
	 *  This array contains the information of the mutator operators
	 *  each mutator is defined by a string and will be declared in the xml file
	 *  as <mutator>string<mutator>
	 */
	private String[] mutatorsTexts;
	
	private String configurationName = "Descartes Launch";
	
	/**
	 *  this is the list with the items of the mutators list, initially 
	 *  it will contain the mutators declared in pom.xml
	 */
	private ArrayList<TreeItem> items = new ArrayList<TreeItem>(1);
	
	// useful strings
	private String[] initialNames;
	private String pomName;
	private String projectPath;
	
	// widgets
	private Tree mutatorsTree;
	private Text projectText;
	private Text configurationText;
	private Text pomText;

	public DescartesWizardPage1(DescartesWizard wizard) {
		super("Descartes configuration");
		this.wizard = wizard;
		setTitle("Descartes configuration");
		setDescription("Configuration of Descartes mutators");
	}
	
	@Override
	public void createControl(Composite parent) {
		
		// loading the properties for the tooltip, the name of each property is
		// the name of its corresponding widget
		Properties tooltipsProperties = new Properties();
		try {tooltipsProperties = getTheProperties("files/descartes_tooltips.properties");	
		} catch (IOException e1) { e1.printStackTrace(); }
		
		// create the composite
		Composite composite = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();    // the layout of composite
		layout.numColumns = 3;
		composite.setLayout(layout);
		
		
		/*
		 *  ROW 1 : Load configuration
		 */
		Label configurationLabel = new Label(composite,SWT.NONE);
		configurationLabel.setText("load configuration : ");
		GridDataFactory.swtDefaults().grab(false, false).applyTo(configurationLabel);
		configurationLabel.setToolTipText(tooltipsProperties.getProperty(
				"configurationLabel"));
		
		Combo configurationCombo = new Combo(composite,SWT.BORDER); // combo for saved configurations
		configurationCombo.setEnabled(false);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(configurationCombo);
		String[] configurations = wizard.getWizardConfiguration().getConfigurationNames();
		for(String sr : configurations) configurationCombo.add(sr);
		/*
		 *  ROW 2 : Create new configuration
		 */
		Label newConfigurationLabel = new Label(composite,SWT.NONE);
		newConfigurationLabel.setText("create new configuration : ");
		GridDataFactory.swtDefaults().grab(false, false).applyTo(newConfigurationLabel);
		newConfigurationLabel.setToolTipText(tooltipsProperties.getProperty(
				"newConfigurationLabel"));
		
		configurationText = new Text(composite,SWT.BORDER);  // text for the name of a new configuration
		GridDataFactory.fillDefaults().grab(true, false).applyTo(configurationText);
		
		Button configurationButton = new Button(composite,SWT.CHECK); 
		configurationButton.setSelection(true);   // enables-disables the configuration text and combo
		
		/*
		 *   ROW 3 : path of the selected project
		 */
		Label projectLabel = new Label(composite,SWT.NONE);
		projectLabel.setText("path of the project : ");
		GridDataFactory.swtDefaults().grab(false, false).applyTo(projectLabel);
		projectLabel.setToolTipText(tooltipsProperties.getProperty(
				"projectLabel"));
		
		projectText = new Text(composite,SWT.BORDER);  
		GridDataFactory.fillDefaults().grab(true, false).applyTo(projectText);
		projectText.setText(projectPath);
		
		Button projectButton = new Button(composite,SWT.PUSH);  // opens a dialog to select a project
		projectButton.setText("Select a Project");
		GridDataFactory.swtDefaults().applyTo(projectButton);
		projectButton.setToolTipText(tooltipsProperties.getProperty(
				"projectButton"));
		/*
		 *   ROW 4 : Mutators list title
		 */
		Label mutatorsLabel = new Label(composite,SWT.NONE);
		mutatorsLabel.setText("Mutators : ");
		GridDataFactory.fillDefaults().span(3, 1).indent(0, 8).applyTo(mutatorsLabel);
		/*
		 *   ROW 5 (multiple row) : list with the mutators and buttons to add,remove ...
		 */
		mutatorsTree = new Tree(composite,SWT.V_SCROLL | SWT.CHECK);
        GridData gd = new GridData(SWT.FILL,SWT.FILL,true,true);
        gd.horizontalSpan = 2;
        gd.verticalSpan = 6;
        gd.minimumWidth = 250;
        mutatorsTree.setLayoutData(gd);
        mutatorsTree.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        GridLayout Layforgr1 = new GridLayout();
        mutatorsTree.setLayout(Layforgr1);
        mutatorsTree.setToolTipText(tooltipsProperties.getProperty("mutatorsTree"));
        
        for(int i = 0; i < mutatorsTexts.length; i++) {
         TreeItem item = new TreeItem(mutatorsTree,SWT.NONE);	
         item.setText(mutatorsTexts[i]);
         items.add(item);
        }

         initialNames = new String[items.size()];
         for(int i = 0; i < items.size(); i++) initialNames[i] = items.get(i).getText();
         
         /*
          *  Buttons to manipulate the mutators list and their listeners (at the end)
          */
         
        // a button to remove the selected mutators
        Button removeMutatorButton = new Button(composite,SWT.PUSH);
        removeMutatorButton.setText("Remove selected mutators");
        GridDataFactory.fillDefaults().applyTo(removeMutatorButton);
        removeMutatorButton.setToolTipText(tooltipsProperties.getProperty(
        		"removeMutatorButton"));
        
        // a button to add a new mutator to the list (it opens a dialog with a text)
        Button addMutatorButton = new Button(composite,SWT.PUSH);
        addMutatorButton.setText("Add mutator");
        GridDataFactory.fillDefaults().applyTo(addMutatorButton);
        addMutatorButton.setToolTipText(tooltipsProperties.getProperty(
        		"addMutatorButton"));
        
        // a button to remove all the mutators in the list
        Button removeAllButton = new Button(composite,SWT.PUSH);
        removeAllButton.setText("Remove all");
        GridDataFactory.fillDefaults().applyTo(removeAllButton);
        removeAllButton.setToolTipText(tooltipsProperties.getProperty(
        		"removeAllButton"));
        
        // a button to revert the changes in the mutator list
        Button initialListButton = new Button(composite,SWT.PUSH);
        initialListButton.setText("Set initial mutators");
        GridDataFactory.fillDefaults().applyTo(initialListButton);
        initialListButton.setToolTipText(tooltipsProperties.getProperty(
        		"initialListButton"));
        
        // a button to set a default mutator list
        Button defaultMutatorsButton = new Button(composite,SWT.PUSH);
        defaultMutatorsButton.setText("Set default mutators");
        GridDataFactory.fillDefaults().applyTo(defaultMutatorsButton);
        defaultMutatorsButton.setToolTipText(tooltipsProperties.getProperty(
        		"defaultMutatorsButton"));
        
        Label space = new Label(composite,SWT.NONE);
        space.setText("");
        
        /*
         *   ROW 6 : Pom file
         */
        Label pomLabel = new Label(composite,SWT.NONE);
        pomLabel.setText("name of the POM file : ");
        GridDataFactory.swtDefaults().grab(false, false).indent(0, 8).applyTo(pomLabel);
        pomLabel.setToolTipText(tooltipsProperties.getProperty("pomLabel"));
        
        pomText = new Text(composite,SWT.BORDER);
        pomText.setText("descartes_pom.xml");
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).indent(0, 8)
        .applyTo(pomText);
        
        // listeners
        configurationCombo.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		configurationText.setText("");
        		if(configurationCombo.getText() != null)if(!configurationCombo.getText().isEmpty()) {
        		configurationName = configurationCombo.getText();
        		try {
					DescartesWizardConfiguration conf = wizard.getWizardConfiguration();
							conf.setCurrentConfiguration(configurationCombo.getText()); 
					wizard.setWizardConfiguration(conf);
					// now the wizard configuration is updated ready to update all the wizard parts
					wizard.updateWizardParts();
				} catch (CoreException e1) {
					e1.printStackTrace();
				}}
        	}
        });
        configurationButton.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		boolean selection = configurationButton.getSelection();
        		configurationText.setEnabled(selection);
        		if(!selection)configurationText.setText("");
        		configurationCombo.setEnabled(!selection);
        		if(selection)configurationCombo.setText("");
        	}
        });
        projectButton.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		IJavaProject jProject = showProjectDialog();
        		if(jProject != null) wizard
        		.setWizardConfiguration(new DescartesWizardConfiguration(jProject));
        	}
        });
        removeMutatorButton.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
            for(int i = 0; i < items.size(); i++) {
            	if(!items.get(i).isDisposed()) if(items.get(i).getChecked()) {
            		items.get(i).dispose();
            		items.remove(i); i--;
            	}
            }}
        });
        addMutatorButton.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        	  String sr = showInputDialog();
        	  if(sr != null) { 
        		  items.add(new TreeItem(mutatorsTree,SWT.NONE));
        		  items.get(items.size()-1).setText(sr);
        	  }
        	}
        });
        removeAllButton.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		for(int i = items.size()-1; i >= 0; i--) items.remove(i);
        		mutatorsTree.removeAll();
        	}
        });
        initialListButton.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		for(int i = items.size()-1; i >= 0; i--) items.remove(i);
        		mutatorsTree.removeAll();
        		for(int i = 0; i < initialNames.length; i++) {
        		TreeItem it = new TreeItem(mutatorsTree,SWT.NONE);
        		it.setText(initialNames[i]);
        		items.add(it);}
        	}
        });


        String[] defaultMutators = {""};
        try { defaultMutators = getDefaultMutators();
		} catch (IOException e1) { e1.printStackTrace(); }
        final String[] finalDefaultMutators = defaultMutators;
        defaultMutatorsButton.addSelectionListener(new SelectionAdapter(){
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		for(int i = items.size()-1; i >= 0; i--) items.remove(i);
        		mutatorsTree.removeAll();
        		for(int i = 0; i < finalDefaultMutators.length; i++) {
        		TreeItem it = new TreeItem(mutatorsTree,SWT.NONE);
        		it.setText(finalDefaultMutators[i]);
        		items.add(it);}
        	}
        });
        
        pomText.addKeyListener(new KeyListener() {
        	@Override
        	public void keyPressed(KeyEvent e) {}
        	@Override
        	public void keyReleased(KeyEvent e) {
        		if(!pomText.getText().isEmpty())pomName = pomText.getText();
        	}
        });
        
		// required to avoid an error in the System
		setControl(composite);
		setPageComplete(true);	  
		}

	@Override
	public void updateDescartesWizardPart(DescartesWizardConfiguration wConf) {
		
		// update project path
		projectPath = wConf.getProjectPath();
		if(projectText != null) { if(!projectText.isDisposed()) projectText.setText(projectPath);}
		
		// update mutators
		mutatorsTexts = wConf.getMutatorsTexts();
		 
		// set the updated mutators in the tree
		
		if(mutatorsTree != null) if(!mutatorsTree.isDisposed()) {
		for(int i = items.size()-1; i >= 0; i--) items.remove(i);
		mutatorsTree.removeAll();
		
        for(int i = 0; i < mutatorsTexts.length; i++) {
            TreeItem item = new TreeItem(mutatorsTree,SWT.NONE);
            item.setText(mutatorsTexts[i]);
            items.add(item);
           }

            initialNames = new String[items.size()];
            for(int i = 0; i < items.size(); i++) initialNames[i] = items.get(i).getText();}
            
		    // update pom's name
		    if(pomText != null) pomText.setText(wConf.getPomName());
	}
	
	@Override
	public void updateWizardReference(DescartesWizard wizard) {
		this.wizard = wizard;
	}
    /**
     * @return an string array with the mutators contents
     */
	public String[] getMutatorsSelection() {
		String[] texts = new String[items.size()];
		for(int i = 0; i < items.size(); i++) texts[i] = items.get(i).getText();
		return texts;
	}
	public String getConfigurationName() {
		if(configurationText.getText() != null && !configurationText.getText().isEmpty())
			configurationName =  configurationText.getText();
		return configurationName;
	}
	/**
	 * @return the name of the POM file to write
	 */
	public String getPomName() {
		return pomName;
	}
	/**
	 *  This method opens a dialog with a text to set the content of a new mutator
	 *  it is called by the add mutator button listener
	 */
	private String showInputDialog () {
		InputDialog dialog = new InputDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),"Add mutator",
				"enter the new mutator",null,null);
		if(dialog.open() == Window.OK) return dialog.getValue();
		return null;
	}
	/**
	 * This method opens a dialog to select a project, 
	 * it is called by the listener of the select project button
	 * @return the new project
	 */
	private IJavaProject showProjectDialog() {
		
		Class<?>[] acceptedClasses = new Class[] {IJavaProject.class,IProject.class};
		TypedElementSelectionValidator validator = new TypedElementSelectionValidator(acceptedClasses,true);
		ViewerFilter filter= new TypedViewerFilter(acceptedClasses) {
			@Override
			public boolean select(Viewer viewer,Object parentElement, Object element) {
				if(element instanceof IProject) {
					try {
						return ((IProject)element).hasNature(DescartesWizardConstants.MAVEN_NATURE_ID);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
				if(element instanceof IJavaProject) {
					try {
						return ((IJavaProject)element).getProject().hasNature(DescartesWizardConstants.MAVEN_NATURE_ID);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
				return false;
			}
		};	
		
		  IWorkspaceRoot fWorkspaceRoot= ResourcesPlugin.getWorkspace().getRoot();
	        
	        StandardJavaElementContentProvider provider= new StandardJavaElementContentProvider();
	        ILabelProvider labelProvider= new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
	        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	        ElementTreeSelectionDialog dialog= new ElementTreeSelectionDialog(shell, labelProvider, provider);
	        dialog.setValidator(validator);
	        dialog.setComparator(new JavaElementComparator());
	        dialog.setTitle(" Select a project ");
	        dialog.setMessage(" Select a project ");
	        dialog.setInput(JavaCore.create(fWorkspaceRoot));
	        dialog.addFilter(filter);
	        dialog.setHelpAvailable(false);
	        
	      

	        if(dialog.open() == Window.OK) {
	            Object[] results = dialog.getResult();
	            for(Object ob : results) {
	            	if(ob instanceof IJavaProject) { 
	            		IJavaProject jProject = (IJavaProject)ob;
	            		return jProject;
	             }
	            }
	        }
	        return null;
	}
	/**
	 * get the default mutators from the properties file
	 * @return an array with a default mutators list
	 * @throws IOException
	 */
	private String[] getDefaultMutators() throws IOException {

		Properties properties = getTheProperties("files/default_mutators.properties");
		
		ArrayList<String> list = new ArrayList<String>(1);
		Set<Object> set = properties.keySet();
		for(Object o : set) list.add((String)o);
	    Collections.sort(list);
		String[] result = new String[list.size()];
		for(int i = 0; i < list.size(); i++) result[i] = properties.getProperty(list.get(i));
		return result;
	}
	/**
	 * loads a properties object from a file
	 * @param path properties file's relative path to the project folder
	 * @return a properties object
	 * @throws IOException
	 */
	private Properties getTheProperties(String path) throws IOException {
		final URL propertiesURL = FileLocator.find(Platform.getBundle(
				DescartesWizardConstants.DESCARTES_PLUGIN_ID),
				new Path(path),null);
		Properties properties = new Properties();
		InputStream inputStream = propertiesURL.openStream();
		properties.load(inputStream);
		inputStream.close();
		return properties;
	}

}
