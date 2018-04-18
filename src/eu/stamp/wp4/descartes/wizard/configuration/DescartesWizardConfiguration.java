package eu.stamp.wp4.descartes.wizard.configuration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.w3c.dom.Node;

import eu.stamp.wp4.descartes.wizard.utils.*;

public class DescartesWizardConfiguration {

	private IJavaProject jProject;
	
	private String projectPath;
	
	private Node[] mutators;
	
	public DescartesWizardConfiguration(){
		jProject = DescartesWizardStaticUtils.obtainProject();
		if(jProject != null) { 
			try {
				if(jProject.getProject().hasNature(DescartesWizardConstants.MAVEN_NATURE_ID)) {
mutators = DescartesWizardStaticUtils.obtainMutators(jProject);
projectPath = jProject.getProject().getLocation().toString();} 
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
	
	public DescartesWizardConfiguration(IJavaProject jProject) {
		this.jProject = jProject;
		mutators = DescartesWizardStaticUtils.obtainMutators(jProject);
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
		return mutators;
	}
	public String[] getMutatorsNames() {
		String[] names = {""};
		if(mutators != null) {
	    names = new String[mutators.length];
		for(int i = 0; i < mutators.length; i++) names[i] = mutators[i].getNodeName();}
		return names;
	}
	public String[] getMutatorsTexts() {
		String[] texts = {""};
		if(mutators != null) {
		texts = new String[mutators.length];
		for(int i = 0; i < mutators.length; i++) texts[i] = mutators[i].getTextContent();}
		return texts;
	}
	/*
	 *  setter methods
	 */
	public void setProject(IJavaProject jProject) {
		this.jProject = jProject;
		mutators = DescartesWizardStaticUtils.obtainMutators(jProject);
		projectPath = jProject.getProject().getLocation().toString();	
	}
	public void setMutators(Node[] mutators) {
		this.mutators = mutators;
	}
	
}
