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

public class OutputTraceProperty extends BotsingExplorerField {
	
	public static final String KEY = "Trace path";

	public OutputTraceProperty(String defaultValue, String name, 
			boolean compulsory,StringValidationToolkit kit) {
		super(defaultValue, KEY, name, compulsory,false,true,kit);
	}
	
	@Override
	protected String openExplorer() {
		
		DirectoryDialog dialog = new DirectoryDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell());
		
		String path = dialog.open();
		return path;
	}

}
