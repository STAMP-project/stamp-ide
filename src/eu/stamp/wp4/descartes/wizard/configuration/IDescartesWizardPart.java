package eu.stamp.wp4.descartes.wizard.configuration;

import eu.stamp.wp4.descartes.wizard.DescartesWizard;

public interface IDescartesWizardPart {
	
	public void updateDescartesWizardPart(DescartesWizardConfiguration wConf);
	
	public void updateWizardReference(DescartesWizard wizard);

}
