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

package eu.stamp.eclipse.plugin.dspot.wizard;

import java.util.List;
import java.util.LinkedList;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

import eu.stamp.eclipse.plugin.dspot.controls.Controller;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;

/**
 * 
 */
public class DSpotPage extends WizardPage {
	
	protected final String ID;
	
	/**
	 * 
	 */
	protected final List<DSpotDialog> dialogs;

	public DSpotPage(String pageName,String ID) {
		super(pageName);
		this.ID = ID;
		dialogs = new LinkedList<DSpotDialog>();
		setTitle("DSpot configuration");
		setDescription("configure DSpot");
	}
	
	public DSpotPage(String pageName,String ID,String title,String description) {
		super(pageName);
		this.ID = ID;
		dialogs = new LinkedList<DSpotDialog>();
		setTitle(title);
		setDescription(description);
		
	}
	
	public void addDialog(DSpotDialog dialog) {
		dialogs.add(dialog);
	}
	
	@Override
	public void createControl(Composite parent) {
		
		// create the composite
		Composite composite = new Composite(parent,SWT.V_SCROLL);
		GridLayout layout = new GridLayout();    // the layout of composite
		layout.numColumns = 3;
		composite.setLayout(layout);
		
		List<Controller> controllers = DSpotMapping.getInstance()
				.getControllersList(ID);
		for(Controller controller : controllers) {
			if(controller instanceof IPageUserElement)((IPageUserElement)controller).setPage(this);
			controller.createControl(composite);
			controller.loadProject();
		}
		
		if(!dialogs.isEmpty())for(DSpotDialog dialog : dialogs) {
			Link link = new Link(composite,SWT.NONE);
			link.setText("<A>open " + dialog.ID + "</A>");
			GridDataFactory.fillDefaults().span(3,1).grab(true,false).applyTo(link);
			link.addSelectionListener(new SelectionAdapter() {
		    	@Override
		    	public void widgetSelected(SelectionEvent e) {
		    		dialog.open();
		    	}
		    });
		}
		
		// required to avoid an error in the System
		setControl(composite);
		setPageComplete(true);
	}
}