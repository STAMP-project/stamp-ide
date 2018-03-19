package eu.stamp.wp4.dspot.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SegmentEvent;
import org.eclipse.swt.events.SegmentListener;

import eu.stamp.wp4.dspot.wizard.utils.WizardConfiguration;

/**
 *  This class describes a dialog to set the advanced options of Dspot execution,
 *  it will be open by a link called advanced options in page 2
 */
public class DspotAdvancedOptionsDialog extends Dialog {
	
	// [0] randomSeed, [1] timeOut (ms),[2] test cases, [3] path pit result,[4] MAVEN_HOME 
	private String[] advParameters = new String[5];                   // this is for the user information
	private DirectoryDialog dd = new DirectoryDialog(new Shell());    // this is to set [3]
	private boolean pitSelected;  // [3] will be only available if the user selected PitMutantScoreSelector
	                              // as test criterion in page 2
	private String direction;
	private CheckingDialog chDiag;
	private String[] testMethods;
	
	public DspotAdvancedOptionsDialog(Shell parentSh,boolean pitSelected,String direction,String[] testCases,String[] testMethods) {
		super(parentSh);
		this.pitSelected = pitSelected;
		this.direction = direction;
		this.testMethods = testMethods;
		chDiag = new CheckingDialog(new Shell(),testCases," Select test cases ");
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
		spin0.setSelection(23); spin0.setMinimum(1);
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
		
		Button casesButton = new Button(composite,SWT.PUSH);
		casesButton.setText("Select");
		GridData casesBtData = new GridData(SWT.NONE,SWT.NONE,false,false);
		casesBtData.verticalIndent = 8;
		casesButton.setLayoutData(casesBtData);
		
		gd = new GridData(SWT.FILL,SWT.NONE,true,false);  // text for the test cases
		gd.verticalIndent = 8;
		gd.horizontalSpan = 1;
		Text tx0 = new Text(composite,SWT.BORDER);   
		tx0.setLayoutData(gd);
		tx0.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				advParameters[2] = " -c " + tx0.getText();
				if (tx0.getText() == null || tx0.getText() == "") { advParameters[2] = ""; }
			}
			
		});  // end of the Keylistener
		tx0.addSegmentListener(new SegmentListener() {  // a segment listener for detecting if the user
			@Override                                     // copy - pastes
			public void getSegments(SegmentEvent event) {
				advParameters[2] = " -c " + tx0.getText();
				if (tx0.getText() == null || tx0.getText() == "") { advParameters[2] = ""; }
			}		
		});  // end of the segment listener
		
		casesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(chDiag.open() == Window.OK) {
				String selection = chDiag.getSelection();
				String text = "";
				for(String sr : testMethods) {
					if(selection.contains(sr))text = text+sr+WizardConfiguration.getSeparator();
				}
                if(text.endsWith(WizardConfiguration.getSeparator())&&text.length()>0)text = text.substring(0, text.length()-1);
                tx0.setText(text);
                }
			}
		}); 
		
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
		
		Text tx1 = new Text(composite,SWT.BORDER);  // the text in (4,3) for the path pit result
		GridData gdTx1 = new GridData(SWT.FILL,SWT.NONE,true,false);
		gdTx1.verticalIndent = 12;
        tx1.setLayoutData(gdTx1);
		tx1.setEnabled(pitSelected);
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
		
		dd.setText("Select folder"); // this is the directory dialog opened by the push button in (3,2)
		dd.setFilterPath(direction);  // the initial point is the project's folder
		
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String theString = dd.open();
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
        return new Point(600, 300);
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
