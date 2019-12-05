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
package eu.stamp.eclipse.botsing.dialog;

import org.eclipse.swt.widgets.Shell;

import eu.stamp.eclipse.wizard.dialogs.ExecutionErrorDialog;

public class BotsingExecutionErrorDialog extends ExecutionErrorDialog {

	public BotsingExecutionErrorDialog(Shell parentShell, boolean toolError) {
		super(parentShell, toolError);
	}

	@Override
	protected String getPluginErrorDirection() { 
		return "https://github.com/STAMP-project/stamp-ide/issues";
	}

	@Override
	protected String getPluginErrorMessage() {
		return "Botsing plugin error, please check the .log file,"
				+ " you can report it in : ";
	}

	@Override
	protected String getToolErrorDirection() {
		return "https://github.com/STAMP-project/botsing/issues";
	}

	@Override
	protected String getToolErrorMessage() {
		return "Error during Botsing execution, please check the .log file,"
				+ " you can report it in : ";
	}

	@Override
	protected String getToolName() {
		return "Botsing";
	}

}
