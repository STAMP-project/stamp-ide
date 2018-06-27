package eu.stamp.wp4.descartes.wizard.configuration;

import eu.stamp.wp4.descartes.wizard.DescartesWizard;

public interface IDescartesWizardPart {
	/**
	 * The wizard calls this method over all the parts implementing this interface
	 * when a change in the configuration requires an update in the wizard parts
	 * for example another project is selected
	 * @param wConf : the new wizard configuration
	 */
	public void updateDescartesWizardPart(DescartesWizardConfiguration wConf);
	/**
	 * @param wizard : the updated reference to the Descartes wizard
	 */
	public void updateWizardReference(DescartesWizard wizard);
    
}
