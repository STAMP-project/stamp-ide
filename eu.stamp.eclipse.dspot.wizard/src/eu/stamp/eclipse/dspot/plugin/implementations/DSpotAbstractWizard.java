package eu.stamp.eclipse.dspot.plugin.implementations;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.wizard.Wizard;

import eu.stamp.eclipse.dspot.plugin.interfaces.IDSpotConfigurablePart;
import eu.stamp.eclipse.dspot.plugin.interfaces.IDSpotElement;
import eu.stamp.eclipse.dspot.plugin.interfaces.IDSpotInfoSource;
import eu.stamp.eclipse.dspot.plugin.interfaces.IDSpotProjectDependentPart;
import eu.stamp.eclipse.dspot.plugin.launch.info.DSpotLaunchInfo;
import eu.stamp.eclipse.dspot.test.detection.DSpotPluginSourcesLocator;

public abstract class DSpotAbstractWizard extends Wizard 
               implements IDSpotConfigurablePart, IDSpotProjectDependentPart{

	protected List<IDSpotElement> elements;
	
	protected final DSpotPluginSourcesLocator locator;
	
	public DSpotAbstractWizard(IJavaProject project) {
		elements = new LinkedList<IDSpotElement>();
		locator = new DSpotPluginSourcesLocator();
		locator.inspectProject(project);
	}
	
	public DSpotPluginSourcesLocator getLocator() {
		return locator;
	}
	
	public IDSpotElement getElement(String name) {
		for(IDSpotElement element : elements)
			if(element.getName().equalsIgnoreCase(name))
				return element;
		return null;
	}
	
	public void writePropertiesFile() {
		// TODO
	}
	
	public DSpotLaunchInfo generateLaunchInfoObject() {
		DSpotLaunchInfo result = new DSpotLaunchInfo();
		for(IDSpotElement element : elements)
			if(element instanceof IDSpotInfoSource)
				((IDSpotInfoSource)element).appendToInfoObject(result);
		return result;
	}
	
	@Override
	public void loadProject(IJavaProject project) {
		locator.inspectProject(project);
		for(IDSpotElement element : elements)
			if(element instanceof IDSpotProjectDependentPart)
				((IDSpotProjectDependentPart)element).loadProject(project);
	}
	
	@Override
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy copy) {
		for(IDSpotElement element : elements)
			if(element instanceof IDSpotConfigurablePart)
				((IDSpotConfigurablePart)element).appendToConfiguration(copy);
	}
	@Override
	public void load(ILaunchConfigurationWorkingCopy copy) {
		for(IDSpotElement element : elements)
			if(element instanceof IDSpotConfigurablePart)
				((IDSpotConfigurablePart)element).load(copy);
	}
}
