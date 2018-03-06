package com.dspot.menu.wizard;


import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SegmentListener;
import org.eclipse.swt.events.SegmentEvent;

import eu.stamp.wp4.dspot.dialogs.BigDialog;

public class DsPa1 extends WizardPage {    // first page of the Dspot wizard
	
	// [0] project, [1] src, [2] testScr, [3] javaVersion, [4] outputDirectory, [5] filter
	private String[] TheProperties = new String[6];
	private boolean[] Comp = {true,false,false,true};  // this is to set next page
	private WizardConf wConf;

	public DsPa1(WizardConf wConf){
		super("First page");
		setTitle("First page");
		setDescription("Information about the project");
		this.wConf = wConf;
	} // end of the constructor
	
	@Override
	public void createControl(Composite parent) {
		
		// create the composite
		Composite composite = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();    // the layout of composite
		layout.numColumns = 3;
		composite.setLayout(layout);
		
		int VS = 8;   // this will be the verticalIndent between rows in composite
		
		// first row  (1,x)     Project's path
		Label lb1 = new Label(composite,SWT.NONE);     // Label in (1,1)
		lb1.setText("Path of the project :        ");
		
		// Obtain the path of the project
		String direction = wConf.getProjectPath();
		String[] sour = wConf.getSources();
		boolean[] isTest = wConf.getIsTest();  // the packages in sour with test classes

		
		Text tx1 = new Text(composite,SWT.BORDER);    // Text in (1,2) for the poject's path
		tx1.setText(direction);
		GridData gd = new GridData(SWT.FILL,SWT.FILL,true,false);  // data for texts they fill horizontal
        gd.verticalIndent = VS;
        gd.horizontalSpan = 2;
        tx1.setLayoutData(gd);
        TheProperties[0] = direction;
        tx1.addKeyListener(new KeyListener() {  // add a keyListener
        	@Override
        	public void keyPressed(KeyEvent e){}
        	@Override
        	public void keyReleased(KeyEvent e) {
        		
        		TheProperties[0] = tx1.getText();  // Project's path
        		Comp[0] = !TheProperties[0].isEmpty();
        		setPageComplete(Comp[0] && Comp[1] && Comp[2] && Comp[3]);
        		
        	} 
        }); // end of the KeyListener
        tx1.addSegmentListener(new SegmentListener(){
			@Override
			public void getSegments(SegmentEvent event) {
			 	
        		TheProperties[0] = tx1.getText();  // Project's path
        		Comp[0] = !TheProperties[0].isEmpty();
        		setPageComplete(Comp[0] && Comp[1] && Comp[2] && Comp[3]);
				
			}	
        });  // end of the segment listener
		
		// second row (2,x)      Source path
		Label lb2 = new Label(composite,SWT.NONE);   // Label in (2,1)
		lb2.setText("Path of the source : ");
		GridData gd2 = new GridData(SWT.FILL,SWT.FILL,false,false);
        gd2.verticalIndent = VS;
        lb2.setLayoutData(gd2);
        
        Combo combo0 = new Combo(composite,SWT.BORDER);  // Combo in (2,2) for the source's path
        combo0.setLayoutData(gd);
        combo0.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		
        		TheProperties[1] = combo0.getText();  // the path of the source
        		Comp[1] = TheProperties[1] != null;    // look at the !
        		setPageComplete(Comp[0] && Comp[1] && Comp[2] && Comp[3]);
        		
        	}
        }); // end of the selection listener
		
        
		// third row (3,x)   SourceTest path
		Label lb3 = new Label(composite,SWT.NONE);   // Label in (3,1)
		lb3.setText("Path of the source test : ");
        lb3.setLayoutData(gd2);
        
        Combo combo2 = new Combo(composite,SWT.BORDER);
        combo2.setLayoutData(gd);
        for(int i = 0; i < sour.length; i++) {  // add the sources to the combo
        	if(isTest[i]) {  // if it is not a test package
        	combo2.add(sour[i]);} else { combo0.add(sour[i]); }
        } // end of the for
        combo2.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        	
        		TheProperties[2] = combo2.getText();    //  testSrc
        		Comp[2] = TheProperties[2] != null;  // look at the "!"
        		setPageComplete(Comp[0] && Comp[1] && Comp[2] && Comp[3]);
        		
        	}
        });
		
        
		// fourth row (4,x) Java version
		Label lb4 = new Label(composite,SWT.NONE);  // Label in (4,1)
		lb4.setText("Java version : ");
        lb4.setLayoutData(gd2);
		
		Combo combo1 = new Combo(composite,SWT.NONE);  // Combo in (4,2) for the version
		combo1.add("8"); combo1.add("7"); combo1.add("6"); combo1.add("5");
		combo1.setText("8");
        combo1.setLayoutData(gd);
        TheProperties[3] = "8";
        combo1.addSelectionListener(new SelectionAdapter() {  // Use a SelectionAdapter
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		
        		TheProperties[3] = combo1.getText();    // javaVersion
        		Comp[3] = TheProperties[3] != null;
        		setPageComplete(Comp[0] && Comp[1] && Comp[2] && Comp[3]);
        		
        	}
        });  // end of the SelectionListener
		
		// (5,1 and 2) group with optional information
		Group gr = new Group(composite,SWT.NONE);
		gr.setText("Optional information");
		gd.horizontalSpan = 2;   // the group takes all the row
		gd = new GridData(SWT.FILL,SWT.FILL,true,false);
		gd.verticalSpan = 3;
		gd.horizontalSpan = 3;
		gd.verticalIndent = 2*VS;
		gr.setLayoutData(gd);
		GridLayout layout2 = new GridLayout();
		layout2.numColumns = 2;
		gr.setLayout(layout2);
		
		// first row in group gr (1,x)(gr)
		lb1 = new Label(gr,SWT.NONE);               // Label in (1,1)(gr)
		lb1.setText("Path of the output folder : ");
		
		Text tx4 = new Text(gr,SWT.BORDER);     // Text in (1,2)(gr) for the output's folder path
		tx4.setText("dspot-out/");
		gd = new GridData(SWT.FILL,SWT.FILL,true,false);
		tx4.setLayoutData(gd);
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
				
			}	
		});
		
		lb2 = new Label(gr,SWT.NONE);    // Label in (2,1)(gr)
		lb2.setText("Filter :  ");
		
		Text tx5 = new Text(gr,SWT.BORDER);    // Text in (2,2)(gr) for the filter
		tx5.setText("");
		tx5.setLayoutData(gd);
		tx5.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {	
				TheProperties[5] = tx5.getText();  // filter	
			}
		});  // end of the KeyListener
		
		
		
		// required to avoid an error in the System
		setControl(composite);
		setPageComplete(false);	
	}  // end of create control
	
	 @Override
	 public void performHelp() {
		 String[] myText = {"The first Text contains the project's path","The first combo the relative path (from the projects folder) to the sources package",
				 "The second combo the relative path to the test sources","The output folder is the directory where the output files of DSpot will be placed",
				 "The last parameter is a filter in the name of the classes to test, it's optional",""};
		 BigDialog info = new BigDialog(new Shell()," This page contains the information to write the properties file for DSpot ",myText);
		 info.open();
	 }  
	
	public String[] getTheProperties() {
		return TheProperties;
	}
}
