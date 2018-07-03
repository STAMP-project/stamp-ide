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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.List;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;

import eu.stamp.eclipse.dspot.wizard.page.utils.DSpotPageSizeCalculator;
import eu.stamp.eclipse.dspot.wizard.page.utils.DSpotRowSizeCalculator;
import eu.stamp.eclipse.dspot.wizard.page.utils.DSpotSizeManager;
import eu.stamp.wp4.dspot.constants.DSpotWizardConstants;
import eu.stamp.wp4.dspot.dialogs.*;
import eu.stamp.wp4.dspot.wizard.utils.DSpotMemory;
import eu.stamp.wp4.dspot.wizard.utils.WizardConfiguration;

/**
 * this class describes the second page of the DSpot wizard 
 */
@SuppressWarnings("restriction")
public class DSpotWizardPage2 extends WizardPage {
	
	private WizardConfiguration wConf; 
	private Wizard wizard;
	private String[] amplifiers = {"StringLiteralAmplifier","NumberLiteralAmplifier","CharLiteralAmplifier",
			"BooleanLiteralAmplifier","AllLiteralAmplifiers","MethodAdd","MethodRemove","TestDataMutator",
			"StatementAdd",""};  // the possible amplifiers;
	
	// widgets 
	private Text executionClassesText;
	private Spinner spin;
	private Spinner spin1; 
	private List amplifiersList;
	private Combo combo1;
	private Button button;
	private Button cleanButton;
	private  Button commentButton;
	
	// Dialogs
	private ArrayList<Object> testSelection = new ArrayList<Object>(1);
	// this is for the advanced dialog
	private int r = 23;
	private int timeOut = 10000;
	private String casesToTest = "";
	private String pathPitResult = "";
	private Shell shell;
	private String[] selectedCases = {""};
	private DSpotAdvancedOptionsDialog expDiag;
	private boolean resetAdvancedOptions = false;
	
    // to compute size
    private DSpotPageSizeCalculator sizeCalculator;
    private final DSpotRowSizeCalculator row;
	
	public DSpotWizardPage2(WizardConfiguration wConf,Wizard wizard) {
		super("DSpot execution");
		setTitle("DSpot execution");
		setDescription("Configuration of the DSpot execution");
		this.wConf = wConf;
		this.wizard = wizard;
		shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		expDiag = new DSpotAdvancedOptionsDialog(shell, wConf);
		expDiag.setMemory(wConf.getDSpotMemory());
		sizeCalculator = new DSpotPageSizeCalculator();
		row = new DSpotRowSizeCalculator();
	} 
	
