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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.PlatformUI;

import com.richclientgui.toolbox.validation.string.StringValidationToolkit;

import eu.stamp.eclipse.text.validation.TextFieldValidatorFactory;

public class ModelProperty extends BotsingExplorerField {
	
	public static final String MODEL_KEY = "-model";
	
	private String folderPath;

	public ModelProperty(StringValidationToolkit kit) {
		super("",MODEL_KEY,"Model Path",false,true,true,kit);
		TextFieldValidatorFactory.getFactory().notEmpty(TextFieldValidatorFactory.ERROR)
		.pointsToDirectory(TextFieldValidatorFactory.ERROR).applyTo(this);
	}

	@Override
	protected String openExplorer() {

		final DirectoryDialog dialog = new DirectoryDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell());
		
		folderPath = dialog.open();
		
		return folderPath;
	}
	
	@Override
	public String[] getPropertyString() { return new String[] {key,getData()}; }
	
	@Override
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy copy) {
		super.appendToConfiguration(copy);
		copy.setAttribute(MODEL_KEY,folderPath);
	}
	@Override
	public void load(ILaunchConfigurationWorkingCopy copy) {
		super.load(copy);
		try {
			folderPath = copy.getAttribute(MODEL_KEY, "");
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isSet() {
		if(data == null || data.replaceAll(" ","").isEmpty()) return false;
		return true;
	}
}
