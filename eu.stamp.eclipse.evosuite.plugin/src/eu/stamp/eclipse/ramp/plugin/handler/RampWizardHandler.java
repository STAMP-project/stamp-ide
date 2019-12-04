/*******************************************************************************
 * Copyright (c) 2019 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Ricardo Jose Tejada Garcia (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.eclipse.ramp.plugin.handler;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.stamp.eclipse.botsing.model.generation.handler.StampHandler;
import eu.stamp.eclipse.ramp.plugin.classpth.ClassPathCreator;
import eu.stamp.eclipse.ramp.plugin.wizard.RampWizard;

/**
 * Handler for opening the Evosuite wizard
 */
public class RampWizardHandler extends StampHandler {
	
	private ExecutionEvent event;
	
	private IJavaProject project;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		this.event = event;
		project = getProject();
		
		if(project == null) {
			MessageDialog.openError(HandlerUtil.getActiveShell(event),"No project selected",
					"There is no project selected, please select a project and open this dialog again");
			return null;
		}
		
		ClassPathCreator classPathCreator = new ClassPathCreator(project,HandlerUtil.getActiveShell(event));
		classPathCreator.createClassPathFile();
		
	    openWizard();
		return null;
	}
	
	private void openWizard() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					WizardDialog wizardDialog = new WizardDialog(
							HandlerUtil.getActiveShell(event),new RampWizard(project));
					 wizardDialog.open();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
