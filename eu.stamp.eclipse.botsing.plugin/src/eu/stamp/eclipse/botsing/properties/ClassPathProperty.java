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

import eu.stamp.botsing.Main;

/**
 * @see eu.stamp.eclipse.botsing.properties.BotsingExplorerField
 */
public class ClassPathProperty extends BotsingExplorerField {
	
	private String folderPath;
	
	private final String folderKey;
	
	public ClassPathProperty(String defaultValue, 
			String key, String name) {
		super(defaultValue, key, name);
		folderKey = "folderKey";
	}
	@Override
	protected String openExplorer() {
		
		final DirectoryDialog dialog = new DirectoryDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell());
		
		folderPath = dialog.open();
		
		// the class Main in Botsing contains a method to get the
		// class path from a folder
		Main.bin_path = folderPath;
		return Main.getListOfDeps();
		
	}
	
	/**
	 * in this case the array key is the elemnt 0 in the array
	 * and the value the element 1
	 */
	@Override
	public String[] getPropertyString() { 
		System.setProperty("user.dir",folderPath);
		return new String[] {key, data};
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
