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


import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.io.File;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import eu.stamp.wp4.dspot.execution.launch.DSpotProperties;
import eu.stamp.wp4.dspot.wizard.utils.DSpotEclipseJob;
import eu.stamp.wp4.dspot.wizard.utils.WizardConfiguration;

/**
 * this class describes the Eclipse wizard for DSpot
 * 
 */
public class DSpotWizard extends Wizard{
	
	protected DSpotWizardPage1 one;
	protected DSpotWizardPage2 two;
	private WizardConfiguration wConf;
	private String configurationName = "DSpot";
	
	// [0] Dspot jar path, [1] project path, [2] number of iterations i, [3] -t test class, [4] -a Method
	// [5] test criterion, [6] max Test Amplified
	private String[] parameters = new String[7];   // this will be the execution parameters
	private Shell shell;
	
	public DSpotWizard(WizardConfiguration wConf) {
		super();
		setNeedsProgressMonitor(true);
		setHelpAvailable(true);
		this.wConf = wConf;
		shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if(System.getenv("MAVEN_HOME") == null) { // this is a warning if MAVEN_HOME is not set
			MessageDialog.openWarning(shell, "Maven Home not set", 
					"The enviroment variable MAVEN_HOME is not set, please set it in your computer or set it in the text in advanced options in page 2");
		}
	} // end of the constructor
	
	@Override
	public String getWindowTitle() { 
		return "Dspot Wizard";
	}
	@Override
	public Image getDefaultPageImage() {
	final URL iconStampURL = FileLocator.find(Platform.getBundle("eu.stamp.wp4.dspot.wizard"),new Path("images/Stamp.png"),null);
	ImageDescriptor descriptor = ImageDescriptor.createFromURL(iconStampURL);
	return descriptor.createImage();
	}
	@Override
	public void addPages() {
		one = new DSpotWizardPage1(wConf,this);
		addPage(one);
		two = new DSpotWizardPage2(wConf);
		addPage(two);
		
	}
	
	@Override
	public boolean performFinish() {
		DSpotProperties.LAUNCH_CONF_NAME = configurationName;
		String[] advParameters = two.getAdvparameters();
		if(System.getenv("MAVEN_HOME") == null && (advParameters[4] == null || advParameters[4] == "")) { // an error message if MAVEN_HOME is not set
			MessageDialog.openError(shell, "Maven Home not set","Error the enviroment variable MAVEN_HOME is required, please set it in your computer or in the text in advanced options in page 2");
		}else {  // if MAVEN_HOME is set
		writeTheFile();    // writing the properties file
        String[] MyS = two.getMyStrings(); // obtain the user information from page 2
        for(int i = 0; i < MyS.length; i++) {
        	parameters[i+2] = MyS[i];
        } // end of the for
        boolean verbose = two.getVerbose(); // more user information
        boolean clean = two.getClean();
        Job job = new DSpotEclipseJob(parameters,advParameters,verbose,clean,wConf); // execute Dspot in background
        job.schedule();  // background invocation of Dspot
		}
		return true;
	}
	/**
	 * @param configurationName : the name to save the configuration
	 */
	public void setConfigurationName(String configurationName) {
		this.configurationName = configurationName;
	}
	/**
	 * this is the method to write the dspot.properties, it is called by performFinish
	 * it uses the information in page 1 and it is called by performFinish
	 */
	private void writeTheFile() {   	                       

		String[] Values = one.getTheProperties();
		String[] Keys = {"project","src","testSrc","javaVersion","outputDirectory","filter"};
		
		    
			String p = wConf.getProjectPath();
			configurationName = configurationName.replaceAll(" ", "_");
			parameters[1] = p+"/"+configurationName+"_dspot.properties";  // this will be set when perform finish will use it
			File file = new File(p+"/"+configurationName+"_dspot.properties");
			try {
			file.createNewFile();
			BufferedWriter fw = new BufferedWriter(new FileWriter(file));
			fw.write("# Properties File #");
			fw.newLine();
			if(Values[4] == null || Values[4] == "") {   // use the default output directory
				Values[4] = "dspot-out/";
			}
			Values[4] = p+"/"+Values[4];
			if(Values[5] == null) { Values[5] = ""; } // if there is no filter
			for(int i = 0; i < Values.length; i++){
				fw.write(Keys[i]+"="+Values[i]);
				fw.newLine();
			}  // end of the for
			fw.close();
			} catch(IOException ioe) {ioe.printStackTrace();}		
	}    // end of writTheFile
	/**
	 * this method updates the information in page two when a configuration is loaded
	 */
	public void refreshPageTwo() {
		two.refresh();
	}
	public void refreshConf(WizardConfiguration wConf) {
		two.refreshPageConfiguration(wConf);
	}
}
	

