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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
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

import eu.stamp.wp4.dspot.dialogs.DspotWizardHelpDialog;
import eu.stamp.wp4.dspot.wizard.utils.WizardConfiguration;

/**
 * this class describes the first page of the DSpot wizard 
 * 
 */
public class DSpotWizardPage1 extends WizardPage { 
	
	// [0] project, [1] src, [2] testScr, [3] javaVersion, [4] outputDirectory, [5] filter
	private String[] TheProperties = new String[6];
	private boolean[] Comp = {true,false,false,true};  // this is to set next page
	private WizardConfiguration wConf;
	private DSpotWizard wizard;
	

	public DSpotWizardPage1(WizardConfiguration wConf,DSpotWizard wizard){
		super("First page");
		setTitle("First page");
		setDescription("Information about the project");
		this.wConf = wConf;
		this.wizard = wizard;
	} // end of the constructor
	
 
	@Override
	public void createControl(Composite parent) {
		
		// create the composite
		Composite composite = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();    // the layout of composite
		layout.numColumns = 3;
		composite.setLayout(layout);
		
		int VS = 8;   // this will be the verticalIndent between rows in composite
		
		// use saved configuration
		Label lb0 = new Label(composite,SWT.NONE);
		lb0.setText("Use saved configuration : ");
	    
		Combo configCombo = new Combo(composite,SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true,false).span(2, 1).indent(0, VS).applyTo(configCombo);
		ILaunchConfiguration[] configurations = wConf.getLaunchConfigurations();
		for(ILaunchConfiguration laun : configurations) {
			configCombo.add(laun.getName());
		}
		configCombo.setEnabled(false);
		
		// New Configuration
		Label lbNewConfig = new Label(composite,SWT.NONE);
		GridDataFactory.swtDefaults().indent(0, VS).applyTo(lbNewConfig);
		lbNewConfig.setText("New Configuration : ");
		
		Button btNewConfig = new Button(composite,SWT.CHECK);
		GridDataFactory.swtDefaults().indent(0, VS).applyTo(btNewConfig);
		btNewConfig.setSelection(true);
		
		Text txNewConfig = new Text(composite,SWT.BORDER);
		txNewConfig.setText(" Type configuration name ");
		txNewConfig.setEnabled(true);
		GridDataFactory.fillDefaults().grab(false, true).indent(0, VS).applyTo(txNewConfig);
		txNewConfig.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				wizard.setConfigurationName(txNewConfig.getText());
			}
		});
		txNewConfig.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
				wizard.setConfigurationName(txNewConfig.getText());
			}	
		});
		
		btNewConfig.addSelectionListener(new SelectionAdapter() { // selection listener of the 
	        @Override                                    // new configuration check button
	        public void widgetSelected(SelectionEvent e) {
	        	if(btNewConfig.getSelection()) {
	        		txNewConfig.setText(" Type configuration name ");
	        		txNewConfig.setEnabled(true);
	        		configCombo.setText("");
	        		configCombo.setEnabled(false);
	        	} else {
	        		txNewConfig.setText("");
	        		txNewConfig.setEnabled(false);
	        		configCombo.setEnabled(true);
	        	}
	        }
});
		
		// first row  (1,x)     Project's path
		Label lb1 = new Label(composite,SWT.NONE);     // Label in (1,1)
		lb1.setText("Path of the project :        ");
		GridDataFactory.fillDefaults().grab(false, false).indent(0, VS).applyTo(lb1);
		
		// Obtain the path of the project
		String direction = wConf.getProjectPath();
		String[] sour = wConf.getSources();
		boolean[] isTest = wConf.getIsTest();  // the packages in sour with test classes

		
		Text tx1 = new Text(composite,SWT.BORDER);    // Text in (1,2) for the poject's path
		tx1.setText(direction);
		GridDataFactory.fillDefaults().grab(true,false).span(2, 1).indent(0, VS).applyTo(tx1);
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
		GridDataFactory.fillDefaults().grab(false, false).indent(0, VS).applyTo(lb2);
        Combo combo0 = new Combo(composite,SWT.BORDER);  // Combo in (2,2) for the source's path
        GridDataFactory.fillDefaults().grab(true,false).span(2, 1).indent(0, VS).applyTo(combo0);
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
		GridDataFactory.fillDefaults().grab(false, false).indent(0, VS).applyTo(lb3);
        
        Combo combo2 = new Combo(composite,SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true,false).span(2, 1).indent(0, VS).applyTo(combo2);
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
		GridDataFactory.fillDefaults().grab(false, false).indent(0, VS).applyTo(lb4);
		
		Combo combo1 = new Combo(composite,SWT.NONE);  // Combo in (4,2) for the version
		combo1.add("8"); combo1.add("7"); combo1.add("6"); combo1.add("5");
		combo1.setText("8");
        GridDataFactory.fillDefaults().grab(true,false).span(2, 1).indent(0, VS).applyTo(combo1);
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
		GridDataFactory.fillDefaults().grab(true,false).span(3,3).indent(0,2*VS).applyTo(gr);
		GridLayout layout2 = new GridLayout();
		layout2.numColumns = 2;
		gr.setLayout(layout2);
		
		// first row in group gr (1,x)(gr)
		lb1 = new Label(gr,SWT.NONE);               // Label in (1,1)(gr)
		lb1.setText("Path of the output folder : ");
		
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
				
			}	
		});
		
		lb2 = new Label(gr,SWT.NONE);    // Label in (2,1)(gr)
		lb2.setText("Filter :  ");
		
		Text tx5 = new Text(gr,SWT.BORDER);    // Text in (2,2)(gr) for the filter
		tx5.setText("");
		GridDataFactory.fillDefaults().grab(true,false).indent(0, VS).applyTo(tx5);
		tx5.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {	
				TheProperties[5] = tx5.getText();  // filter	
			}
		});  // end of the KeyListener
		
		configCombo.addSelectionListener(new SelectionAdapter() { // selection listener of the 
			@Override                                            // configurations combo
			public void widgetSelected(SelectionEvent e) {
				if(!configCombo.getText().isEmpty()) {
				try {
					wConf.setIndexOfCurrentConfiguration(configCombo.getSelectionIndex());
					
				String myArguments = wConf.getCurrentConfiguration()
					.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,"");
				String myS = myArguments.substring(
						myArguments.indexOf("-p ")+3,myArguments.indexOf(" -i "));
				myS = myS.substring(0,myS.indexOf("/dspot.properties"));
				tx1.setText(myS);
				wizard.refreshPageTwo();
				wizard.setConfigurationName(configCombo.getText());
				} catch (CoreException e1) {
					e1.printStackTrace();
				} 
				}
			}
		});
		
		// required to avoid an error in the System
		setControl(composite);
		setPageComplete(false);	
	}  // end of create control
	
	 @Override
	 public void performHelp() {
		 String[] myText = {"The first Text contains the project's path","The first combo the relative path (from the projects folder) to the sources package",
				 "The second combo the relative path to the test sources","The output folder is the directory where the output files of DSpot will be placed",
				 "The last parameter is a filter in the name of the classes to test, it's optional",""};
		 Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		 DspotWizardHelpDialog info = new DspotWizardHelpDialog(shell," This page contains the information to write the properties file for DSpot ",myText);
		 info.open();
	 }  
	
	 /**
	  * @return an String array with the information set by the user in this page
	  */
	public String[] getTheProperties() {
		return TheProperties;
	}
	
}
