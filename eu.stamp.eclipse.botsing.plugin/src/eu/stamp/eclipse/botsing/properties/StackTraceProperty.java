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

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
//import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

import eu.stamp.eclipse.botsing.interfaces.IProjectRelated;

/**
 *  This is the property associated with the path of the .log file
 *  when the class path folder changes the explorer filter changes
 *  in order to start in a probably next point to the file
 */
public class StackTraceProperty 
             extends BotsingExplorerField implements IProjectRelated {

    private String filterPath;
    
    private FileDialog dialog;
	
	public StackTraceProperty(String defaultValue, String key, String name) {
		super(defaultValue, key, name);
	}
	@Override
	protected String openExplorer() {
		 
		 dialog = new FileDialog(
				 PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				 .getShell(),SWT.OK | SWT.MULTI);
		 dialog.setFilterExtensions(new String[] {"*.log"});
		 dialog.setText("Select log file");
		 if(filterPath != null)if(!filterPath.isEmpty())
		 dialog.setFilterPath(filterPath);
	     
	      String result = dialog.open();
	      return result;
	}
	@Override
	public void projectChanged(IJavaProject newProject) {
		filterPath = newProject.getProject().getLocation().toString();
		if(dialog != null)dialog.setFilterPath(filterPath);
	}
}