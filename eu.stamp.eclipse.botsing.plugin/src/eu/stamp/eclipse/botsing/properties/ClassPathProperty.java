/*******************************************************************************
 * Copyright (c) 2018 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ricardo José Tejada García (Atos) - main developer
 * Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.eclipse.botsing.properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.PlatformUI;

import com.richclientgui.toolbox.validation.string.StringValidationToolkit;

/**
 * @see eu.stamp.eclipse.botsing.properties.BotsingExplorerField
 */
public class ClassPathProperty extends BotsingExplorerField {
	
	private String folderPath;
	
	private final String folderKey;
	
	public ClassPathProperty(String defaultValue, 
			String key, String name,StringValidationToolkit kit) {
		super(defaultValue, key, name,true,true,true,kit);
		folderKey = "folderKey";
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
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy copy) {
		super.appendToConfiguration(copy);
		copy.setAttribute(folderKey,folderPath);
	}
	@Override
	public void load(ILaunchConfigurationWorkingCopy copy) {
		super.load(copy);
		try {
			folderPath = copy.getAttribute(folderKey, "");
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
