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
package eu.stamp.eclipse.dspot.plugin.launch.ui;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SegmentEvent;
import org.eclipse.swt.events.SegmentListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings("restriction")
public class DSpotLaunchConfigurationTab extends AbstractLaunchConfigurationTab {

	private static final String FIRST_PART = "eu.stamp-project:dspot-maven:LATEST:amplify-unit-tests";
	
	private Text projectText,commandText;
	
	private String command,project,originalProject,originalCommand;
	
	public DSpotLaunchConfigurationTab() {
		super();
		originalProject = "";
		originalCommand = "";
	}
	
	@Override
	public void createControl(Composite parent) {
		
		Composite container = new Group(parent,SWT.BORDER);
		setControl(container);
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(container);
		
		// project
		Label projectLabel = new Label(container,SWT.NONE);
		projectLabel.setText("Absolute path of the project : ");
		
		projectText = new Text(container,SWT.BORDER);
		GridDataFactory.fillDefaults().span(2,1)
		.grab(true,false).applyTo(projectText);
		projectText.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
				project = projectText.getText();
				updateLaunchConfigurationDialog();
			}
		});
		
		// command
		Label commandLabel = new Label(container,SWT.NONE);
		commandLabel.setText("Maven attributes : ");
		commandLabel.setToolTipText("-D<name>=<value>");
		GridDataFactory.swtDefaults().indent(0,6).applyTo(commandLabel);
		
		commandText = new Text(container,SWT.BORDER);
        GridDataFactory.fillDefaults().span(2,1).grab(true, false)
        .indent(0,6).applyTo(commandText);
        commandText.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
				command = commandText.getText();
				updateLaunchConfigurationDialog();
			}
        });
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// No default configuration
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		if(commandText != null && !commandText.isDisposed())try {
			projectText.setText(configuration.getAttribute(MavenLaunchConstants.ATTR_POM_DIR,""));
			commandText.setText(configuration.getAttribute(MavenLaunchConstants.ATTR_GOALS,"")
					.replaceAll(FIRST_PART,""));
			originalProject = projectText.getText();
			originalCommand = commandText.getText();
		} catch(CoreException e) { e.printStackTrace(); }
		updateLaunchConfigurationDialog();
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
	    configuration.setAttribute(MavenLaunchConstants.ATTR_POM_DIR,project);
	    configuration.setAttribute(MavenLaunchConstants.ATTR_GOALS,FIRST_PART + " " + command);
	    String[] properties = command.replaceAll(" ","").split("-D");
        String[] parts;
	    for(String property : properties){
        	parts = property.split("=");
	    	if(parts.length == 2)configuration.setAttribute(parts[0],parts[1]);
        }
	}
	
	@Override
	public boolean isDirty() {
		return !project.equalsIgnoreCase(originalProject) 
				|| !command.equalsIgnoreCase(originalCommand);
	}
	
	@Override
	public boolean canSave() {
		if(project == null || command == null) {
			setErrorMessage("project field is empty");
			return false;
		}
		if(command ==  null || command.replaceAll(" ","").isEmpty()) {
			setErrorMessage("maven arguments field is empty");
			return false;
		}
		File folder = new File(project);
		if(!folder.exists() || !folder.isDirectory()) {
			setErrorMessage("the project field doesn't point a folder");
			return false;
		}
		setErrorMessage(null);
		return true;
	}

	@Override
	public String getName() { return "DSpot launch configuration tab"; }
}
