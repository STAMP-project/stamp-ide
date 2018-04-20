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
package eu.stamp.wp4.dspot.dialogs;

import java.text.Collator;
import java.util.ArrayList;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;

import eu.stamp.wp4.dspot.wizard.utils.WizardConfiguration;

/**
 *  This class describes a dialog to set the advanced options of Dspot execution,
 *  it will be open by a link called advanced options in page 2
 */
public class DSpotAdvancedOptionsDialog extends Dialog{
	
	private WizardConfiguration wConf;  // to obtain the possible test cases
	
	// parameters  
	//private int randomSeed = 23;
	private String[] selection;
	private String pathPitResult = "";
	private String mavenHome;
	
	private boolean pitSelected = false;
	
	private DSpotAdvancedOptionsDialogMemory memory;
	
	// widgets
	private Spinner timeOutSpinner;
	private Spinner randomSeedSpinner;
	private List list;
	private Text pathPitResultText;
	private Text mavenHomeText;
	

	public DSpotAdvancedOptionsDialog(Shell parentShell, WizardConfiguration wConf) {
		super(parentShell);
		this.wConf = wConf;
	}
     @Override
     protected Control createDialogArea(Composite parent) {
    	 
    	 /*
    	  *  Row 1 : timeOut
    	  */
    	 Composite composite = (Composite)super.createDialogArea(parent);
 		 GridLayout layout = new GridLayout();
 		 layout.numColumns = 3;
 		 composite.setLayout(layout);
 		 int vSpace = 8;
 		 
 		 Label timeOutLabel = new Label(composite,SWT.NONE);
 		 timeOutLabel.setText("Time out (ms) ");
 		 GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).indent(0, vSpace).applyTo(timeOutLabel);
 		 
 		 timeOutSpinner = new Spinner(composite,SWT.BORDER);
 		 GridDataFactory.fillDefaults().span(2, 1).grab(true, false).indent(0, vSpace).applyTo(timeOutSpinner);
 		 timeOutSpinner.setMaximum(100000); timeOutSpinner.setMinimum(500); timeOutSpinner.setIncrement(100);
 		 timeOutSpinner.setSelection(memory.getTimeOut());
 		 
