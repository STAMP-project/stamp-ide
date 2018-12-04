package eu.stamp.eclipse.general.validation;

public interface IValidationPage {

	public void error(String mess);
	
	public void message(String mess, int style);
	
	public void cleanError();
}
