/*******************************************************************************
 * Copyright (c) 2019 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * 	Ricardo Jose Tejada Garcia (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.eclipse.dspot.wizard.validation;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.wizard.WizardPage;

import com.richclientgui.toolbox.validation.IFieldErrorMessageHandler;

public class ErrorHandlerFactory {

public static IFieldErrorMessageHandler getHandler(Object page) {
	ErrorHandlerFactory factory = new ErrorHandlerFactory();
	return factory.createHandler(page);
}
	
private IFieldErrorMessageHandler createHandler(Object page) {
	if(page instanceof WizardPage) return new PageErrorHandler((WizardPage)page);
	if(page instanceof TitleAreaDialog) return new DialogErrorHandler((TitleAreaDialog)page);
	return null;
}

// Nested classes
public class PageErrorHandler implements IFieldErrorMessageHandler {
	
	private final WizardPage page;
	
	public PageErrorHandler(WizardPage page) {
		this.page = page;
	}
	@Override
	public void clearMessage() {
		page.setErrorMessage(null);
		page.setMessage(null,DialogPage.WARNING);
		page.setPageComplete(true);
	}
	@Override
	public void handleErrorMessage(String message, String arg) {
		page.setMessage(null,DialogPage.WARNING);
		page.setErrorMessage(message);
		page.setPageComplete(false);
	}
	@Override
	public void handleWarningMessage(String message, String arg) {
		page.setErrorMessage(null);
		page.setMessage(message,DialogPage.WARNING);
		page.setPageComplete(true);
	}
	
}

public class DialogErrorHandler implements IFieldErrorMessageHandler {

	private final TitleAreaDialog dialog;
	
	public DialogErrorHandler(TitleAreaDialog dialog) {
		this.dialog = dialog;
	}
	@Override
	public void clearMessage() {
		dialog.setErrorMessage(null);
		dialog.setMessage(null); // TODO check
	}
	@Override
	public void handleErrorMessage(String message, String arg) {
		dialog.setErrorMessage(message); // TODO dialog set complete false
	}
	@Override
	public void handleWarningMessage(String message, String arg) {
		dialog.setMessage(message);
	}
}
}