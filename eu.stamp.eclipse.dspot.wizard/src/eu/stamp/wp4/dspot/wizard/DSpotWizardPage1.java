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
package eu.stamp.wp4.dspot.wizard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import com.richclientgui.toolbox.validation.IFieldErrorMessageHandler;
import com.richclientgui.toolbox.validation.IQuickFixProvider;
import com.richclientgui.toolbox.validation.ValidatingField;
import com.richclientgui.toolbox.validation.string.StringValidationToolkit;
import com.richclientgui.toolbox.validation.validator.IFieldValidator;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SegmentListener;
import org.eclipse.swt.events.SegmentEvent;

import eu.stamp.eclipse.dspot.wizard.page.utils.DSpotPageSizeCalculator;
import eu.stamp.eclipse.dspot.wizard.page.utils.DSpotRowSizeCalculator;
import eu.stamp.eclipse.dspot.wizard.page.utils.DSpotSizeManager;
import eu.stamp.wp4.dspot.constants.DSpotWizardConstants;
import eu.stamp.wp4.dspot.dialogs.DspotWizardHelpDialog;
import eu.stamp.wp4.dspot.wizard.utils.WizardConfiguration;

/**
 * this class describes the first page of the DSpot wizard 
 * 
 */
@SuppressWarnings("restriction")
public class DSpotWizardPage1 extends WizardPage { 
	
	// [0] project, [1] src, [2] testScr, [3] javaVersion, [4] outputDirectory, [5] filter
	private String[] TheProperties = new String[6];
	private WizardConfiguration wConf;
	private DSpotWizard wizard;
	private DSpotPage1Validator pageValidator;
	
	private Properties tooltipsProperties;
	
    private StringValidationToolkit valKit = null;
    private final IFieldErrorMessageHandler errorMessageHandler;
	
    private Combo configCombo;
    private Combo sourcePathCombo;
    private Combo sourceTestCombo;
    private Button projectSelectionbt;
    private ValidatingField<String> configurationField;
    private ValidatingField<String> projectField;
    private ValidatingField<String> configurationComboField;
    
    // to compute size
    DSpotPageSizeCalculator sizeCalculator;
    DSpotRowSizeCalculator row;
    
	public DSpotWizardPage1(WizardConfiguration wConf,DSpotWizard wizard){
		super("Project configuration");
		setTitle("Project configuration");
		setDescription("Information about the project");
		this.wConf = wConf;
		this.wizard = wizard;
		
		 tooltipsProperties = new Properties();
			final URL propertiesURL = FileLocator.find(Platform.getBundle(
					DSpotWizardConstants.PLUGIN_NAME),
					new Path("files/dspot_tooltips1.properties"),null);
			      InputStream inputStream;
			
		    try {
			inputStream = propertiesURL.openStream();
			tooltipsProperties.load(inputStream);
			inputStream.close();} catch (IOException e2) {
				e2.printStackTrace(); }

		    errorMessageHandler = new WizardErrorHandler();
			valKit = new StringValidationToolkit(SWT.LEFT | SWT.TOP,
	        		1,true);
	        valKit.setDefaultErrorMessageHandler(errorMessageHandler);
	        
	        pageValidator = new DSpotPage1Validator();
	        sizeCalculator = new DSpotPageSizeCalculator();
	        row = new DSpotRowSizeCalculator();
	} // end of the constructor
 
