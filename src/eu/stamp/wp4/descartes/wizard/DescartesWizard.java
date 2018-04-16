package eu.stamp.wp4.descartes.wizard;

import java.util.ArrayList;

import org.eclipse.jface.wizard.Wizard;

import eu.stamp.wp4.descartes.wizard.configuration.DescartesWizardConfiguration;
import eu.stamp.wp4.descartes.wizard.configuration.IDescartesWizardPart;

public class DescartesWizard extends Wizard {
	
	private DescartesWizardConfiguration wConf;

	private ArrayList<IDescartesWizardPart> partsList = new ArrayList<IDescartesWizardPart>(1);
	
	protected DescartesWizardPage1 one;
	
	public DescartesWizard() {
		super();
		setNeedsProgressMonitor(true);
		setHelpAvailable(true);
		wConf = new DescartesWizardConfiguration();
	}
	
	@Override
	public void addPages() {
		one = new DescartesWizardPage1(this);
		addPage(one);
		partsList.add(one);
		updateWizardParts();
	}
	@Override
	public String getWindowTitle() { 
		return "Descartes Wizard";
	}
	@Override
	public boolean performFinish() {
		return true;
	}
	
	public void updateWizardParts() {
		for(int i = 0; i < partsList.size(); i++) partsList.get(i).updateDescartesWizardPart(wConf);
	}
}
