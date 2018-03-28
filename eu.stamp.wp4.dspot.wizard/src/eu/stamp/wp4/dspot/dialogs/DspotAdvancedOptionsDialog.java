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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SegmentEvent;
import org.eclipse.swt.events.SegmentListener;

import eu.stamp.wp4.dspot.wizard.DSpotWizardPage2;
import eu.stamp.wp4.dspot.wizard.utils.WizardConfiguration;

/**
 *  This class describes a dialog to set the advanced options of Dspot execution,
 *  it will be open by a link called advanced options in page 2
 */
public class DspotAdvancedOptionsDialog extends Dialog {
	
	// [0] randomSeed, [1] timeOut (ms),[2] test cases, [3] path pit result,[4] MAVEN_HOME 
	private String[] advParameters = new String[5];                   // this is for the user information
	private DirectoryDialog ddialog;    // this is to set [3]
	private String[] selectedCases = {""};
	private boolean pitSelected;  // [3] will be only available if the user selected PitMutantScoreSelector
	                              // as test criterion in page 2
	private String direction;
	private String[] testMethods;
	private String[] testCases;
	private DSpotWizardPage2 page;
	private Shell shell;
	private List casesList;

	
	public DspotAdvancedOptionsDialog(Shell parentSh,boolean pitSelected,String direction,String[] testCases,String[] testMethods, DSpotWizardPage2 page,String[] selectedCases) {
		super(parentSh);
		this.pitSelected = pitSelected;
		this.direction = direction;
		this.testMethods = testMethods;
		this.testCases = testCases;
		this.page = page;
		shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if(selectedCases.length > 0) {
			this.selectedCases = selectedCases;
			if(selectedCases[0] != null && !selectedCases[0].isEmpty()) { // if the [0] element is empty there is not selection
		advParameters[2] = " -c " + selectedCases[0];
		for(int i = 1; i < selectedCases.length; i++) {
			advParameters[2] = advParameters[2] + WizardConfiguration
					.getSeparator() + selectedCases[i];}}}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		
		// create the composite
		Composite composite = (Composite)super.createDialogArea(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);
		
		GridData gd = new GridData(SWT.FILL,SWT.NONE,true,false);  // for texts and spinners
		gd.horizontalSpan = 2;
		gd.verticalIndent = 8;
		
        GridData gd2 = new GridData(SWT.FILL,SWT.NONE,true,false); // for labels
        gd2.verticalIndent = 8;
		
		// first row (1,x)  randomSeed
		Label lb0 = new Label(composite,SWT.NONE);  // A label in (1,1)
		lb0.setText("Random seed ");
		lb0.setLayoutData(gd2);
		
		Spinner spin0 = new Spinner(composite,SWT.BORDER); // A spinner in (1,2) for randomSeed
		spin0.setSelection(page.getRandomSeed()); spin0.setMinimum(1);
		spin0.setLayoutData(gd);
		spin0.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				advParameters[0] = " -r "+(new Integer(spin0.getSelection())).toString();
			}
		}); // end of the selection listener
		
		// second row (2,x)
		Label lb1 = new Label(composite,SWT.NONE);  // A label in (2,1)
		lb1.setText("Time out (ms) ");
		lb1.setLayoutData(gd2);
		
		Spinner spin1 = new Spinner(composite,SWT.BORDER); // A spinner in (2,2) for timeOut
		spin1.setMaximum(100000); spin1.setMinimum(500); spin1.setIncrement(100); spin1.setSelection(10000);
		spin1.setSelection(page.getTimeOut());
		spin1.setLayoutData(gd);
		spin1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				advParameters[1] = " -v "+(new Integer(spin1.getSelection())).toString();
			}
		}); // end of the selection listener
		
		// third row (3,x) test cases
		Label lb2 = new Label(composite,SWT.NONE);  // label in (3,1)
		lb2.setText("test cases to amplify : ");
		lb2.setLayoutData(gd2);
		
		casesList = new List(composite,SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData(SWT.FILL,SWT.FILL,false,false);  // text for the test cases
		gd.verticalIndent = 8;
		gd.horizontalSpan = 2;
		gd.heightHint = 150;
		casesList.setLayoutData(gd);
		for(String sr : testCases) {
			casesList.add(sr);
		}
		casesList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] selection = casesList.getSelection();
				String methodSelected = "";
				for(String sr : testMethods) {
					if(selection[0].contains(sr)) {
						methodSelected = sr; break;
					}
				}
				advParameters[2] = " -c " + methodSelected;
				for(int i = 1; i < selection.length; i++) {
					for(String sr : testMethods) {
						if(selection[i].contains(sr)) {
							methodSelected = sr; break;
						}
					}
					advParameters[2] = advParameters[2] + WizardConfiguration
							.getSeparator() + methodSelected;
				}
			}
		});
			casesList.setSelection(selectedCases);
		
		
		// fourth row (4,x) path pit result
		Label lb3 = new Label(composite,SWT.NONE);  // A label in (4,1)
		lb3.setText("path pit result : ");
		GridData gd3 = new GridData(SWT.NONE,SWT.NONE,true,false);
		gd3.verticalIndent = 10;
		lb3.setLayoutData(gd3);
		
		Button button = new Button(composite,SWT.PUSH);  // this button in (4,2) opens a directory dialog
		GridData gdButton = new GridData(SWT.LEFT,SWT.NONE,false,false);
		gdButton.verticalIndent = 12;
		button.setLayoutData(gdButton);
		button.setText("Add");
		button.setEnabled(pitSelected);
		button.setEnabled(true);
		
		Text tx1 = new Text(composite,SWT.BORDER);  // the text in (4,3) for the path pit result
		GridData gdTx1 = new GridData(SWT.FILL,SWT.NONE,true,false);
		gdTx1.verticalIndent = 12;
        tx1.setLayoutData(gdTx1);
		tx1.setEnabled(pitSelected);
		tx1.setText(page.getPathPitResult());
		tx1.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				advParameters[3] = " -m " + tx1.getText();  // including the -m in the string
				if(tx1.getText() == null || tx1.getText() == "") { advParameters[3] =""; } // if there is no text put an empty string 
			}			
		}); // end of the key listener
		tx1.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
			advParameters[3] = " -m " + tx1.getText();	
			if(tx1.getText() == null || tx1.getText() == "") { advParameters[3] =""; }
			}
			
		}); // end of the segment listener
		ddialog = new DirectoryDialog(shell);
		ddialog.setText("Select folder"); // this is the directory dialog opened by the push button in (3,2)
		ddialog.setFilterPath(direction);  // the initial point is the project's folder
		
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String theString = ddialog.open();
				if(theString == null) { theString = ""; }
				tx1.setText(theString);  // put the path of the selected directory into the text in (3,3)
			}
		}); // end of the selection listener
			
		// fifth row (5,x) MAVEN_HOME 
		  Label lb4 = new Label(composite,SWT.NONE); // Label in (5,1)
		  lb4.setText("Set MAVEN_HOME : ");
		  GridData gd5 = new GridData(SWT.FILL,SWT.FILL,true,false);
		  gd5.verticalIndent = 15;
		  lb4.setLayoutData(gd5);
		  
		  Button checking = new Button(composite,SWT.CHECK);  // this check button in (5,2) avoids an accidental change in MAVEN_HOME
		  GridData gd6 = new GridData(SWT.NONE,SWT.NONE,false,false);
		  gd6.verticalIndent = 15;
		  checking.setLayoutData(gd6);
		  
		  Text tx2 = new Text(composite,SWT.BORDER);  // the text for setting MAVEN_HOME in (5,3)
		  GridData gd4 = new GridData(SWT.FILL,SWT.NONE,true,false);
		  gd4.verticalIndent = 12;
		  tx2.setLayoutData(gd4);
		  tx2.setText(System.getenv("MAVEN_HOME"));
		  tx2.setEnabled(false);
		  tx2.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
			advParameters[4] = " -j " + tx2.getText();	
			if(tx2.getText() == null || tx2.getText() == "") { advParameters[4] =""; }
			}
		  });
		  
		  
		  tx2.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
				advParameters[4] = " -j " + tx2.getText();
				if(tx2.getText() == null || tx2.getText() == "") { advParameters[4] =""; }
			}  
		  });
		  
		  checking.addSelectionListener(new SelectionAdapter() {  // the check button in (5,2) enables the text in (5,3)
			  @Override
			  public void widgetSelected(SelectionEvent e) {
				  tx2.setEnabled(checking.getSelection());
			  }
		  });
		  
		
		return composite;
	}
	
	@Override
	protected void configureShell(Shell shell) {  // set the title
		super.configureShell(shell);
		shell.setText(" Advanced options ");
	}
	
    @Override
    protected Point getInitialSize() { // default size of the dialog
        return new Point(600, 400);
    }
	
	/**
	 * getAdvParameters
	 * @return an String array with the user information
	 */
	public String[] getAdvParameters() {
		for(int i = 0; i < advParameters.length; i++) {
			if(advParameters[i] == null) { advParameters[i] = ""; }
		}
		return advParameters;
	}
}
