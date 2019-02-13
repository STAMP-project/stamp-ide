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

import java.net.URL;

import javax.inject.Inject;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;
/**
 * This class describes the common part of the PitCoverage 
 * and DescartesIssues views
 * @see eu.stamp.wp4.descartes.view.DescartesIssuesView
 * @see eu.stamp.wp4.descartes.view.PitCoverageView
 */
public abstract class DescartesAbstractView extends ViewPart {
	
	@Inject IWorkbench workbench;
	
	protected URL url;
	
	protected Browser browser;
	
	protected Button jiraButton;
	
	@Override
	public void createPartControl(Composite parent) {
          
	GridLayoutFactory.fillDefaults().numColumns(3).applyTo(parent);
	
	Link backLink = new Link(parent,SWT.PUSH);
	backLink.setText("<A>Back</A>");
	GridDataFactory.fillDefaults().applyTo(backLink);
	
	Link forwardLink = new Link(parent,SWT.PUSH);
	forwardLink.setText("<A>Forward</A>");	
	GridDataFactory.fillDefaults().applyTo(forwardLink);
	
	if(putJiraButton()) {
	jiraButton = new Button(parent,SWT.PUSH);
	jiraButton.setText("Open Jira ticket");
	GridDataFactory.swtDefaults().align(SWT.RIGHT,SWT.CENTER).applyTo(jiraButton);
	jiraButton.setEnabled(false);
	}
	
	browser = new Browser(parent,SWT.NONE);
	GridDataFactory.fillDefaults().span(3,1).grab(true, true)
	.minSize(300,400).applyTo(browser);

	backLink.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			browser.back();
		}
	});
	
	forwardLink.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			browser.forward();
		}
	});
	
	parent.pack();
	load();
	}

	@Override
	public void setFocus() {}
	
	public void setURL(URL url) {
         this.url = url;
	}
	
	public void load() {
		if(browser != null)if(!browser.isDisposed())if(url != null)
			browser.setUrl(url.toString());
	}
	
	protected boolean putJiraButton() { return false; }
}