	@Override
	public void createControl(Composite parent) {
		
		// create the composite
		Composite composite = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();    // the layout of composite
		layout.numColumns = 3;
		composite.setLayout(layout);
		
		int VS = 8;   // this will be the verticalIndent between rows in composite
		
		/*
		 *  ROW 1 : use saved configuration
		 */
		Label lb0 = new Label(composite,SWT.NONE);  // label in (1,0)
		lb0.setText("Use saved configuration : ");
		lb0.setToolTipText(tooltipsProperties.getProperty("lb0"));
		row.addWidget(lb0);
	    
		configCombo = new Combo(composite,SWT.BORDER | SWT.READ_ONLY); // combo in (1,1) to select a configuration
		GridDataFactory.fillDefaults().grab(true,false).span(2, 1).indent(0, VS).indent(8, 0).applyTo(configCombo);
		List<ILaunchConfiguration> configurations = wConf.getLaunchConfigurations();
		for(ILaunchConfiguration laun : configurations) {
			configCombo.add(laun.getName());
		}
		configCombo.add(""); // IMPORTANT this must be at the end of the combo list to get the correct selection index
		configCombo.setEnabled(false); 
		row.addWidget(configCombo);
		
		pageValidator.addElement(new IDSpotPageElement() {
			@Override
			public boolean validate() {
				if(configCombo.isEnabled() && configCombo.getText().equalsIgnoreCase("")) 
					return false;
				return true;
			}
		});
		
		createConfigurationComboValidator();  // this is to display an error message if no configuration is selected
		sizeCalculator.addRow(row);
		/*
		 *  Row 2 : New Configuration 
		 */
		row = new DSpotRowSizeCalculator();
		createConfigurationField(composite);
		sizeCalculator.addRow(row);
		/*
		 *  ROW 3 : Project's path
		 */ 
		row = new DSpotRowSizeCalculator();
		// Obtain the path of the project
		String[] sour = wConf.getSources();  
		boolean[] isTest = wConf.getIsTest();  // the packages in sour with test classes
		
		createProjectField(composite);
		sizeCalculator.addRow(row);
        /*
         *  ROW 4 : Source path
         */
		row = new DSpotRowSizeCalculator();
		createLabel(composite,"Path of the source : ","lb2"); // Label in (4,1)
	
        sourcePathCombo = new Combo(composite,SWT.BORDER | SWT.READ_ONLY);  // Combo in (4,2) for the source's path
        GridDataFactory.fillDefaults().grab(true,false).span(2,1).indent(0, VS).applyTo(sourcePathCombo);
        row.addWidget(sourcePathCombo);
        sourcePathCombo.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		
        		TheProperties[1] = sourcePathCombo.getText();  // the path of the source
        		
        	}
        }); // end of the selection listener
        sizeCalculator.addRow(row);
        /*
         *  ROW 5 : SourceTest path
         */
        row = new DSpotRowSizeCalculator();
        createLabel(composite,"Path of the source test : ","lb3");
		
        sourceTestCombo = new Combo(composite,SWT.BORDER | SWT.READ_ONLY);
        GridDataFactory.fillDefaults().grab(true,false).span(2, 1).indent(0, VS).applyTo(sourceTestCombo);
        row.addWidget(sourceTestCombo);
        for(int i = 0; i < sour.length; i++) {  // add the sources to the combo
        	if(isTest[i]) {  // if it is not a test package
        	sourceTestCombo.add(sour[i]);} else { sourcePathCombo.add(sour[i]); }
        } // end of the for
        
        if(sourcePathCombo.getItems().length > 0) {
        	sourcePathCombo.setText(sourcePathCombo.getItem(0));
    		TheProperties[1] = sourcePathCombo.getText();  // the path of the source
        }
        if(sourceTestCombo.getItems().length > 0) {
        	sourceTestCombo.setText(sourceTestCombo.getItem(0));
    		TheProperties[2] = sourceTestCombo.getText();    //  testSrc
        }
        
        
        sourceTestCombo.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        	
        		TheProperties[2] = sourceTestCombo.getText();    //  testSrc 		
        	}
        });
		sizeCalculator.addRow(row);
        /*
         *  ROW 6 : Java version
         */
		row = new DSpotRowSizeCalculator();
        createLabel(composite,"Java version : ","lb4"); // Label in (6,1)
		
		Combo combo1 = new Combo(composite,SWT.NONE | SWT.READ_ONLY);  // Combo in (6,2) for the version
		combo1.add("8"); combo1.add("7"); combo1.add("6"); combo1.add("5");
		combo1.setText("8");
        GridDataFactory.fillDefaults().grab(true,false).span(2, 1).indent(0, VS).applyTo(combo1);
        row.addWidget(combo1);
        TheProperties[3] = "8";
        combo1.addSelectionListener(new SelectionAdapter() {  // Use a SelectionAdapter
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		
        		TheProperties[3] = combo1.getText();    // javaVersion		
        	}
        });  // end of the SelectionListener
		
		// (7,1 and 2) group with optional information
        row = new DSpotRowSizeCalculator();
		Group gr = new Group(composite,SWT.NONE);
		gr.setText("Optional information");
		GridDataFactory.fillDefaults().grab(true,false).span(3,3).indent(0,2*VS).applyTo(gr);
		GridLayout layout2 = new GridLayout();
		layout2.numColumns = 2;
		gr.setLayout(layout2);
		
		// first row in group gr (1,x)(gr)
		Label lbOutput = new Label(gr,SWT.NONE);               // Label in (1,1)(gr)
		lbOutput.setText("Path of the output folder : ");
		lbOutput.setToolTipText(tooltipsProperties.getProperty("lbOutput"));
		
		Text tx4 = new Text(gr,SWT.BORDER);     // Text in (1,2)(gr) for the output's folder path
		tx4.setText("dspot-out/");
		GridDataFactory.fillDefaults().grab(true,false).indent(0, VS).applyTo(tx4);
		tx4.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				
				TheProperties[4] = tx4.getText();
			}
		});  // end of the KeyListener
		tx4.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
				
				TheProperties[4] = tx4.getText();
				pageValidator.validatePage();
			}	
		});
		
		pageValidator.addElement(new IDSpotPageElement() {
			@Override
			public boolean validate() {
				String sr = tx4.getText().replaceAll("\\.", "");
				if(!sr.equalsIgnoreCase(sr
						.replaceAll("[^A-za-z0-9_/\\- ]", ""))) return false;
				return true;
			}
		});
		
		Label lbFilter = new Label(gr,SWT.NONE);    // Label in (2,1)(gr)
		lbFilter.setText("Filter :  ");
		lbFilter.setToolTipText(tooltipsProperties.getProperty("lbFilter"));
		
		Text tx5 = new Text(gr,SWT.BORDER);    // Text in (2,2)(gr) for the filter
		tx5.setText("");
		GridDataFactory.fillDefaults().grab(true,false).indent(0, VS).applyTo(tx5);
		tx5.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {	
				TheProperties[5] = tx5.getText();  // filter
				pageValidator.validatePage();
			}
		});  // end of the KeyListener
		row.addWidget(gr);
		sizeCalculator.addRow(row);
		DSpotSizeManager.getInstance().addPage(sizeCalculator);
		DSpotSizeManager.getInstance().configureWizardSize(wizard);
		
		pageValidator.addElement(new IDSpotPageElement() {
			@Override
			public boolean validate() {
				String sr = tx5.getText().replaceAll("\\.", "");
			if(!sr.equalsIgnoreCase(sr
						.replaceAll("[^A-za-z0-9_ ]", ""))) return false;
				return true;
			}
		});
		
		configCombo.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
				if(!configCombo.getText().isEmpty()) {
				try {
					wConf.setIndexOfCurrentConfiguration(configCombo.getSelectionIndex());
				String myArguments = wConf.getCurrentConfiguration()
					.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,"");
				String myS = myArguments.substring(
						myArguments.indexOf("-p ")+3);
				if(myS.contains("-")) {
					myS = myS.substring(0,myS.indexOf("-"));
				}
				myS = myS.substring(0,myS.indexOf((new Path(myS)).lastSegment())-1); // -1 because of the last /
				((Text)projectField.getControl()).setText(myS);
				
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				IJavaProject theProject = null;
				for(IProject pro : projects) {
				if(pro.getLocation().toString().contains(myS)) theProject = new JavaProject(pro,null);
		}
				if(theProject != null) wConf = new WizardConfiguration(theProject);
				wizard.setConfigurationName(configCombo.getText());
				wizard.refreshPageTwo();
				wizard.refreshConf(wConf);
				wizard.setResetadv();
				pageValidator.validatePage();
				} catch (CoreException e1) {
					e1.printStackTrace();
				} 
				}
				pageValidator.validatePage();
			}			
		});
		
		// required to avoid an error in the System
		setControl(composite);	
		pageValidator.validatePage();
	}  // end of create control
	
	 @Override
	 public void performHelp() {
		 String[] myText = {"The first Text contains the project's path","The first combo the relative path (from the projects folder) to the sources package",
				 "The second combo the relative path to the test sources","The output folder is the directory where the output files of DSpot will be placed",
				 "The last parameter is a filter in the name of the classes to test, it's optional",""};
		 Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		 DspotWizardHelpDialog info = new DspotWizardHelpDialog(shell, " This page contains the information to write the properties file for DSpot ",myText);
		 info.open();
	 }  
	
	 /**
	  * @return an String array with the information set by the user in this page
	  */
	public String[] getTheProperties() {
		return TheProperties;
	}
	
	private IJavaProject showProjectDialog() {
		
		Class<?>[] acceptedClasses = new Class[] {IJavaProject.class,IProject.class};
		TypedElementSelectionValidator validator = new TypedElementSelectionValidator(acceptedClasses,true);
		ViewerFilter filter= new TypedViewerFilter(acceptedClasses) {
			@Override
			public boolean select(Viewer viewer,Object parentElement, Object element) {
				if(element instanceof IProject) {
					try {
						return ((IProject)element).hasNature(DSpotWizardConstants.MAVEN_NATURE);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
				if(element instanceof IJavaProject) {
					try {
						return ((IJavaProject)element).getProject().hasNature(DSpotWizardConstants.MAVEN_NATURE);
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
	private void createConfigurationField(Composite composite) {
		
		createLabel(composite,"New Configuration : ","lbNewConfig"); 
		
		configurationField = valKit.createTextField(composite, new IFieldValidator<String>() {
			boolean flag; // two possible error messages
			@Override
			public String getErrorMessage() {
				if(flag) return "Configuration name contains not allowed characters";
				return "Configuration name is empty";
			}
			@Override
			public String getWarningMessage() { return null; }
			@Override
			public boolean isValid(String content) {
				pageValidator.validatePage();          
				if(configCombo.isEnabled()) return true;
				if(content.isEmpty()) {
					flag = false; return false;
				}
				if(!content.equalsIgnoreCase(content.replaceAll("[^A-Za-z0-9\\.\\-_ ]",""))) {
					flag = true; return false;
				}
				wizard.setConfigurationName(content);
				setPageComplete(true); return true;
			}
			
			@Override
			public boolean warningExist(String content) { return false; }	
		}, false, "Type configuration name");
		
		Text text = (Text)configurationField.getControl();
		GridDataFactory.fillDefaults().grab(true, false).indent(10, 8).applyTo(configurationField.getControl());
		row.addWidget(text);
		
		configurationField.setQuickFixProvider(new IQuickFixProvider<String>() {
			@Override
			public boolean doQuickFix(ValidatingField<String> field) {  
				String result = field.getContents().replaceAll("[^A-Za-z0-9_\\-\\. ]","");
				if(result.isEmpty()) {
					((Text)field.getControl()).setText("DSpot_configuration");
					return true;
				}
			    ((Text)field.getControl()).setText(result);
				return true;
			}
			@Override
			public String getQuickFixMenuText() {
				return "fix problems";
			}

			@Override
			public boolean hasQuickFix(String contents) {
				if(contents.isEmpty()) return true;
				return !contents.equalsIgnoreCase(contents.replaceAll("[A-Za-z0-9\\.\\-_ ]",""));
			}
			
		});
		
		pageValidator.addElement(new IDSpotPageElement() {
			@Override
			public boolean validate() {
				String sr = configurationField.getContents();
				if(configurationField.getControl().isEnabled()) { 
					if(!sr.isEmpty() && sr.equalsIgnoreCase(sr
						.replaceAll("[^A-Za-z0-9_\\.\\- ]",""))) return true;
					return false;
				}
				return true;
			}
		});
		
		Button btNewConfig = new Button(composite,SWT.CHECK); // button to enable the new dialog text
		GridDataFactory.swtDefaults().indent(0, 8).applyTo(btNewConfig);
		btNewConfig.setToolTipText(tooltipsProperties.getProperty("btNewConfig"));
		btNewConfig.setSelection(true);
		row.addWidget(btNewConfig);
		
		btNewConfig.addSelectionListener(new SelectionAdapter() { // selection listener of the 
	        @Override                                    // new configuration check button
	        public void widgetSelected(SelectionEvent e) {
	        	if(btNewConfig.getSelection()) {
	        		configCombo.setEnabled(false);
	        		text.setEnabled(true);
	        		text.setText(" Type configuration name ");
	        		configCombo.setText("");
	        		configurationComboField.validate();
	        	} else {
	        		text.setEnabled(false);
	        		configCombo.setEnabled(true);
	        		text.setText("");
	        		configurationComboField.validate();
	        	}
	        }
});
	}
	private void createConfigurationComboValidator() {
		
		configurationComboField = valKit.createField(configCombo,new IFieldValidator<String>(){
			@Override
			public String getErrorMessage() {
				return "Select a configuration or create a new one";
			}
			@Override
			public String getWarningMessage() { return null;
			}
			@Override
			public boolean isValid(String sr) {
				if(configCombo.isEnabled() && configCombo.getText().equalsIgnoreCase("")) 
					return false;
				return true;
			}
			@Override
			public boolean warningExist(String sr) { return false;
			}	
			
		},false,"");
	}
	private void createProjectField(Composite composite) {
		
		createLabel(composite,"Path of the project : ","lb1");
		
		// Obtain the path of the project
		String direction = wConf.getProjectPath();
		
		projectField = valKit.createTextField(composite, new IFieldValidator<String>() {
			@Override
			public String getErrorMessage() { return "Project's directory not found"; }
			
			@Override
			public String getWarningMessage() { return null; }
			
			@Override
			public boolean isValid(String content) {
				pageValidator.validatePage();     
				File file = new File(content);
				if(file.exists())if(file.isDirectory()) {
	        		TheProperties[0] = content;  // Project's path
					return true;
				}
				return false;
			}
			
			@Override
			public boolean warningExist(String content) { return false; }
			
		},true,direction);	
		
		pageValidator.addElement(new IDSpotPageElement() {
			@Override
			public boolean validate() {
				File file = new File(projectField.getContents());
				if(file.exists() && file.isDirectory()) return true;
				return false;
			}
		});
		
		Text text = (Text)projectField.getControl();
		GridDataFactory.fillDefaults().grab(true,false).indent(10,8).applyTo(text);
		TheProperties[0] = direction;
		row.addWidget(text);
		 
	        projectSelectionbt = new Button(composite,SWT.PUSH);
	        GridDataFactory.swtDefaults().indent(0, 8).applyTo(projectSelectionbt);
			projectSelectionbt.setText("Select a Project");
			projectSelectionbt.setToolTipText(tooltipsProperties.getProperty("projectSelectionbt"));
			row.addWidget(projectSelectionbt);
			
			projectSelectionbt.addSelectionListener(new SelectionAdapter() {
			    @Override
			    public void widgetSelected(SelectionEvent e) {
			    	IJavaProject jPro = showProjectDialog();
			    	if(jPro != null) {
			        try {
						wConf = new WizardConfiguration(jPro);
					} catch (CoreException e1) {
						e1.printStackTrace();
					}
			    	text.setText(wConf.getProjectPath());
			    	TheProperties[0] = wConf.getProjectPath();
	                sourcePathCombo.removeAll(); sourceTestCombo.removeAll();
			        for(int i = 0; i < wConf.getSources().length; i++) {  // add the sources to the combo
			        	if(wConf.getIsTest()[i]) {  // if it is not a test package
			        	sourceTestCombo.add( wConf.getSources()[i]);} else { sourcePathCombo.add( wConf.getSources()[i]); }
			        } // end of the for
			    	wizard.refreshConf(wConf);
			    	configCombo.setEnabled(false);
			    	configCombo.setText("");
			    	configurationField.getControl().setEnabled(true);
			    	((Text)configurationField.getControl()).setText("Type configuration name");
			    	String[] sour = wConf.getSources(); 
					boolean[] isTest = wConf.getIsTest();  // the packages in sour with test classes
					sourcePathCombo.removeAll();
					sourceTestCombo.removeAll();
			        for(int i = 0; i < sour.length; i++) {  // add the sources to the combo
			        	if(isTest[i]) {  // if it is not a test package
			        	sourceTestCombo.add(sour[i]);} else { sourcePathCombo.add(sour[i]); }
			        } // end of the for
			        
			        if(sourcePathCombo.getItems().length > 0) {
			        	sourcePathCombo.setText(sourcePathCombo.getItem(0));
			    		TheProperties[1] = sourcePathCombo.getText();  // the path of the source
			        }
			        if(sourceTestCombo.getItems().length > 0) {
			        	sourceTestCombo.setText(sourceTestCombo.getItem(0));
			    		TheProperties[2] = sourceTestCombo.getText();    //  testSrc
			        }
			        
			    	wizard.setDefaultValuesInPage2();
			        }
			    }
				});
	}
			
	private void createLabel(Composite composite, String text,String tooltipKey) {
		Label label = new Label(composite,SWT.NONE);
		//GridDataFactory.swtDefaults().indent(0, 8).applyTo(label);
		label.setText(text);
		label.setToolTipText(tooltipsProperties.getProperty(tooltipKey));
		row.addWidget(label);
	}	
	/**
	 *  inner class to handle the field validation error messages
	 */
		public class WizardErrorHandler implements IFieldErrorMessageHandler{
		@Override
		public void clearMessage() {
			setErrorMessage(null);
			setMessage(null,DialogPage.WARNING);	
		}
		@Override
		public void handleErrorMessage(String message, String input) {
		 setMessage(null,DialogPage.ERROR);
		 setErrorMessage(message);	
		}
		@Override
		public void handleWarningMessage(String message, String input) {
		 setErrorMessage(null);
		 setMessage(message,DialogPage.WARNING);	
		}
		
	}	
		private class DSpotPage1Validator{
			
		 ArrayList<IDSpotPageElement> list = new ArrayList<IDSpotPageElement>(5);
		 
		 void addElement(IDSpotPageElement element) {
			 list.add(element);
		 }
		 
		 void validatePage() {
			 for(IDSpotPageElement element : list)if(!element.validate()) {
				 setPageComplete(false); return;
			 }
			 setPageComplete(true);
		 }
		}
		
		private interface IDSpotPageElement{ public boolean validate(); }
}