	@Override
	public void createControl(Composite parent) {
		
		Properties tooltipsProperties = new Properties();
		final URL propertiesURL = FileLocator.find(Platform.getBundle(
				DSpotWizardConstants.PLUGIN_NAME),
				new Path("files/dspot_tooltips2.properties"),null);
		      InputStream inputStream;
		
	    try {
		inputStream = propertiesURL.openStream();
		tooltipsProperties.load(inputStream);
		inputStream.close();} catch (IOException e2) {
			e2.printStackTrace(); }
		
		// create the composite
		Composite composite = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();    // the layout of the composite
		int n = 4;
		layout.numColumns = n;
		composite.setLayout(layout);
		/*
		 *   ROW 1 : number of iterations
		 */
		row.reStart();
		Label lb1 = new Label(composite,SWT.NONE);  // A label in (1,1)
		lb1.setText("Number of iterations :  ");
		lb1.setToolTipText(tooltipsProperties.getProperty("lb1"));
		row.addWidget(lb1);
		
		GridData gd = new GridData(SWT.FILL,SWT.FILL,true,false);
		gd.heightHint = 20;
		gd.horizontalSpan = 2;
		
		spin = new Spinner(composite,SWT.NONE); // A spinner in  (1,2)
		spin.setMinimum(1);                             // for the number of iterations i
		spin.setLayoutData(gd);
		spin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(spin.getSelection() > 0);				
			}
		});
		row.addWidget(spin);
		
		Label space1 = new Label(composite,SWT.NONE);    // (1,3)
		space1.setText("");
		gd = new GridData(SWT.FILL,SWT.FILL,false,false);
		space1.setLayoutData(gd);
		
		sizeCalculator.addRow(row);
		/*
		 *   Row 2 : execution classes 
		 */
		row.reStart();
		Label lb2 = new Label(composite,SWT.NONE);   // A label in (2,1)
		lb2.setText("Execution classes :  ");
		lb2.setToolTipText(tooltipsProperties.getProperty("lb2"));
		row.addWidget(lb2);

		executionClassesText = new Text(composite,SWT.BORDER | SWT.READ_ONLY);  // A text in (2,2) for the execution classes
		executionClassesText.setText("");
		gd = new GridData(SWT.FILL,SWT.FILL,true,false);
		gd.verticalIndent = 8;
		gd.horizontalSpan = 2;
		executionClassesText.setLayoutData(gd);
		row.addWidget(executionClassesText);
     
     Button fileButton = new Button(composite,SWT.PUSH); // A button in (2,3), it opens the file dialog
     fileButton.setText("Select tests");
     fileButton.setToolTipText(tooltipsProperties.getProperty("fileButton"));
     row.addWidget(fileButton);
     fileButton.addSelectionListener(new SelectionAdapter() {
    	 @Override
    	 public void widgetSelected(SelectionEvent e) {
    		 
 			try {
			showElementTreeSelectionDialog2(wConf.getPro(),wConf.getTheWindow());
			} catch (JavaModelException e1) {
				e1.printStackTrace();
			} 

    	 }
     }); // end of the selection listener
     sizeCalculator.addRow(row);
     	/*
     	 *   Row 3 : Amplifiers	
     	 */
        row.reStart();
		Label lb3 = new Label(composite,SWT.NONE);   // A label in (3,1)
		lb3.setText("Amplifier :  ");
		lb3.setToolTipText(tooltipsProperties.getProperty("lb3"));
		row.addWidget(lb3);
		
		amplifiersList = new List(composite,SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);  // list to select the amplifiers
		gd = new GridData(SWT.FILL,SWT.FILL,true,false);
	    gd.grabExcessVerticalSpace = true;
		gd.horizontalSpan = n-1;
		amplifiersList.setLayoutData(gd);
		for(int i = 0; i < amplifiers.length -1; i++) {
			amplifiersList.add(amplifiers[i]);
		}
		row.addWidget(amplifiersList);
		sizeCalculator.addRow(row);
		/*
		 *  Row 4 : criterion
		 */
		row.reStart();
	    Label lb5 = new Label(composite,SWT.NONE); // A label in (5,1)
	    lb5.setText("Test Criterion : ");
	    lb5.setToolTipText(tooltipsProperties.getProperty("lb5"));
	    row.addWidget(lb5);
	    
	    combo1 = new Combo(composite,SWT.BORDER | SWT.READ_ONLY);  // combo for the test criterion in (4,2)
	    gd = new GridData(SWT.FILL,SWT.FILL,true,false);
	    gd.horizontalSpan = n-1;
	    combo1.setLayoutData(gd);  // setting the criterions in the combo
	    combo1.add("PitMutantScoreSelector"); combo1.add("ExecutedMutantSelector");
	    combo1.add("CloverCoverageSelector"); combo1.add("BranchCoverageTestSelector");
	    combo1.add("JacocoCoverageSelector"); combo1.add("TakeAllSelector");
	    combo1.add("ChangeDetectorSelector"); combo1.add("");
	    row.addWidget(combo1);
	    sizeCalculator.addRow(row);
	    /*
	     *  Row 5 : Max test amplified
	     */
	    row.reStart();
	    Label lb6 = new Label(composite,SWT.NONE);  // A label in (5,1)
	    lb6.setText("Max test amplified : ");
	    lb6.setToolTipText(tooltipsProperties.getProperty("lb6"));
	    row.addWidget(lb6);
	    
	    spin1 = new Spinner(composite,SWT.BORDER);
	    spin1.setMinimum(50); spin1.setIncrement(50); spin1.setMaximum(4000); spin1.setSelection(200);
	    spin1.setLayoutData(gd);
	    row.addWidget(spin1);
        sizeCalculator.addRow(row);
        /*
         *   Row 6 : Verbose
         */
	    row.reStart();
	    Label lb7 = new Label(composite,SWT.NONE); // A label in (6,1)
	    lb7.setText("Verbose ");
	    row.addWidget(lb7);
	    
	    button = new Button(composite,SWT.CHECK);  // check button in (6,2)
        button.setToolTipText(tooltipsProperties.getProperty("button"));
        row.addWidget(button);
	    
	    Label space = new Label(composite,SWT.NONE);
	    space.setText("");
	    row.addWidget(space);
	    
	    Link link = new Link(composite,SWT.NONE);  // this link in (6,4) open the dialog with the advanced options
	    link.setText("<A>Dspot advanced options</A>");
	    link.setToolTipText(tooltipsProperties.getProperty("link"));
	    row.addWidget(link);
	    link.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {

	    		if(resetAdvancedOptions) {
	    			expDiag.reset(wConf, r, timeOut, selectedCases, pathPitResult);
	    			resetAdvancedOptions = false;
	    		} else expDiag.setMemory(wConf.getDSpotMemory());
	    		expDiag.setPitSelected(combo1.getText().contains("PitMutantScoreSelector"));
	    		if(expDiag.open() == Dialog.OK) wConf.setDSpotMemory(expDiag.getMemory());
	    		
	    	}
	    }); // end of the selection listener
	    sizeCalculator.addRow(row);
	    /*
	     *  Row 7 : check buttons
	     */
	    row.reStart();
	    cleanButton = new Button(composite,SWT.CHECK);  // check button in (6,2)
	    cleanButton.setText("clean ");
	    cleanButton.setToolTipText(tooltipsProperties.getProperty("button2"));
	    row.addWidget(cleanButton);
	    
	    Label space2 = new Label(composite,SWT.NONE);
	    space2.setText("");
	    row.addWidget(space2);
	    
	    // comment button 
	    commentButton = new Button(composite,SWT.CHECK);
	    commentButton.setText("with comment ");
	    row.addWidget(commentButton);
	    sizeCalculator.addRow(row);
	    DSpotSizeManager.getInstance().addPage(sizeCalculator);
	    DSpotSizeManager.getInstance().configureWizardSize(wizard);
	    
		setControl(composite);
		setPageComplete(true);
	}  // end of create Control
	/**
	 * Method to create and show a dialog to select test classes
	 * @param jProject : the selected project
	 * @param window : the active workbench window
	 * @return a string with the class selected by the user
	 * @throws JavaModelException
	 */
    private String showElementTreeSelectionDialog2(IJavaProject jProject, IWorkbenchWindow window) throws JavaModelException {
        Class<?>[] acceptedClasses= new Class[] { IPackageFragmentRoot.class, IJavaProject.class, IJavaElement.class };
        TypedElementSelectionValidator validator= new TypedElementSelectionValidator(acceptedClasses, true) {
            @Override
            public boolean isSelectedValid(Object element) { // this method is override to specify what objects can be selected
                
                    if (element instanceof ICompilationUnit) {
                        return true;
                    }
                    return false;
                
            }
        };

        acceptedClasses= new Class[] { IJavaModel.class, IPackageFragmentRoot.class, IJavaProject.class, IJavaElement.class, ICompilationUnit.class };
        ViewerFilter filter= new TypedViewerFilter(acceptedClasses) {
            @Override
            public boolean select(Viewer viewer, Object parent, Object element) {
                if (element instanceof IJavaProject) {
                    if (!((IJavaProject) element).getElementName().equals(jProject.getElementName())) {
                        return false;
                    }
                }
                if (element instanceof IPackageFragment) {
                    try {
                        return containsTestClasses ((IPackageFragment)element);
                    } catch (JavaModelException e) {
                        JavaPlugin.log(e.getStatus()); // just log, no UI in validation
                        return false;
                    }
                }
                if (element instanceof JarPackageFragmentRoot) {
                    return false;
                }
                return super.select(viewer, parent, element);
            }

            private boolean containsTestClasses(IPackageFragment element) throws JavaModelException {
                boolean result = false;
                if (!(element instanceof JarPackageFragmentRoot)) {
                    for (IJavaElement child:element.getChildren()) {
                        if (child instanceof ICompilationUnit && isTestClass (child)) {
                            result = true;
                            break;
                        }
                    }
                } 
                return result;
            }

            private boolean isTestClass(IJavaElement child) {
                // Detect test class by inspecting JUnit annotations
            	try {
					return wConf.lookForTest(wConf.getqName(child.getElementName()));
				} catch (JavaModelException e) {
					e.printStackTrace(); return child.getElementName().toLowerCase().contains("test");
				}
            }
        };
        
        IWorkspaceRoot fWorkspaceRoot= ResourcesPlugin.getWorkspace().getRoot();
        IJavaElement initElement = jProject.getPackageFragmentRoots()[0];
        
        StandardJavaElementContentProvider provider= new StandardJavaElementContentProvider();
        ILabelProvider labelProvider= new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
        ElementTreeSelectionDialog dialog= new ElementTreeSelectionDialog(window.getShell(), labelProvider, provider);
        dialog.setValidator(validator);
        dialog.setComparator(new JavaElementComparator());
        dialog.setTitle(" Select a test-class ");
        dialog.setMessage(" Select a class file ");
        dialog.addFilter(filter);
        dialog.setInput(JavaCore.create(fWorkspaceRoot));
        dialog.setInitialSelection(initElement);
        dialog.setHelpAvailable(false);
        if(!testSelection.isEmpty()) {
        	dialog.setInitialSelections(testSelection.toArray());
        }  
        
        String selection = "";
        if (dialog.open() == Window.OK) {
            Object[] results = dialog.getResult();
            testSelection = new ArrayList<Object>(1);
            for(Object ob : results) {
            	testSelection.add(ob);
            }
            for(Object ob : results) {
            	if(ob instanceof ICompilationUnit) { 
            		if(!selection.isEmpty()) {
             selection = selection + WizardConfiguration
            		 .getSeparator() + wConf.getqName(((ICompilationUnit)ob).getElementName());}
            		else{ selection = wConf.getqName(((ICompilationUnit)ob).getElementName()); }}
            }
            executionClassesText.setText(selection);
        }
        return selection;
    }
