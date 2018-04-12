package eu.stamp.wp4.dspot.dialogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import edu.emory.mathcs.backport.java.util.Arrays;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;

import eu.stamp.wp4.dspot.wizard.utils.WizardConfiguration;

public class ExperimentalDialog extends Dialog{
	
	private WizardConfiguration wConf;
	
	private int timeOut = 10000;
	private int randomSeed = 23;
	private String[] selection;
	private String pathPitResult;
	private String mavenHome;
	
	private Spinner timeOutSpinner;
	private Spinner randomSeedSpinner;
	private List list;
	private Text pathPitResultText;
	private Text mavenHomeText;
	

	public ExperimentalDialog(Shell parentShell, WizardConfiguration wConf) {
		super(parentShell);
		this.wConf = wConf;
	}
     @Override
     protected Control createDialogArea(Composite parent) {
    	 
    	 Composite composite = (Composite)super.createDialogArea(parent);
 		 GridLayout layout = new GridLayout();
 		 layout.numColumns = 3;
 		 composite.setLayout(layout);
 		 
 		 Label timeOutLabel = new Label(composite,SWT.NONE);
 		 timeOutLabel.setText("Time out (ms) ");
 		 GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(timeOutLabel);
 		 
 		 timeOutSpinner = new Spinner(composite,SWT.BORDER);
 		 GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(timeOutSpinner);
 		 timeOutSpinner.setMaximum(100000); timeOutSpinner.setMinimum(500); timeOutSpinner.setIncrement(100);
 		 timeOutSpinner.setSelection(timeOut);
 		 
 		 Label randomSeedLabel = new Label(composite,SWT.NONE);
 		 randomSeedLabel.setText("random seed : ");
 		 GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(randomSeedLabel);
 		 
 		 randomSeedSpinner = new Spinner(composite,SWT.BORDER);
 		 randomSeedSpinner.setMinimum(1); randomSeedSpinner.setSelection(randomSeed);
 		 GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(randomSeedSpinner);
 		 
 		 Label listLabel = new Label(composite,SWT.NONE);
 		 listLabel.setText("test cases : ");
 		 GridDataFactory.fillDefaults().align(SWT.LEFT,SWT.CENTER).applyTo(listLabel);
    	 
    	 list = new List(composite,SWT.MULTI);
    	 String[] cases = wConf.getTestCases();
    	 HashSet<String> casesSet = new HashSet<String>(Arrays.asList(cases));
    	 cases = casesSet.toArray(new String[casesSet.size()]);
    	 for(String sr : cases) list.add(sr);
    	 GridDataFactory.fillDefaults().grab(true, false).span(2,1).applyTo(list);
    	 if(selection != null) {
    	 if(selection.length > 0) list.setSelection(selection);}
    	 
    	 Label buttonLabel = new Label(composite,SWT.NONE);
    	 buttonLabel.setText(" push to deselect all test cases : ");
    	 GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(buttonLabel);
    	 
    	 Button button = new Button(composite,SWT.PUSH);
    	 button.setText("clean list");
    	 GridDataFactory.swtDefaults().span(2, 1).align(SWT.LEFT, SWT.CENTER).applyTo(button);
    	 
    	 Label pathPitResultLabel = new Label(composite,SWT.NONE);
    	 pathPitResultLabel.setText("path pit result : ");
    	 GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(pathPitResultLabel);
    	 
    	 pathPitResultText = new Text(composite,SWT.BORDER);
    	 pathPitResultText.setEnabled(false);
    	 GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(pathPitResultText);
    	 
    	 Button mavenHomeButton = new Button(composite,SWT.CHECK);
    	 mavenHomeButton.setText("set MAVEN_HOME : "); 
    	 GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(mavenHomeButton);
    	 
    	 mavenHomeText = new Text(composite,SWT.BORDER);
    	 mavenHomeText.setText(System.getenv("MAVEN_HOME"));
    	 mavenHomeText.setEnabled(false);
    	 GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(mavenHomeText);
    	 

    	 // listeners
    	 button.addSelectionListener(new SelectionAdapter() {
    		 @Override
    		 public void widgetSelected(SelectionEvent e) {
    			 list.deselectAll();
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
    	 timeOut = timeOutSpinner.getSelection();
    	 randomSeed = randomSeedSpinner.getSelection();
    	 pathPitResult = pathPitResultText.getText();
    	 mavenHome = mavenHomeText.getText();
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
     
     public void setConfiguration(WizardConfiguration wConf) {
    	 this.wConf = wConf;
     }
     
     public void reset(WizardConfiguration wConf,int randomSeed, int timeOut,String[] selection,String pathPitResult) {
    	 this.wConf = wConf;
    	 this.randomSeed = randomSeed;
    	 this.timeOut= timeOut;
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
     
     public String[] getAdvancedParameters() {
    	 
    		// [0] randomSeed, [1] timeOut (ms),[2] test cases, [3] path pit result,[4] MAVEN_HOME 
    	    String[] advParameters = new String[5];                   // this is for the user information
    	    
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
}
