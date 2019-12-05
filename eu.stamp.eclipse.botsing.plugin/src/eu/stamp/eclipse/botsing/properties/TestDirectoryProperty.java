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
package eu.stamp.eclipse.botsing.properties;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.PlatformUI;

import com.richclientgui.toolbox.validation.string.StringValidationToolkit;

import eu.stamp.eclipse.text.validation.TextFieldValidatorFactory;

/**
 * This is the property associated with the directory to store the 
 * generated tests
 */
public class TestDirectoryProperty extends BotsingExplorerField {

	public TestDirectoryProperty(String defaultValue, String key, 
			String name,StringValidationToolkit kit) {
		super(defaultValue, key, name,true,true,false,kit);
		char[] allowed;
		if(System.getProperty("os.name").contains("indow"))
			allowed = new char[] {'\\','/',':','-','_'};
		else allowed = new char[] {'/','-','_'};
		TextFieldValidatorFactory.getFactory().notEmpty(
				TextFieldValidatorFactory.ERROR).onlyAlphaNumerical(TextFieldValidatorFactory.ERROR)
		.setExtraCharactersAllowed(allowed,TextFieldValidatorFactory.ERROR).applyTo(this);
	}

	@Override
	protected String openExplorer() {
		
		DirectoryDialog dialog = 
				new DirectoryDialog(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell());
		
		dialog.setMessage(" Select test directory ");
		dialog.setText(" Test directory selection ");
		
		String result = dialog.open();
		
		callListeners();
		return result;
	}

}
