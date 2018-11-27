/*******************************************************************************
 * Copyright (c) 2018 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ricardo Jose Tejada Garcia (Atos) - main developer
 * Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.wp4.dspot.dialogs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TreeSet;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
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

import com.richclientgui.toolbox.validation.IFieldErrorMessageHandler;
import com.richclientgui.toolbox.validation.ValidatingField;
import com.richclientgui.toolbox.validation.string.StringValidationToolkit;
import com.richclientgui.toolbox.validation.validator.IFieldValidator;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;

import eu.stamp.eclipse.dspot.launch.configuration.DSpotButtonsInformation;
import eu.stamp.eclipse.dspot.wizard.page.utils.DSpotPageSizeCalculator;
import eu.stamp.eclipse.dspot.wizard.page.utils.DSpotRowSizeCalculator;
import eu.stamp.wp4.dspot.constants.DSpotWizardConstants;
import eu.stamp.wp4.dspot.wizard.utils.DSpotMemory;
import eu.stamp.wp4.dspot.wizard.utils.WizardConfiguration;

/**
 *  This class describes a dialog to set the advanced options of Dspot execution,
 *  it will be open by a link called advanced options in page 2
 */
public class DSpotAdvancedOptionsDialog extends TitleAreaDialog{

    private static final int DECORATOR_POSITION = SWT.TOP | SWT.LEFT; 

