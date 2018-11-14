package eu.stamp.eclipse.dspot.plugin.implementations;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.wizard.WizardPage;

import eu.stamp.eclipse.dspot.plugin.interfaces.IDSpotConfigurablePart;
import eu.stamp.eclipse.dspot.plugin.interfaces.IDSpotElement;
import eu.stamp.eclipse.dspot.plugin.interfaces.IDSpotInfoSource;
import eu.stamp.eclipse.dspot.plugin.interfaces.IDSpotProjectDependentPart;
import eu.stamp.eclipse.dspot.plugin.launch.info.DSpotLaunchInfo;

public abstract class DSpotAbstractPage extends WizardPage
        implements IDSpotConfigurablePart,IDSpotProjectDependentPart,IDSpotInfoSource{

	protected final DSpotAbstractWizard wizard;

	protected final List<IDSpotElement> elements;
	
	protected DSpotAbstractPage(String pageName,DSpotAbstractWizard wizard) {
		super(pageName);
		this.wizard = wizard;
		elements = new LinkedList<IDSpotElement>();
	}

	@Override
	public void loadProject(IJavaProject project) {
        
		wizard.loadProject(project);	
	}

	@Override
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy copy) {
		// TODO
	}

	@Override
	public void load(ILaunchConfigurationWorkingCopy copy) {
		wizard.load(copy);
	}
	@Override
	public void appendToInfoObject(DSpotLaunchInfo info) {
		for(IDSpotElement element : elements)
			((IDSpotInfoSource)element).appendToInfoObject(info);
	}
}
