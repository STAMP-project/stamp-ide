package eu.stamp.wp4.descartes.wizard.configuration;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eu.stamp.wp4.descartes.wizard.utils.*;

public class DescartesWizardConfiguration {

	private IJavaProject jProject;
	
	private String projectPath;
	
	private DescartesWizardPomParser descartesParser;
	
	private ILaunchConfiguration[] configurations;
	private int indexOfCurrentConfiguration = 0;
	
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
		try { configurations = findConfigurations();
		} catch (CoreException e) { e.printStackTrace(); }
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
	public String[] getConfigurationNames() {
		String[] result = new String[configurations.length];
		for(int i = 0; i < configurations.length; i++)result[i] = configurations[i].getName();
		return result;
	}
	public void setCurrentConfiguration(String name) {
		for(int i = 0; i < configurations.length; i++)if(configurations[i]
				.getName().equalsIgnoreCase(name)) {
			indexOfCurrentConfiguration = i; return;
		}
	}
	public ILaunchConfiguration getCurrentConfiguration() {
		if(configurations.length > 0)return configurations[indexOfCurrentConfiguration];
		return null; 
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
