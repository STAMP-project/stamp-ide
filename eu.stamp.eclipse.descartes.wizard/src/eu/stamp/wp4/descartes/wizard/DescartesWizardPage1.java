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
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
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

import com.richclientgui.toolbox.validation.IFieldErrorMessageHandler;
import com.richclientgui.toolbox.validation.ValidatingField;
import com.richclientgui.toolbox.validation.string.StringValidationToolkit;
import com.richclientgui.toolbox.validation.validator.IFieldValidator;
import com.richclientgui.toolbox.validation.IQuickFixProvider;

import eu.stamp.eclipse.descartes.wizard.validation.DescartesWizardErrorHandler;
import eu.stamp.eclipse.descartes.wizard.validation.IDescartesPage;
import eu.stamp.wp4.descartes.wizard.configuration.DescartesWizardConfiguration;
import eu.stamp.wp4.descartes.wizard.configuration.IDescartesWizardPart;
import eu.stamp.wp4.descartes.wizard.utils.DescartesWizardConstants;

@SuppressWarnings("restriction")
public class DescartesWizardPage1 extends WizardPage 
                           implements IDescartesWizardPart, IDescartesPage{
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
	private Combo configurationCombo;
	private Text projectText;
	private ValidatingField<String> configurationField;
	private ValidatingField<String> pomField;
	private ValidatingField<String> configurationComboField;
	
	private Properties tooltipsProperties;
    private StringValidationToolkit valKit = null;
    private final IFieldErrorMessageHandler errorHandler;
    private boolean[] check = {true,false,false};

	public DescartesWizardPage1(DescartesWizard wizard) {
		super("Descartes configuration");
		this.wizard = wizard;
		setTitle("Descartes configuration");
		setDescription("Configuration of Descartes mutators");
		
		/*  loading the properties for the tooltip, the name of each property is
		 *  the name of its corresponding widget   
		 */
		tooltipsProperties = new Properties();
		try {tooltipsProperties = getTheProperties("files/descartes_tooltips.properties");	
		} catch (IOException e1) { e1.printStackTrace(); }
		
		// prepare the message handler and the validation tool kit
		errorHandler = new DescartesWizardErrorHandler(this); 
		valKit = new StringValidationToolkit(SWT.LEFT | SWT.TOP,1,true);
        valKit.setDefaultErrorMessageHandler(errorHandler);
	}
	
	@Override
	public void createControl(Composite parent) {
		
		// create the composite
		Composite composite = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();    // the layout of composite
		layout.numColumns = 3;
		composite.setLayout(layout);
		
        // ROW 1 : Load configuration
		createLabel(composite,"load configuration : ","configurationLabel");
		
		configurationCombo = new Combo(composite,SWT.BORDER | SWT.READ_ONLY); // combo for saved configurations
		configurationCombo.setEnabled(false);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(configurationCombo);
		String[] configurations = wizard.getWizardConfiguration().getConfigurationNames();
		configurationCombo.add("");
		for(String sr : configurations) configurationCombo.add(sr);
		
		createConfigurationField(composite);  // ROW 2 : Create new configuration
		
		createLabel(composite,"path of the project : ","projectLabel");
		
		projectText = new Text(composite,SWT.BORDER | SWT.READ_ONLY);
		projectText.setText(projectPath);
		GridDataFactory.fillDefaults().applyTo(projectText);
		
		Button projectButton = new Button(composite,SWT.PUSH);  // opens a dialog to select a project
		projectButton.setText("Select a Project");
		GridDataFactory.swtDefaults().applyTo(projectButton);
		projectButton.setToolTipText(tooltipsProperties.getProperty(
				"projectButton"));
		 projectButton.addSelectionListener(new SelectionAdapter() {
	        	@Override
	        	public void widgetSelected(SelectionEvent e) {
	        		IJavaProject jProject = showProjectDialog();
	        		if(jProject == null) return;
	        		wizard.setWizardConfiguration(new DescartesWizardConfiguration(jProject));
	        		projectText.setText(wizard.getWizardConfiguration().getProjectPath());
	        		check[1] = true; checkPage();
	        	}
	        });
		
		
		//createProjectField(composite); //  ROW 3 : path of the selected project

		Label mutatorsLabel = new Label(composite,SWT.NONE);  // ROW 4 : Mutators list title
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

        createPomField(composite); //  ROW 6 : Pom file
        
        // ROW 7 : Pom field button
        Button pomButton = new Button(composite,SWT.CHECK);
        pomButton.setText(" Use a different pom name");
        pomButton.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
             pomField.getControl().setEnabled(pomButton.getSelection());
             if(!pomButton.getSelection() && configurationField.getControl().isEnabled())
            	 ((Text)pomField.getControl()).setText(
            			 ((Text)configurationField.getControl()).getText() + "_pom.xml"); 
        	}
        });
        
        // listeners
        configurationCombo.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		((Text)configurationField.getControl()).setText("");
        		if(configurationCombo.getText() != null)if(!configurationCombo.getText().isEmpty()) {
        		configurationName = configurationCombo.getText();
        		if(!configurationName.isEmpty()) { check[0] = true; checkPage();}
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
        	  String sr = showAddDialog();
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
        try { defaultMutators = getDefaultMutators();  // get the default list from the properties file
		} catch (IOException e1) { e1.printStackTrace(); }
        final String[] finalDefaultMutators = defaultMutators;
        defaultMutatorsButton.addSelectionListener(new SelectionAdapter(){ // set a default mutators list
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
        
        // now the widgets are not null and open
        createConfigurationComboValidator();
        
		// required
		setControl(composite);
		setPageComplete(true);	  
		}

	/**
	 * create a validation field to set the name of the new configuration, the validator 
	 * checks that the name is not empty and it doesn't contain non allowed characters,
	 * the non allowed characters are the non alphanumeric characters except _ and - 
	 * the field includes a quick fixer to remove the non allowed characters and to set
	 * a default name when the text is empty
	 * @param composite : the composite to append the validation field
	 */
	private void createConfigurationField(Composite composite) {
		
		createLabel(composite,"create new configuration : ","newConfigurationLabel");
		
		configurationField = valKit.createTextField(composite,new IFieldValidator<String>() {
			boolean flag; // two possible error messages
			@Override
			public String getErrorMessage() { 
				if(flag) return "Configuration name is empty";
				return "Configuration contains non allowed characters"; }
			@Override
			public String getWarningMessage() { return ""; }
			@Override
			public boolean isValid(String contents) { 
				 if(pomField != null)if(!pomField.getControl().isDisposed())
					 if(!pomField.getControl().isEnabled())
						 ((Text)pomField.getControl()).setText(contents + "_pom.xml"); // look at the !
				
				if(configurationField != null)if(configurationField.getControl().isEnabled()) {
					if(contents.isEmpty()) {
						check[0] = false; checkPage();
					     flag = true; return false;
					     }
					if( !contents.equalsIgnoreCase(
							contents.replaceAll("[^A-Za-z0-9_\\-]", ""))) {
						check[0] = false; checkPage();
						flag = false; return false;
					}
				}
				check[0] = true; checkPage(); 

				return true;
				}
			@Override
			public boolean warningExist(String contents) { return false; }
			
		},false,"new_configuration");	
		
		GridDataFactory.fillDefaults().grab(true, false).indent(10, 0)
		.applyTo(configurationField.getControl());
		
		configurationField.setQuickFixProvider(new IQuickFixProvider<String>() {
			boolean flag; // two possible problems
			@Override
			public boolean doQuickFix(ValidatingField<String> field) {
				Text text = ((Text)field.getControl());
				if(flag) {
					text.setText("descartes_configuration"); return true;
				}
			    text.setText(text.getText().replaceAll("[^A-Za-z0-9_\\-]",""));
				return true;
			}   
			@Override  
			public String getQuickFixMenuText() { 
				return "fix problems"; }
			@Override
			public boolean hasQuickFix(String content) {
				if(content.isEmpty()) { flag = true; return true;}
				flag = false;
				return !content.equalsIgnoreCase(content.replaceAll("[^A-Za-z0-9_\\-]",""));
			}			
		});
		
		Button configurationButton = new Button(composite,SWT.CHECK); 
		configurationButton.setSelection(true);   // enables-disables the configuration text and combo
        configurationButton.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) { 
  
        		boolean selection = configurationButton.getSelection();
        		configurationField.getControl().setEnabled(selection);
        		configurationCombo.setEnabled(!selection);
        		if(selection) {
        			((Text)configurationField.getControl()).setText("new_configuration");
        			configurationCombo.setText("");
        			configurationComboField.validate();
        			checkPage();
        		}
        		if(!selection) {
        			((Text)configurationField.getControl()).setText("");
        			configurationComboField.validate();
        	         checkPage();
        		}
        	}
        });
	}
	private void createConfigurationComboValidator() {
		configurationComboField = valKit.createField(configurationCombo,new IFieldValidator<String>() {
			@Override
			public String getErrorMessage() { return " Select a configuration ";
			}
			@Override
			public String getWarningMessage() { return null; 
			}
			@Override
			public boolean isValid(String sr) {
				if(configurationCombo.isEnabled() && sr.isEmpty()) return false;
				return true;
			}
			@Override
			public boolean warningExist(String sr) { return false;
			}		
		},false, "");
	}
	/**
	 * 
	 * @param composite : the composite to append the validation field
	 */
	private void createPomField(Composite composite) {
		
		createLabel(composite,"name of the POM file : ","pomLabel");
		
		pomField = valKit.createTextField(composite, new IFieldValidator<String>() {
			int flag;  // three posible messages
			@Override
			public String getErrorMessage() {
				if(flag == 0) return "Pom name is empty";
				if(flag == 1) return "Pom name must end with .xml";
				return "Pom name contains non allowed characters";
			}
			@Override
			public String getWarningMessage() { return null; }
			@Override
			public boolean isValid(String contents) {
				
        		if(pomField != null)if(!((Text)pomField.getControl()).getText().isEmpty()) // the listener instructions
        			pomName = ((Text)pomField.getControl()).getText();
				
				if(contents.isEmpty()) { flag = 0;   // validation
				check[2] = false; checkPage();
				return false; }
				if(!contents.endsWith(".xml")) { flag = 1;
				check[2] = false; checkPage();
				return false; }
				if(!contents.equalsIgnoreCase(contents
						.replaceAll("[^A-Za-z0-9_/\\.\\-\\ ]",""))) {flag = 2;
					check[2] = false; checkPage(); 
					return false; }
				check[2] = true; checkPage(); return true;
			}
			@Override
			public boolean warningExist(String contents) { return false; }
		}, false, "new_configuration_pom.xml");
		
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).indent(10, 0)
		.applyTo(pomField.getControl());

        pomField.setQuickFixProvider(new IQuickFixProvider<String>() {
            int flag; // three possible problems
			@Override
			public boolean doQuickFix(ValidatingField<String> field) {
				Text text = (Text)field.getControl();
				if(flag == 0) {
                  text.setText("descartes_pom.xml"); return true;
				}
				if(flag == 1) {
					text.setText(text.getText()+".xml"); 
				}
				text.setText(text.getText()
						.replaceAll("[^A-Za-z0-9_/\\.\\-\\ ]",""));
				return true;
			}
			@Override
			public String getQuickFixMenuText() {      
				return "fix problems";
			}
			@Override
			public boolean hasQuickFix(String content) {
				if(content.isEmpty()) {
					flag = 0; return true;
				}
				if(!content.endsWith(".xml")) {
					flag = 1; return true;
				}
				if(!content.equalsIgnoreCase(
						content.replaceAll("[^A-Za-z0-9/_\\.\\- \\ ]",""))) {
					flag = 2; return true;
				}
				return false;
			}       	
        });
       pomField.getControl().setEnabled(false);
	}
	/**
	 * creates a label with the given text, and tooltip text and a predefined grid data and style
	 * @param composite : the composite to append the label
	 * @param labelText : the text to display in the label
	 * @param propertyKey : the key of the tooltip text for this label
	 */
    private void createLabel(Composite composite,String labelText,String propertyKey) {
	   
		Label label = new Label(composite,SWT.NONE);
		label.setText(labelText);
		GridDataFactory.swtDefaults().grab(false, false).grab(false, false).applyTo(label);
		label.setToolTipText(tooltipsProperties.getProperty(propertyKey));
	}
    
   @Override
   public void updateDescartesWizardPart(DescartesWizardConfiguration wConf) {
		
		// update project path
		projectPath = wConf.getProjectPath();
		if(projectText != null)if(!projectText.isDisposed()) {
			projectText.setText(projectPath);
		}
		
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
		    if(pomField != null)((Text)pomField.getControl()).setText(wConf.getPomName());
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
		if(configurationField != null)if(!((Text)configurationField.getControl())
				.getText().isEmpty())
			configurationName =  ((Text)configurationField.getControl()).getText();
		if(configurationName.isEmpty())configurationName = configurationCombo.getText();
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
	private String showAddDialog () {
		AddMutatorDialog addDialog = new AddMutatorDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell());
		if(addDialog.open() == Window.OK) {
			return addDialog.getResult();
		}
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

	private void checkPage() {
		if(configurationCombo != null)  
			if(configurationCombo.isEnabled()){
					if(configurationCombo.getText().isEmpty())check[0] = false;
					else check[0] = true;
			}
		if(projectText != null) {
			if(!projectText.isDisposed()) check[1] = !projectText.getText().isEmpty();
			else check[1] = true;
		}
		else check[1] = true;
		for(boolean bo : check)if(!bo) { setPageComplete(false); return; }
		setPageComplete(true);
	}

	@Override
	public void error(String mess) { setErrorMessage(mess); }
	@Override
	public void message(String mess, int style) { setMessage(mess,style); }
}