/**
 * this method updates the information in the page when a configuration has been selected in page one
 */
    public void refresh() {

    ILaunchConfiguration config = wConf.getCurrentConfiguration();
	 String argument = null;
	 DSpotMemory dSpotMemory = wConf.getDSpotMemory();
	 try {
	 argument = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,"");
	 System.out.println(argument);
	 dSpotMemory = wConf.getDSpotMemory().resetFromString(argument);
	} catch (CoreException e) {
		e.printStackTrace();
	}
   if(!argument.isEmpty()) {
   	spin.setSelection(Integer.parseInt(dSpotMemory.getDSpotValue(DSpotMemory.ITERATIONS_KEY)));
   	
   	if(dSpotMemory.getDSpotValue(DSpotMemory.TEST_CLASSES_KEY) != null) {
   		executionClassesText.setText(dSpotMemory.getDSpotValue(DSpotMemory.TEST_CLASSES_KEY));
   	IJavaElement[] children = wConf.getFinalChildren(wConf.getPro());
   	testSelection = new ArrayList<Object>(1);
   	for(IJavaElement child : children) {
   		if(dSpotMemory.getDSpotValue(DSpotMemory.TEST_CLASSES_KEY)
   				.contains(child.getElementName().replaceAll(".java", ""))) testSelection.add(child);
   	}}

   	ArrayList<Integer> indices = new ArrayList<Integer>(1);
   	for(int i = 0; i < amplifiers.length; i++) {
   		if(dSpotMemory.getDSpotValue(DSpotMemory.AMPLIFIERS_KEY).contains(amplifiers[i])) {
   			indices.add(new Integer(i));
   		}
   	}
   	int[] theIndices = new int[indices.size()];
   	for(int i = 0; i < indices.size(); i++) {
   		theIndices[i] = indices.get(i).intValue();
   	}
   	amplifiersList.setSelection(theIndices);

	if(argument.contains(DSpotMemory.CRITERION_KEY)) {
   	combo1.setText(dSpotMemory.getDSpotValue(DSpotMemory.CRITERION_KEY));
	}

   	if(argument.contains(DSpotMemory.MAX_TEST_KEY))
   	spin1.setSelection(Integer.parseInt(dSpotMemory.getDSpotValue(DSpotMemory.MAX_TEST_KEY)));
   	
   	if(argument.contains(DSpotMemory.RANDOMSEED_KEY))
   		r = Integer.parseInt(dSpotMemory.getDSpotValue(DSpotMemory.RANDOMSEED_KEY));
   	
   	if(argument.contains(DSpotMemory.TIMEOUT_KEY))
   		timeOut = Integer.parseInt(dSpotMemory.getDSpotValue(DSpotMemory.TIMEOUT_KEY));
   	
   	if(argument.contains(DSpotMemory.TEST_CASES_KEY)) {
        casesToTest = dSpotMemory.getDSpotValue(DSpotMemory.TEST_CASES_KEY);
        String[] allCases = wConf.getTestMethods();
        ArrayList<String> casesList = new ArrayList<String>(1);
        for(String sr : allCases) {
        	if(casesToTest.contains(sr)) casesList.add(sr);
        }
       selectedCases =  casesList.toArray(new String[casesList.size()]);
     	}
   	
   	if(argument.contains("-m ")) {
        pathPitResult = dSpotMemory.getDSpotValue(DSpotMemory.PATH_PIT_RESULT_KEY);
     	} else pathPitResult = "";
   	if(argument.contains("verbose")) { button.setSelection(true); dSpotMemory.setDSpotValue("verbose", "true"); }
   	else { button.setSelection(false); dSpotMemory.setDSpotValue("verbose", "false"); }
   	if(argument.contains("clean")) {cleanButton.setSelection(true); dSpotMemory.setDSpotValue("clean", "true"); }
   	else { cleanButton.setSelection(false); dSpotMemory.setDSpotValue("clean", "false"); }
   	if(argument.contains("comment")) { commentButton.setSelection(true); dSpotMemory.setDSpotValue("comment","true"); }
   	else { commentButton.setSelection(false); dSpotMemory.setDSpotValue("comment","false"); }
   	expDiag.setMemory(dSpotMemory);
   	expDiag.resetFromMemory();
   	wConf.setDSpotMemory(dSpotMemory);
   }
    }

     public WizardConfiguration getConfiguration() {
    	 DSpotMemory memory = wConf.getDSpotMemory();
    	 if(expDiag.dialogUnused()) {
    		 memory.setDSpotValue(DSpotMemory.RANDOMSEED_KEY, String.valueOf(r));
    		 memory.setDSpotValue(DSpotMemory.TIMEOUT_KEY, String.valueOf(timeOut));
    		 memory.setDSpotValue(DSpotMemory.PATH_PIT_RESULT_KEY, pathPitResult);
    		 String selection = "";
    		 String sep = WizardConfiguration.getSeparator();
    		 for(int i = 0; i < selectedCases.length -1; i++) {
    			 selection = selection + selectedCases[i] + sep;
    		 }
    		selection = selection + selectedCases[selectedCases.length-1];	 
    		memory.setDSpotValue(DSpotMemory.TEST_CASES_KEY, selection);
    	 }
    	 memory.setDSpotValue(DSpotMemory.MAX_TEST_KEY, String.valueOf(spin1.getSelection()));
    	 memory.setDSpotValue(DSpotMemory.TEST_CLASSES_KEY, executionClassesText.getText());
    	 memory.setDSpotValue(DSpotMemory.ITERATIONS_KEY, String.valueOf(spin.getSelection()));
    	 memory.setDSpotValue(DSpotMemory.CRITERION_KEY, combo1.getText());
    	 String[] selection = amplifiersList.getSelection();
 		if(selection != null && selection.length > 0) {
 			String amplList = selection[0];
 		for(int i = 1; i < selection.length; i++) {
 			amplList = amplList + WizardConfiguration.getSeparator() + selection[i];	
 		}
 		memory.setDSpotValue(DSpotMemory.AMPLIFIERS_KEY,amplList);}
 		//if(button.getSelection()) memory.setDSpotValue("verbose", "true");
 		//else memory.setDSpotValue("verbose", "false");
 		 memory.setDSpotValue("verbose", String.valueOf(button.getSelection()));
 	     memory.setDSpotValue("clean", String.valueOf(cleanButton.getSelection()));
 	     memory.setDSpotValue("comment", String.valueOf(commentButton.getSelection()));
    	 wConf.setDSpotMemory(memory);	 
    	 return wConf;
     }
