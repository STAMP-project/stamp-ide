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
package eu.stamp.wp4.descartes.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import eu.stamp.eclipse.descartes.jira.DescartesJiraWizard;

public class DescartesIssuesView extends DescartesAbstractView {
	
	public static final String ID = "eu.stamp.wp4.descartes.view.issues";
    
	private List<String> info;
	
	private static DescartesJiraWizard wizard;
	
	public DescartesIssuesView() {
		super();
        resetWizard();
	}
	
	public static void resetWizard() {
		try {
			wizard = new DescartesJiraWizard();
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void createPartControl(Composite parent) {
		
		super.createPartControl(parent);
		
		browser.addLocationListener(new LocationListener() {
			@Override
			public void changing(LocationEvent event) {}
			
			@Override
			public void changed(LocationEvent event) {
				try {
					URL direction = new URL(event.location);
					InputStream stream = direction.openStream();
				    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
				    String line;
				    info = new LinkedList<String>();
				    boolean packageFound = false;
				    boolean classFound = false;
				    while((line = reader.readLine()) != null) {
				    	info.add(line);
				    	if(line.contains("Package")) packageFound = true;
				        if(line.contains("Class")) classFound = true;
				    }
				    reader.close();
				    if(jiraButton != null && !jiraButton.isDisposed())
				    	jiraButton.setEnabled(packageFound && classFound);
				    	} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
         jiraButton.addSelectionListener(new SelectionAdapter() {
			 @Override
			 public void widgetSelected(SelectionEvent e) {
				 StringBuilder builder = new StringBuilder();
				 for(String sr : info) builder.append(sr);
                 String target = builder.toString();
                 int start = target.indexOf("<title>") + 7;
                 int end = target.indexOf("</title>");
                 String title = target.substring(start,end);
                 
                 if(wizard != null) {
                	 wizard.setTitle(title);
                	 wizard.parseDescription(target);
                	 Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
					WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getShell(),wizard);
					dialog.open();
						}	        
		});
	}
			 }
			 });
	}
	
	@Override
	protected boolean putJiraButton() { return true; }
}