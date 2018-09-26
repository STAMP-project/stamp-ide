package eu.stamp.eclipse.descartes.wizard.interfaces;
/**
 * this interface allows to give the error handler instances with the required methods, WizardPage
 * and TitleAreaDialog have the same methods to set the messages but they don't belong to the same
 * inheritance tree so without the interface the error handler would need two references and 
 * it would be necessary to check which is null each time a method is called.
 */
public interface IDescartesPage {
 
	public void error(String mess);
		
	public void message(String mess, int style);
	
}
