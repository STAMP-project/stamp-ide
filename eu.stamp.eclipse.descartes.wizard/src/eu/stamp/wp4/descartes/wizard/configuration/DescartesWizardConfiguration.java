/*******************************************************************************
 * Copyright (c) 2019 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * 	Ricardo Jose Tejada Garcia (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.wp4.descartes.wizard.configuration;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.internal.Workbench;
import org.xml.sax.SAXException;

import eu.stamp.eclipse.descartes.plugin.pom.DescartesPomReader;
import eu.stamp.wp4.descartes.wizard.utils.DescartesWizardConstants;

@SuppressWarnings("restriction")
public class DescartesWizardConfiguration {

	private IJavaProject jProject;
	private String projectPath;
	private String pomName;
	
	/**
	 *  this object contains information of the pom and the methods to it's manipulation
	 */
	private DescartesPomReader pomReader; 
	
	/**
	 *  an array with all the saved configurations of Descartes type
	 */
	private ILaunchConfiguration[] configurations;
	private int indexOfCurrentConfiguration = 0;  // to set and get the configuration in use
	
	private static IJavaProject obtainProject() {
		ISelectionService selectionService = Workbench.getInstance()
				.getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();
		IJavaProject jProject = null;
		Object element;
		
		if(selection instanceof IStructuredSelection) {
			element = ((IStructuredSelection)selection).getFirstElement();
			
			if(element instanceof IJavaElement) {
				jProject = ((IJavaElement) element).getJavaProject();
				return jProject;
			}
			if(element instanceof IProject) {
				IProject pro = (IProject) element;
				jProject = new JavaProject(pro,null);
				return jProject;
			}
		}
		if(jProject == null) {
			selection = selectionService.getSelection("org.eclipse.ui.navigator.ProjectExplorer");
		if(selection instanceof IStructuredSelection) {
			element = ((IStructuredSelection)selection).getFirstElement();
			if(element instanceof IProject) {
				IProject pro = (IProject) element;
				jProject = new JavaProject(pro,null);
				return jProject;
			}
		}
		
		}
		return null;
	}
	
	
	/**
	 *  this is the constructor called when the wizard is open, it takes the project
	 *  from the eclipse selection
	 */
	public DescartesWizardConfiguration(){
		
		// get the project and check that it's maven
		jProject = obtainProject();
		if(jProject != null) { 
			try {
				if(jProject.getProject().hasNature(DescartesWizardConstants.MAVEN_NATURE_ID)) {
         try {
			pomReader = new DescartesPomReader(jProject
					.getProject().getLocation().toString());
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
         projectPath = jProject.getProject().getLocation().toString();} 
			} catch (ParserConfigurationException | CoreException e) {
				e.printStackTrace();
			}
		}
		// get the Descartes configurations array
		try { configurations = findConfigurations();
		} catch (CoreException e) { e.printStackTrace(); }
	}
	
	/**
	 * this constructor is use to build a wizard configuration from a project, it is called 
	 * when the project is changed
	 * @param jProject : project corresponding to the new wizard configuration
	 */
	public DescartesWizardConfiguration(IJavaProject jProject) {
		this.jProject = jProject;
		try {
			pomReader = new DescartesPomReader(jProject
					.getProject().getLocation().toString());
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		projectPath = jProject.getProject().getLocation().toString();
	}
	/*
	 *  getter methods
	 */
	public IJavaProject getProject() {
		return jProject;
	}
	public String getProjectPath() {
		return projectPath;
	}
	/**
	 * @return an array with the mutators names
	 */
	public String[] getMutatorsNames() {
		return pomReader.getMutators();
	}
	/*
	 *  setter methods
	 */
	public void setProject(IJavaProject jProject) {
		this.jProject = jProject;
		try {
			pomReader = new DescartesPomReader(jProject
					.getProject().getLocation().toString());
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		projectPath = jProject.getProject().getLocation().toString();	
	}
	public String[] getConfigurationNames() {
		String[] result = new String[configurations.length];
		for(int i = 0; i < configurations.length; i++)result[i] = configurations[i].getName();
		return result;
	}
    /**
     * loads a stored Descartes configuration
     * @param name : the name of the configuration to load
     * @throws CoreException
     */
	public void setCurrentConfiguration(String name) throws CoreException {
		
		configurations = findConfigurations();
		
		ILaunchConfiguration configuration = null;  // local variable to store the new current configuration
		for(int i = 0; i < configurations.length; i++)if(configurations[i]
				.getName().equalsIgnoreCase(name)) {
			configuration = configurations[i];  // set the index and put the new configuration in the local variable
			indexOfCurrentConfiguration = i; break;
		}
		
	  // get the project path and name	
	  projectPath = configuration.getAttribute(MavenLaunchConstants.ATTR_POM_DIR, ""); 
	  pomName = configuration.getAttribute(MavenLaunchConstants.ATTR_GOALS,"");
	  pomName = pomName.substring(pomName.indexOf("-f ")+3);  // ... -f pomName
	  
	  // parse the pom of the configuration
	  try {
		pomReader = new DescartesPomReader(projectPath);
	} catch (ParserConfigurationException | SAXException | IOException e) {
		e.printStackTrace();
	}
	  // Now the wizard configuration is updated
	}
	/**
	 * @return the active Descartes configuration
	 */
	public ILaunchConfiguration getCurrentConfiguration() {
		if(configurations.length > 0)return configurations[indexOfCurrentConfiguration];
		return null; 
		}
	/**
	 * @return the name of the file use as pom by the current configuration 
	 */
	public String getPomName() {
		String result = pomName;
		if(result == null && configurations != null) {
			try {
				result = getCurrentConfiguration()
				.getWorkingCopy().getAttribute(DescartesWizardConstants.POM_NAME_LAUNCH_CONSTANT, "");
			} catch (CoreException e) {
				e.printStackTrace();
			}}
		if(result == null) result = "descartes_pom.xml";
		else if(result.isEmpty()) result = "descartes_pom.xml";
		return result;
	}
	/**
	 * @return the list of Descartes configurations that will be displayed in the load configuration combo
	 * @throws CoreException
	 */
	private ILaunchConfiguration[] findConfigurations() throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(
				manager.getLaunchConfigurationType(DescartesWizardConstants
						.LAUNCH_CONFIGURATION_DESCARTES_ID));
		ILaunchConfiguration[] result = new ILaunchConfiguration[configurations.length];
		for(int i = 0; i < configurations.length; i++) result[i] = configurations[i].getWorkingCopy();
		return result;
	}
}
