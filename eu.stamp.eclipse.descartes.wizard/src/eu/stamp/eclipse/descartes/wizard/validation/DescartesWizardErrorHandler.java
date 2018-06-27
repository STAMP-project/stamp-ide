package eu.stamp.eclipse.descartes.wizard.validation;

import org.eclipse.jface.dialogs.DialogPage;
import com.richclientgui.toolbox.validation.IFieldErrorMessageHandler;
/**
 * instances of this class are responsible to display the page messages
 * about the validation of a field
 */
public class DescartesWizardErrorHandler implements IFieldErrorMessageHandler{
	
	private IDescartesPage page;
	
	public DescartesWizardErrorHandler(IDescartesPage page) {
	   super();
	   this.page = page;	
	}
	
	@Override
	public void clearMessage() {
		page.error(null);
		page.message(null,DialogPage.ERROR);	
	}
	@Override
	public void handleErrorMessage(String message, String input) {
	 page.message(null,DialogPage.INFORMATION);
	 page.error(message);	
	}
	@Override
	public void handleWarningMessage(String message, String input) {
	 page.error(null);
	 page.message(message,DialogPage.WARNING);	
	}
	
}

