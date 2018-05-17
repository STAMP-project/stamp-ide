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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eu.stamp.wp4.descartes.wizard.utils.*;

public class DescartesWizardConfiguration {

	private IJavaProject jProject;
	
	private String projectPath;
	
	private DescartesWizardPomParser descartesParser;
	
	public DescartesWizardConfiguration(){
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
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
	}
	
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
	public Node[] getMutators() {
		return descartesParser.getMutators();
	}
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
	
}
