/*******************************************************************************
 * Copyright (c) 2018 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Ricardo José Tejada García (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.eclipse.descartes.wizard.validation;

import org.eclipse.jface.dialogs.DialogPage;
import com.richclientgui.toolbox.validation.IFieldErrorMessageHandler;

import eu.stamp.eclipse.descartes.wizard.interfaces.IDescartesPage;
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

