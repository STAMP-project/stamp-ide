package eu.stamp.eclipse.text.validation;

import org.eclipse.jface.dialogs.DialogPage;

import com.richclientgui.toolbox.validation.IFieldErrorMessageHandler;

import eu.stamp.eclipse.general.validation.IValidationPage;

public class StampTextFieldErrorHandler implements IFieldErrorMessageHandler {

	IValidationPage page;
	
	public StampTextFieldErrorHandler(IValidationPage page) {
		super();
		this.page = page;
	}
	
	@Override
	public void clearMessage() {
		page.error(null);
		page.message(null,DialogPage.ERROR);
		page.cleanError();
	}

	@Override
	public void handleErrorMessage(String message, String arg1) {
		 page.message(null,DialogPage.INFORMATION);
		 page.error(message);
	}

	@Override
	public void handleWarningMessage(String message, String arg1) {
		 page.error(null);
		 page.message(message,DialogPage.WARNING);	
	}

}