/**
 * this method is called when the project is changed
 * @param wConf : the new configuration
 */
	public void refreshPageConfiguration(WizardConfiguration wConf) {
		this.wConf = wConf;
		
	}
	/**
	 *  set default DSpot execution values
	 */
	
	public void setDefaultValues() {
		executionClassesText.setText("");
		spin.setSelection(1);
		spin1.setSelection(200);
		amplifiersList.deselectAll();
		combo1.setText("");
		expDiag.reset(wConf, 23, 10000, null , "");
		
	}
	 @Override
	 public void performHelp() {
		 String[] myText = {"First spinner, number of amplification iterations : A larger number may help to kill more mutants but it has an impact on the execution time",
		"First text, test-classes to use","the option verbose prints more information about the process in the console",
		"the option clean removes the out dirctory if exists, else it will append the results to the exist files",
		"","The link DSpot advanced options opens a dialog to set the time value of degenerated test (ms), the randomSeed, the MAVEN_HOME ",
		"and the path to the .csv of the original result of Pit test, (this only avaiable if the test criterion is PitMutantScoreSelector)",""};
		 DspotWizardHelpDialog info = new DspotWizardHelpDialog(shell," This page contains the information to execute DSpot ",myText);
		 info.open();
	    
	 }
	 
	 public void setResetAdvancedOptions(boolean resetAdvancedOptions) {
		 this.resetAdvancedOptions = resetAdvancedOptions;
	 }
}