    private StringValidationToolkit strValToolkit = null;
    private final IFieldErrorMessageHandler errorMessageHandler;
    
private WizardConfiguration wConf;  // to obtain the possible test cases

// parameters  
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
private ValidatingField<String> pathPitResultField; // TODO
private ValidatingField<String> mavenHomeField;
private Button nominimizeButton;
private Button commentsButton;
private Button verboseButton;

// to compute size
private DSpotPageSizeCalculator sizeCalculator;
private final DSpotRowSizeCalculator row;

public DSpotAdvancedOptionsDialog(Shell parentShell, WizardConfiguration wConf) {
super(parentShell);
this.wConf = wConf;
    errorMessageHandler = new WizardErrorHandler();
strValToolkit = new StringValidationToolkit(DECORATOR_POSITION,
        1,true);
        strValToolkit.setDefaultErrorMessageHandler(errorMessageHandler);
        row = new DSpotRowSizeCalculator();
}
@Override
public void create() {
super.create();
setTitle("Advanced options");
setMessage("This dialog allows to set more DSpot parameters");
final URL iconStampURL = FileLocator.find(Platform.getBundle(DSpotWizardConstants.PLUGIN_NAME)
,new Path("images/stamp.png"),null);
ImageDescriptor descriptor = ImageDescriptor.createFromURL(iconStampURL);
setTitleImage(descriptor.createImage());
}

@Override
     protected Control createDialogArea(Composite parent) {
     
// load the properties for the tooltips
Properties tooltipsProperties = new Properties();
final URL propertiesURL = FileLocator.find(Platform.getBundle(
DSpotWizardConstants.PLUGIN_NAME),
new Path("files/dspot_tooltips_dialog.properties"),null);
      InputStream inputStream;

    try {
inputStream = propertiesURL.openStream();
tooltipsProperties.load(inputStream);
inputStream.close();} catch (IOException e2) {
e2.printStackTrace(); }

         // create the composite
     Composite composite = (Composite)super.createDialogArea(parent);
  GridLayout layout = new GridLayout();
  layout.numColumns = 3;
  composite.setLayout(layout);
  int vSpace = 8;
  
     /*
      *  Row 1 : timeOut
      */
  Label space = new Label(composite,SWT.NONE);
  space.setText("");
  GridDataFactory.fillDefaults().span(2, 1).applyTo(space);
  
  Label timeOutLabel = new Label(composite,SWT.NONE);
  timeOutLabel.setText("Time out (ms) ");
  GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).indent(0, vSpace).applyTo(timeOutLabel);
  timeOutLabel.setToolTipText(tooltipsProperties.getProperty("timeOutLabel"));
  
  
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
  randomSeedLabel.setText("Random seed : ");
  GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).indent(0, vSpace).applyTo(randomSeedLabel);
  randomSeedLabel.setToolTipText(tooltipsProperties.getProperty("randomSeedLabel"));
  
  randomSeedSpinner = new Spinner(composite,SWT.BORDER);
  randomSeedSpinner.setSelection(23);
  randomSeedSpinner.setMinimum(1);
  if(memory.getDSpotValue(DSpotMemory.RANDOMSEED_KEY) != null)
   randomSeedSpinner.setSelection(Integer.parseInt(memory.getDSpotValue(DSpotMemory.RANDOMSEED_KEY)));
  GridDataFactory.fillDefaults().span(2, 1).grab(true, false).indent(0, vSpace).applyTo(randomSeedSpinner);
  
  /*
   *  Row 3 : list for the test cases
   */
  final Label listLabel = new Label(composite,SWT.NONE);
  listLabel.setText("Test cases : ");
  GridDataFactory.fillDefaults().align(SWT.LEFT,SWT.CENTER).indent(0, vSpace).applyTo(listLabel);
  listLabel.setToolTipText(tooltipsProperties.getProperty("listLabel"));
     
     list = new List(composite,SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
     String[] cases = wConf.getTestCases();
     final TreeSet<String> casesSet = new TreeSet<String>(Collator.getInstance());
     for(final String sr : cases) casesSet.add(sr);
     cases = casesSet.toArray(new String[casesSet.size()]);
     for(final String sr : cases) list.add(sr);
     GridDataFactory.fillDefaults().grab(true, false)
     .hint(200, 100).span(2,1).indent(0, vSpace).applyTo(list);
     if(selection != null) {
     if(selection.length > 0) list.setSelection(selection);}

     /*
      *  Row 4 : button to clean the test cases list
      */
     final Label buttonLabel = new Label(composite,SWT.NONE);
     buttonLabel.setText("Push to deselect all test cases : ");
     GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).indent(0, vSpace).applyTo(buttonLabel);
     
     final Button button = new Button(composite,SWT.PUSH);
     button.setText("Clean list");
     GridDataFactory.swtDefaults().span(2, 1).align(SWT.LEFT, SWT.CENTER).indent(0, vSpace).applyTo(button);
     button.setToolTipText(tooltipsProperties.getProperty("button"));
     /*
      *  Row 5 : pathPitResult
      */
     final Label pathPitResultLabel = new Label(composite,SWT.NONE);
     pathPitResultLabel.setText("Path PIT result : ");
     GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).indent(0, vSpace).applyTo(pathPitResultLabel);
     pathPitResultLabel.setToolTipText(tooltipsProperties.getProperty("pathPitResultLabel"));
     
     final Button pathPitResultButton = new Button(composite,SWT.NONE);
     pathPitResultButton.setText("Select folder");
     pathPitResultButton.setEnabled(pitSelected);
     GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER).indent(0, vSpace).applyTo(pathPitResultButton);
     pathPitResultButton.setToolTipText(tooltipsProperties.getProperty("pathPitResultButton"));
     
     Text pathPitText = new Text(composite,SWT.READ_ONLY |SWT.BORDER);
      GridDataFactory.fillDefaults().grab(true, false).indent(10, 8).applyTo(pathPitText);
      pathPitText.setEnabled(pitSelected);
      pathPitText.setText(pathPitResult);
     /*
      *  Row 6 : MAVEN_HOME
      */
     final Label mavenLabel = new Label(composite,SWT.NONE);
     mavenLabel.setText("Set MAVEN_HOME");
     mavenLabel.setToolTipText(tooltipsProperties.getProperty("mavenLabel"));
     
     final Button mavenHomeButton = new Button(composite,SWT.CHECK); 
     GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER).indent(0, vSpace).applyTo(mavenHomeButton);
     
     createMavenHomeField(composite);
     
     /*
      *   advanced buttons
      */
     DSpotButtonsInformation buttonsInfo = 
     DSpotButtonsInformation.getInstance();
     nominimizeButton = new Button(composite,SWT.CHECK);
     nominimizeButton.setText("no minimize "); 
     nominimizeButton.setSelection(buttonsInfo.nominimize);
     GridDataFactory.swtDefaults()
     .align(SWT.BEGINNING,SWT.BEGINNING).applyTo(nominimizeButton);
     
     commentsButton = new Button(composite,SWT.CHECK);
     commentsButton.setText("with comment ");
     commentsButton.setSelection(buttonsInfo.comments);
     GridDataFactory.swtDefaults().
     align(SWT.BEGINNING,SWT.BEGINNING).applyTo(commentsButton);

     verboseButton = new Button(composite,SWT.CHECK);
     verboseButton.setText("verbose ");
     verboseButton.setSelection(buttonsInfo.verbose);
     GridDataFactory.swtDefaults()
     .align(SWT.CENTER,SWT.BEGINNING).applyTo(verboseButton);
     /*
      *   Compute page size
      */
     if(sizeCalculator == null) {
     sizeCalculator = new DSpotPageSizeCalculator();
  row.reStart();
  row.addWidget(space);
  row.addWidget(timeOutLabel);
  row.addWidget(timeOutSpinner);
  sizeCalculator.addRow(row);
  row.reStart();
  row.addWidget(randomSeedLabel);
  row.addWidget(randomSeedSpinner);
  sizeCalculator.addRow(row);
  row.reStart();
  row.addWidget(list);
     sizeCalculator.addRow(row);
     row.reStart();
     row.addWidget(buttonLabel);
     row.addWidget(button);
     sizeCalculator.addRow(row);
     row.reStart();
     row.addWidget(pathPitResultLabel);
     row.addWidget(pathPitResultButton);
      row.addWidget(pathPitText);
      sizeCalculator.addRow(row);
      row.reStart();
      row.addWidget(mavenLabel);
      row.addWidget(mavenHomeButton);
      row.addWidget(mavenHomeField.getControl());
      sizeCalculator.addRow(row);
     }
      
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
     pathPitText.setText(directoryPath);
     //((Text)pathPitResultField.getControl()).setText(directoryPath);
     pathPitResult = directoryPath;
     }
     }
     });
     
     mavenHomeButton.addSelectionListener(new SelectionAdapter() {
     @Override
     public void widgetSelected(final SelectionEvent e) {
     mavenHomeField.getControl().setEnabled(mavenHomeButton.getSelection());
     }
     });
     return composite;
     }
     
     @Override
     public void okPressed() {
     selection = list.getSelection();
     
     // set the buttons information
     DSpotButtonsInformation buttonsInfo = 
     DSpotButtonsInformation.getInstance();
     buttonsInfo.nominimize = nominimizeButton.getSelection();
     buttonsInfo.comments = commentsButton.getSelection();
     buttonsInfo.verbose = verboseButton.getSelection();

     mavenHome = ((Text)mavenHomeField.getControl()).getText();
     memory.setDSpotValue(DSpotMemory.MAVEN_HOME_KEY, mavenHome);
     String[] mySelection = new String[selection.length];
     for(int i = 0; i < selection.length; i++) {
     mySelection[i] = selection[i].substring(selection[i].indexOf("/")+1);
     }
     setMemoryData(timeOutSpinner.getSelection(),randomSeedSpinner.getSelection(),
     "", mySelection); // TODO path pit 
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
        return new Point(sizeCalculator.getX() + 50, sizeCalculator.getY() + 100);
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
     //if(memory.getDSpotValue(DSpotMemory.RANDOMSEED_KEY) != null) 
     //this.randomSeed = Integer.parseInt(memory.getDSpotValue(DSpotMemory.RANDOMSEED_KEY));
    // if(memory.getDSpotValue(DSpotMemory.TIMEOUT_KEY) != null)
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

     private void createMavenHomeField (Composite composite) {
     
       mavenHomeField = strValToolkit.createTextField(composite,getFolderValidator("MAVEN_HOME")
       ,false,System.getenv("MAVEN_HOME"));
       Control mavenControl = mavenHomeField.getControl();
       GridDataFactory.fillDefaults().grab(true, false).indent(10, 8).applyTo(mavenControl);
       mavenControl.setEnabled(false);
     }
     private IFieldValidator<String> getFolderValidator(String name) {
 return new IFieldValidator<String>() {
@Override
public String getErrorMessage() { return name + "'s folder not found"; }
@Override
public String getWarningMessage() { return null; }

@Override
public boolean isValid(String content) {
if(name.contains("Pit") && content.isEmpty()) return true;
File file = new File(content);
if(file.exists())if(file.isDirectory()) return true;
return false;
}
@Override
public boolean warningExist(String content) { return false; }
 };
     }
     @Override
     protected boolean isResizable() {
               return true;
     }
     /**
  *  inner class to handle the field validation error messages
  */
 public class WizardErrorHandler implements IFieldErrorMessageHandler{
 @Override
 public void clearMessage() {
 setErrorMessage(null);
 setMessage(null,DialogPage.ERROR);
 }
 @Override
 public void handleErrorMessage(String message, String input) {
  setMessage(null,DialogPage.INFORMATION);
  setErrorMessage(message);
 }
 @Override
 public void handleWarningMessage(String message, String input) {
  setErrorMessage(null);
  setMessage(message,DialogPage.WARNING);
 }
 
 }
}
