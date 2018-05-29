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
package eu.stamp.wp4.descartes.wizard.configuration;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eu.stamp.wp4.descartes.wizard.utils.*;

@SuppressWarnings("restriction")
public class DescartesWizardConfiguration {

	private IJavaProject jProject;
	private String projectPath;
	
	/**
	 *  this object contains information of the pom and the methods to it's manipulation
	 */
	private DescartesWizardPomParser descartesParser;
	
	/**
	 *  an array with all the saved configurations of Descartes type
	 */
	private ILaunchConfiguration[] configurations;
	private int indexOfCurrentConfiguration = 0;  // to set and get the configuration in use
	
	/**
	 *  this is the constructor called when the wizard is open, it takes the project
	 *  from the eclipse selection
	 */
	public DescartesWizardConfiguration(){
		
		// get the project and check that it's maven
		jProject = DescartesWizardPomParser.obtainProject();
		if(jProject != null) { 
			try {
				if(jProject.getProject().hasNature(DescartesWizardConstants.MAVEN_NATURE_ID)) {
         try {
			descartesParser = new DescartesWizardPomParser(jProject);
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
	 * @param jProject : project corresponding to the nerw wizard configuration
	 */
	public DescartesWizardConfiguration(IJavaProject jProject) {
		this.jProject = jProject;
		try {
			descartesParser = new DescartesWizardPomParser(jProject);
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
	/*
	public Node[] getMutators() {
		return descartesParser.getMutators();
	}*/
	/**
	 * @return an array with the mutators names
	 */
	public String[] getMutatorsNames() {
		String[] names = {""}; 
		Node[] mutators = descartesParser.getMutators();
		if(mutators != null) {
	    names = new String[mutators.length];
		for(int i = 0; i < mutators.length; i++) names[i] = mutators[i].getNodeName();}
		return names;
	}
	public String[] getMutatorsTexts() {
		String[] texts = {""};
		Node[] mutators = descartesParser.getMutators();
		if(mutators != null) {
		texts = new String[mutators.length];
		for(int i = 0; i < mutators.length; i++) texts[i] = mutators[i].getTextContent();}
		return texts;
	}
	public DescartesWizardPomParser getDescartesParser() {
		return descartesParser;
	}
	/*
	 *  setter methods
	 */
	public void setProject(IJavaProject jProject) {
		this.jProject = jProject;
		try {
			descartesParser = new DescartesWizardPomParser(jProject);
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

	public void setCurrentConfiguration(String name) throws CoreException {
		
		configurations = findConfigurations();
		
		ILaunchConfiguration configuration = null;  // local variable to store the new current configuration
		for(int i = 0; i < configurations.length; i++)if(configurations[i]
				.getName().equalsIgnoreCase(name)) {
			configuration = configurations[i];  // set the index and put the new configuration in the local variable
			indexOfCurrentConfiguration = i; break;
		}
		
	  // get the project path and name	
	  projectPath = configuration.getAttribute(MavenLaunchConstants.ATTR_POM_DIR, ""); // the pom is in the project's folder
	  String projectName;  // get the name of the project corresponding to the new configuration
	  if(projectPath.contains("/"))projectName = projectPath.substring(projectPath.lastIndexOf("/"));
	  else projectName = projectPath.substring(projectPath.lastIndexOf("\\")); // windows path separator
	  
	  // now find the project object using the name
	  IProject theProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
	  jProject = new JavaProject(theProject,null); // use the project object to create the IJavaProject
	  String pomName = configuration.getAttribute(MavenLaunchConstants.ATTR_GOALS,"");
	  pomName = pomName.substring(pomName.indexOf("-f ")+3);  // ... -f pomName
	  
	  // parse the pom of the configuration
	  try {
		descartesParser = new DescartesWizardPomParser(jProject,pomName);
	} catch (ParserConfigurationException | SAXException | IOException e) {
		e.printStackTrace();
	}
	  // Now the wizard configuration is updated
	}
	
	public ILaunchConfiguration getCurrentConfiguration() {
		if(configurations.length > 0)return configurations[indexOfCurrentConfiguration];
		return null; 
		}
	
	public String getPomName() {
		String result = descartesParser.getPomName();
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
