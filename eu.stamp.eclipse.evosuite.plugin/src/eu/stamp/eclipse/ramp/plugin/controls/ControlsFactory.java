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
package eu.stamp.eclipse.ramp.plugin.controls;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SegmentEvent;
import org.eclipse.swt.events.SegmentListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import eu.stamp.eclipse.ramp.plugin.constants.RampPluginConstants;
import eu.stamp.eclipse.ramp.plugin.job.RampConfiguration;

public class ControlsFactory {
	
	protected RampConfiguration evosuiteConfiguration;
	
	protected static Properties TOOLTIPS;
	
	private static boolean TOOLTIPS_LOADED;
	
	// variables for the controls
	
	protected String propertyKey,labelText;
	
	protected int spinnerMin,spinnerMax,spinnerStep,spinnerDigits;
	
	
    public static ControlsFactory getFactory(RampConfiguration evosuiteConfiguration) {
    	return new ControlsFactory(evosuiteConfiguration);
    }

	protected ControlsFactory(RampConfiguration evosuiteConfiguration) {
		this.evosuiteConfiguration = evosuiteConfiguration;
		if(TOOLTIPS == null) TOOLTIPS = new Properties();
		if(!TOOLTIPS_LOADED) loadProperties();
		spinnerDigits = 0;
		spinnerStep = 1;
	}
	
	// setters
	
	public ControlsFactory setPropertyKey(String propertyKey) {
		this.propertyKey = propertyKey;
		return this;
	}
	
	public ControlsFactory setLabelText(String labelText) {
		this.labelText = labelText;
		return this;
	}
	
	public ControlsFactory setSpinnerMin(int spinnerMin) {
		this.spinnerMin = spinnerMin;
		return this;
	}
	
	public ControlsFactory setSpinnerMax(int spinnerMax) {
		this.spinnerMax = spinnerMax;
		return this;
	}
	
	public ControlsFactory setSpinnerStep(int spinnerStep) {
		this.spinnerStep = spinnerStep;
		return this;
	}
	
	public ControlsFactory setSpinnerDigits(int spinnerDigits) {
		this.spinnerDigits = spinnerDigits;
		return this;
	}
	
	public void createSpinner(Composite composite) {
		putLabel(composite);
		int nColumns = ((GridLayout)composite.getLayout()).numColumns;
		Spinner spinner = new Spinner(composite,SWT.BORDER);
		GridDataFactory.fillDefaults().span(nColumns-1,1).grab(true,false).applyTo(spinner);
		spinner.setMinimum(spinnerMin);
		spinner.setMaximum(spinnerMax);
		spinner.setIncrement(spinnerStep);
		spinner.setData(propertyKey);
		if(spinnerDigits > 0) spinner.setDigits(spinnerDigits);
		spinner.setSelection(parseNumber(
				evosuiteConfiguration.getProperty(propertyKey)));
		spinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			     evosuiteConfiguration.setProperty(propertyKey,
			    		 spinner.getText().replaceAll(",","\\."));
			}
		});
	}
	
	public Text createText(Composite composite) {
		putLabel(composite);
		int nColumns = ((GridLayout)composite.getLayout()).numColumns;
		Text text = new Text(composite,SWT.BORDER);
	    GridDataFactory.fillDefaults().span(nColumns-1,1).grab(true,false).applyTo(text);
	    text.setText(evosuiteConfiguration.getProperty(propertyKey));
	    text.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
				evosuiteConfiguration.setProperty(propertyKey,text.getText());
			}
	    });
	    return text;
	}

	protected void putLabel(Composite composite) {
		Label label = new Label(composite,SWT.NONE);
		label.setText(labelText);
		if(TOOLTIPS_LOADED) label.setToolTipText(TOOLTIPS.getProperty(propertyKey));
	}
	
	protected int parseNumber(String number) {
		number = number.replaceAll("\\.","") ;
		return Integer.parseInt(number);
	}
	
	private void loadProperties() {
		URL propertiesURL = FileLocator.find(Platform.getBundle(
		         RampPluginConstants.EVOSUITE_PLUGIN_ID),
		         new Path("files/tooltips.properties"),null);
		try {
			TOOLTIPS.load(propertiesURL.openStream());
			TOOLTIPS_LOADED = true;
		} catch(IOException e) {
			System.out.println("[ERROR] error loading tooltips");
			System.out.println("[ERROR] error in eu.stamp.eclipse.evosuite.plugin.wizard.EvosuiteWizardPage");
			TOOLTIPS_LOADED = false;
			e.printStackTrace();
		}
	}
	
}
