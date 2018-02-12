package com.dspot.menu.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SegmentListener;
import org.eclipse.swt.events.SegmentEvent;

public class DsPa2 extends WizardPage {
	
	private boolean[] Comp = {true,false};  // this is to set page complete
	// [0] i : number of iterations, [1] execution test class, [2] Method
	private String[] MyStrings = new String[3];
	
	public DsPa2() {
		super("Second page");
		setTitle("Second page");
		setDescription("Information about the execution");		
	} // end of the constructor
	
	@Override
	public void createControl(Composite parent) {
		
		// create the composite
		Composite composite = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();    // the layout of the composite
		layout.numColumns = 3;
		composite.setLayout(layout);
		
		// First row (1,x) number of iterations
		Label lb1 = new Label(composite,SWT.NONE);  // A label in (1,1)
		lb1.setText("Number of iterations :  ");
		
		GridData gd = new GridData(SWT.FILL,SWT.FILL,true,false);
		gd.heightHint = 20;
		
		Spinner spin = new Spinner(composite,SWT.NONE); // A spinner in  (1,2)
		spin.setMinimum(1);                             // for the number of iterations i
		spin.setLayoutData(gd);
		spin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				Comp[0] = spin.getSelection() > 0;  // we need a positive number of iterations
				MyStrings[0] = (new Integer(spin.getSelection()).toString());
				setPageComplete(Comp[0] && Comp[1]);				
				
			}
		});
		
		Label space1 = new Label(composite,SWT.NONE);    // (1,3)
		space1.setText("");
		space1.setLayoutData(gd);
		
		// Second row (2,x) execution classes
		Label lb2 = new Label(composite,SWT.NONE);   // A label in (2,1)
		lb2.setText("Execution classes :  ");
		
		Text tx1 = new Text(composite,SWT.BORDER);  // A text in (2,2) for the execution classes
		tx1.setText("");
		gd = new GridData(SWT.FILL,SWT.FILL,true,false);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 8;
		tx1.setLayoutData(gd);
		tx1.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
			Comp[1] = !tx1.getText().isEmpty();	  // look at the "!"
			MyStrings[1] = tx1.getText();
			setPageComplete(Comp[0] && Comp[1]);
			}
		});   // end of the Keylistener
		
     tx1.addSegmentListener(new SegmentListener() {  // if the user copy-pastes the key listener dosn't detect it
    	 @Override
    	 public void getSegments(SegmentEvent e) {
 			Comp[1] = !tx1.getText().isEmpty();	  // look at the "!"
 			MyStrings[1] = tx1.getText();
 			setPageComplete(Comp[0] && Comp[1]); 
    	 }
     });  // end of the segment listener
		
		// Third row (3,x) Method
		Label lb3 = new Label(composite,SWT.NONE);   // A label in (3,1)
		lb3.setText("Amplification method :  ");
		
		Text tx2 = new Text(composite,SWT.BORDER);
		tx2.setText("Add");
		tx2.setLayoutData(gd);
		tx2.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				
				MyStrings[2] = tx2.getText();
				
			}
		});
		
		tx2.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent e) {
				
				MyStrings[2] = tx2.getText();
			}
		});
		
		// required to avoid an error in the System
		setControl(composite);
		setPageComplete(false);	
	}  // end of create Control

	public String[] getMyStrings() {
		if(MyStrings[0] == null) {   // if the spinner hasn't be touch
			MyStrings[0] = "1"; // then use the default value
		}
		return MyStrings;
	}
}