 		 /*
 		  *  Row 2 : randomSeed
 		  */
 		 int randomSeed = memory.getRandomseed();
 		 Label randomSeedLabel = new Label(composite,SWT.NONE);
 		 randomSeedLabel.setText("random seed : ");
 		 GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).indent(0, vSpace).applyTo(randomSeedLabel);
 		 
 		 randomSeedSpinner = new Spinner(composite,SWT.BORDER);
 		 randomSeedSpinner.setMinimum(1); randomSeedSpinner.setSelection(randomSeed);
 		 GridDataFactory.fillDefaults().span(2, 1).grab(true, false).indent(0, vSpace).applyTo(randomSeedSpinner);
 		 
 		 /*
 		  *  Row 3 : list for the test cases
 		  */
 		 Label listLabel = new Label(composite,SWT.NONE);
 		 listLabel.setText("test cases : ");
 		 GridDataFactory.fillDefaults().align(SWT.LEFT,SWT.CENTER).indent(0, vSpace).applyTo(listLabel);
    	 
    	 list = new List(composite,SWT.MULTI);
    	 String[] cases = wConf.getTestCases();
    	 TreeSet<String> casesSet = new TreeSet<String>(Collator.getInstance());
    	 for(String sr : cases) casesSet.add(sr);
    	 cases = casesSet.toArray(new String[casesSet.size()]);
    	 for(String sr : cases) list.add(sr);
    	 GridDataFactory.fillDefaults().grab(true, false).span(2,1).indent(0, vSpace).applyTo(list);
    	 if(selection != null) {
    	 if(selection.length > 0) list.setSelection(selection);}
    	 
    	 /*
    	  *  Row 4 : button to clean the test cases list
    	  */
    	 Label buttonLabel = new Label(composite,SWT.NONE);
    	 buttonLabel.setText(" push to deselect all test cases : ");
    	 GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).indent(0, vSpace).applyTo(buttonLabel);
    	 
    	 Button button = new Button(composite,SWT.PUSH);
    	 button.setText("clean list");
    	 GridDataFactory.swtDefaults().span(2, 1).align(SWT.LEFT, SWT.CENTER).indent(0, vSpace).applyTo(button);
    	 
    	 /*
    	  *  Row 5 : pathPitResult
    	  */
    	 Label pathPitResultLabel = new Label(composite,SWT.NONE);
    	 pathPitResultLabel.setText("path pit result : ");
    	 GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).indent(0, vSpace).applyTo(pathPitResultLabel);
    	 
    	 Button pathPitResultButton = new Button(composite,SWT.NONE);
    	 pathPitResultButton.setText("Select folder");
    	 pathPitResultButton.setEnabled(pitSelected);
    	 GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER).indent(0, vSpace).applyTo(pathPitResultButton);
    	 
    	 pathPitResultText = new Text(composite,SWT.BORDER);
    	 pathPitResultText.setEnabled(pitSelected);
    	 pathPitResultText.setText(pathPitResult);
    	 GridDataFactory.fillDefaults().span(1, 1).grab(true, false).indent(0, vSpace).applyTo(pathPitResultText);
    	 
    	 /*
    	  *  Row 6 : MAVEN_HOME
    	  */
    	 Label mavenLabel = new Label(composite,SWT.NONE);
    	 mavenLabel.setText(" set MAVEN_HOME");
    	 
    	 Button mavenHomeButton = new Button(composite,SWT.CHECK); 
    	 GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER).indent(0, vSpace).applyTo(mavenHomeButton);
    	 
    	 mavenHomeText = new Text(composite,SWT.BORDER);
    	 mavenHomeText.setText(System.getenv("MAVEN_HOME"));
    	 mavenHomeText.setEnabled(false);
    	 GridDataFactory.fillDefaults().span(1, 1).grab(true, false).indent(0, vSpace).applyTo(mavenHomeText);
    	 

    	 // listeners
    	 button.addSelectionListener(new SelectionAdapter() {
    		 @Override
    		 public void widgetSelected(SelectionEvent e) {
    			 list.deselectAll();
    		 }
    	 });
    	 pathPitResultButton.addSelectionListener(new SelectionAdapter() {
    		 @Override
    		 public void widgetSelected(SelectionEvent e) {
    			 DirectoryDialog dialog = new DirectoryDialog(
    					 PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),SWT.OK);
    			 dialog.setText("Select a folder");
    			 String directoryPath = dialog.open();
    			 if(directoryPath != null && !directoryPath.isEmpty()) {
    				 pathPitResultText.setText(directoryPath);
    				 pathPitResult = directoryPath;
    			 }
    		 }
    	 });
    	 
    	 mavenHomeButton.addSelectionListener(new SelectionAdapter() {
    		 @Override
    		 public void widgetSelected(SelectionEvent e) {
    			 mavenHomeText.setEnabled(mavenHomeButton.getSelection());
    		 }
    	 });
    	 
    	 return composite;
     }
     
     @Override
     public void okPressed() {
    	 selection = list.getSelection();
    	 //timeOut = timeOutSpinner.getSelection();
    	 //randomSeed = randomSeedSpinner.getSelection();
    	 pathPitResult = pathPitResultText.getText();
    	 mavenHome = mavenHomeText.getText();
    	 memory.setData(timeOutSpinner.getSelection(),randomSeedSpinner.getSelection()
    			 , selection, pathPitResultText.getText());
    	 super.okPressed();
     }
     
 	@Override
 	protected void configureShell(Shell shell) {  // set the title
 		super.configureShell(shell);
 		shell.setText(" Advanced options ");
 	}

    @Override
    protected Point getInitialSize() { // default size of the dialog
        return new Point(600, 600);
    }
     /**
      * this method updates the dialog when a new project is loaded
      * @param wConf
      */
     public void setConfiguration(WizardConfiguration wConf) {
    	 this.wConf = wConf;
     }
     /**
      * If pitSelected is true the text for path to pit results will be enabled
      * @param pitSelected
      */
     public void setPitSelected(boolean pitSelected) {
    	 this.pitSelected = pitSelected;
     }
     /**
      * This method is called when a new configuration is loaded
      * @param wConf
      * @param randomSeed
      * @param timeOut
      * @param selection
      * @param pathPitResult
      */
     public void reset(WizardConfiguration wConf,int randomSeed, int timeOut,String[] selection,String pathPitResult) {
    	 this.wConf = wConf;
    	// this.randomSeed = randomSeed;
    	 memory.setData(timeOut, randomSeed, selection, pathPitResult);
    	 this.pathPitResult = pathPitResult;
    	 String[] cases = wConf.getTestCases();
    	 if(selection != null) {
    	 ArrayList<String> mySelection = new ArrayList<String>(1);
    	 for(String aCase : cases) {
    		 for(String sel : selection) {
    			 if(aCase.contains(sel)) mySelection.add(aCase);
    		 }
    	 }
    	 this.selection = mySelection.toArray(new String[mySelection.size()]);}
     }
     /**
      * Method to obtain the information in the dialog
      * @return a string array [0] randomSeed, [1] timeOut (ms),[2] test cases, [3] path pit result,[4] MAVEN_HOME
      */
     public String[] getAdvancedParameters() {
    	 
    	    String[] advParameters = new String[5];                   // this is for the user information
    	    int timeOut = memory.getTimeOut();
    	    int randomSeed = memory.getRandomseed();
    	    if(randomSeed > 0) advParameters[0] = " --randomSeed " + randomSeed; else advParameters[0] = "";
    	    if(timeOut > 0) advParameters[1] = " --timeOut " + timeOut; else advParameters[1] = "";
    	    if(selection.length > 0) {
    	    if(selection[0] != null && !selection[0].isEmpty()) advParameters[2] = " -c " + selection[0].substring(selection[0].indexOf("/")+1);
    	    for(int i = 1; i < selection.length; i++) {
    	    	if(advParameters[i] != null && !advParameters[i].isEmpty()) advParameters[2] = advParameters[2] + 
    	    			WizardConfiguration.getSeparator() + selection[i].substring(selection[i].indexOf("/")+1);}}
    	    if(pathPitResult != null && !pathPitResult.isEmpty()) advParameters[3] = " -m " + pathPitResult;
    	    if(mavenHome != null && !mavenHome.isEmpty()) advParameters[4] = " --maven-home " + mavenHome;
    	    for(int i = 0; i < advParameters.length; i++) {
    	    	if(advParameters[i] == null) advParameters[i] = "";
    	    }
    	    return advParameters;
     }
     public void resetFromMemory() {
    	 //this.randomSeed = memory.getRandomseed();
    	 //this.timeOut = memory.getTimeOut();
    	 this.selection = memory.getSelection();
    	 this.pathPitResult = memory.getPathPitResult();
     }
     public void setMemory(DSpotAdvancedOptionsDialogMemory memory) {
    	 this.memory = memory;
     }
     public DSpotAdvancedOptionsDialogMemory getMemory() {
    	 return memory;
     }
}
