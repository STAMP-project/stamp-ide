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

import eu.stamp.wp4.dspot.wizard.utils.DSpotMemory;
import eu.stamp.wp4.dspot.wizard.utils.WizardConfiguration;

/**
 *  This class describes a dialog to set the advanced options of Dspot execution,
 *  it will be open by a link called advanced options in page 2
 */
public class DSpotAdvancedOptionsDialog extends Dialog{
	
	private WizardConfiguration wConf;  // to obtain the possible test cases
	
	// parameters  
	private int randomSeed = 23;
	//private int timeOut = 10000;
	private String[] selection = {""};
	private String pathPitResult = "";
	private String mavenHome;
	private boolean unusedDialog = true;
	
	private boolean pitSelected = false;
	
	private DSpotMemory memory;
	
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
 		 if(memory.getDSpotValue(DSpotMemory.TIMEOUT_KEY) != null)
 			 timeOutSpinner.setSelection(Integer.parseInt(memory.getDSpotValue(DSpotMemory.TIMEOUT_KEY)));
 		 else timeOutSpinner.setSelection(10000);

 		 /*
 		  *  Row 2 : randomSeed
 		  */
 		 final Label randomSeedLabel = new Label(composite,SWT.NONE);
 		 randomSeedLabel.setText("random seed : ");
 		 GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).indent(0, vSpace).applyTo(randomSeedLabel);
 		 
 		 randomSeedSpinner = new Spinner(composite,SWT.BORDER);
 		 randomSeedSpinner.setMinimum(1); randomSeedSpinner.setSelection(randomSeed);
 		 GridDataFactory.fillDefaults().span(2, 1).grab(true, false).indent(0, vSpace).applyTo(randomSeedSpinner);
 		 
 		 /*
 		  *  Row 3 : list for the test cases
 		  */
 		 final Label listLabel = new Label(composite,SWT.NONE);
 		 listLabel.setText("test cases : ");
 		 GridDataFactory.fillDefaults().align(SWT.LEFT,SWT.CENTER).indent(0, vSpace).applyTo(listLabel);
    	 
    	 list = new List(composite,SWT.MULTI);
    	 String[] cases = wConf.getTestCases();
    	 final TreeSet<String> casesSet = new TreeSet<String>(Collator.getInstance());
    	 for(final String sr : cases) casesSet.add(sr);
    	 cases = casesSet.toArray(new String[casesSet.size()]);
    	 for(final String sr : cases) list.add(sr);
    	 GridDataFactory.fillDefaults().grab(true, false).span(2,1).indent(0, vSpace).applyTo(list);
    	 if(selection != null) {
    	 if(selection.length > 0) list.setSelection(selection);}
    	 
    	 /*
    	  *  Row 4 : button to clean the test cases list
    	  */
    	 final Label buttonLabel = new Label(composite,SWT.NONE);
    	 buttonLabel.setText(" push to deselect all test cases : ");
    	 GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).indent(0, vSpace).applyTo(buttonLabel);
    	 
    	 final Button button = new Button(composite,SWT.PUSH);
    	 button.setText("clean list");
    	 GridDataFactory.swtDefaults().span(2, 1).align(SWT.LEFT, SWT.CENTER).indent(0, vSpace).applyTo(button);
    	 
    	 /*
    	  *  Row 5 : pathPitResult
    	  */
    	 final Label pathPitResultLabel = new Label(composite,SWT.NONE);
    	 pathPitResultLabel.setText("path pit result : ");
    	 GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).indent(0, vSpace).applyTo(pathPitResultLabel);
    	 
    	 final Button pathPitResultButton = new Button(composite,SWT.NONE);
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
    	 final Label mavenLabel = new Label(composite,SWT.NONE);
    	 mavenLabel.setText(" set MAVEN_HOME");
    	 
    	 final Button mavenHomeButton = new Button(composite,SWT.CHECK); 
    	 GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER).indent(0, vSpace).applyTo(mavenHomeButton);
    	 
    	 mavenHomeText = new Text(composite,SWT.BORDER);
    	 mavenHomeText.setText(System.getenv("MAVEN_HOME"));
    	 mavenHomeText.setEnabled(false);
    	 GridDataFactory.fillDefaults().span(1, 1).grab(true, false).indent(0, vSpace).applyTo(mavenHomeText);
    	 

    	 // listeners
    	 button.addSelectionListener(new SelectionAdapter() {
    		 @Override
    		 public void widgetSelected(final SelectionEvent e) {
    			 list.deselectAll();
    		 }
    	 });
    	 pathPitResultButton.addSelectionListener(new SelectionAdapter() {
    		 @Override
    		 public void widgetSelected(final SelectionEvent e) {
    			 final DirectoryDialog dialog = new DirectoryDialog(
    					 PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),SWT.OK);
    			 dialog.setText("Select a folder");
    			 final String directoryPath = dialog.open();
    			 if(directoryPath != null && !directoryPath.isEmpty()) {
    				 pathPitResultText.setText(directoryPath);
    				 pathPitResult = directoryPath;
    			 }
    		 }
    	 });
    	 
    	 mavenHomeButton.addSelectionListener(new SelectionAdapter() {
    		 @Override
    		 public void widgetSelected(final SelectionEvent e) {
    			 mavenHomeText.setEnabled(mavenHomeButton.getSelection());
    		 }
    	 });
    	 
    	 return composite;
     }
     
     @Override
     public void okPressed() {
    	 selection = list.getSelection();
    	 //timeOut = timeOutSpinner.getSelection();
    	 randomSeed = randomSeedSpinner.getSelection();
    	 pathPitResult = pathPitResultText.getText();
    	 mavenHome = mavenHomeText.getText();
    	 memory.setDSpotValue(DSpotMemory.MAVEN_HOME_KEY, mavenHome);
    	 String[] mySelection = new String[selection.length];
    	 for(int i = 0; i < selection.length; i++) {
    		 mySelection[i] = selection[i].substring(selection[i].indexOf("/")+1);
    	 }
    	 setMemoryData(timeOutSpinner.getSelection(),randomSeedSpinner.getSelection(),
    			 pathPitResultText.getText(), mySelection);
    	 unusedDialog = false;
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
    	 setMemoryData(timeOut, randomSeed, pathPitResult, selection);
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

     public void resetFromMemory() {
    	 if(memory.getDSpotValue(DSpotMemory.RANDOMSEED_KEY) != null) 
    			 this.randomSeed = Integer.parseInt(memory.getDSpotValue(DSpotMemory.RANDOMSEED_KEY));
    	 if(memory.getDSpotValue(DSpotMemory.TIMEOUT_KEY) != null)
    		 //this.timeOut = Integer.parseInt(memory.getDSpotValue(DSpotMemory.TIMEOUT_KEY));
    	 this.selection = memory.getSelectedCasesAsArray();
    	 this.pathPitResult = memory.getDSpotValue(DSpotMemory.PATH_PIT_RESULT_KEY);
     }
     public void setMemory(DSpotMemory memory) {
    	 this.memory = memory;
     }
     public DSpotMemory getMemory() {
    	 return memory;
     }
     private void setMemoryData(int timeOut,int randomSeed,String pathPitResult,String[] selection) {
    	 memory.setDSpotValue(DSpotMemory.TIMEOUT_KEY, String.valueOf(timeOut));
    	 memory.setDSpotValue(DSpotMemory.RANDOMSEED_KEY, String.valueOf(randomSeed));
    	 memory.setDSpotValue(DSpotMemory.PATH_PIT_RESULT_KEY, pathPitResult);
    	 String cases = "";
    	 if(selection != null)if(selection.length > 0) {
    		 cases = selection[0];
    	 for(int i = 1; i < selection.length; i++) {
    		 cases = cases + memory.separator + selection[i];
    	 }}
    	 memory.setDSpotValue(DSpotMemory.TEST_CASES_KEY, cases);
     }
     public boolean dialogUnused() {
    	 return unusedDialog;
     }
}
