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
package eu.stamp.eclipse.botsing.wizard.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.stamp.eclipse.botsing.model.generation.handler.StampHandler;
import eu.stamp.eclipse.botsing.wizard.BotsingWizard;

public class BotsingWizardHandler extends StampHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		WizardDialog wizardDialog = 
				new WizardDialog(HandlerUtil.getActiveShell(event),
						new BotsingWizard(getProject()));
		wizardDialog.open();
		return null;
	}

}
