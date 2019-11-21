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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import eu.stamp.eclipse.botsing.interfaces.IBotsingProperty;
import eu.stamp.eclipse.botsing.listeners.IBotsingPropertyListener;
import eu.stamp.eclipse.botsing.listeners.IPropertyDataListener;

/**
 * Abstract implementation IBotsingProperty
 * the Botsing properties contains configuration information so this class implements
 * IBotsingConfigurablePart, the main differences between the subclasses will come from
 * the kind of widget associate to each subclass (text, spinner ...)
 * 
 * @see eu.stamp.eclipse.botsing.interfaces.IBotsingProperty
 * @see eu.stamp.eclipse.botsing.interfaces.IBotsingConfigurablePart
 */
public abstract class AbstractBotsingProperty 
     implements IBotsingProperty {
	
	protected final String defaultValue;
	
	protected final String key;
	
	protected final String name;
	
	protected String data;
	
	protected final boolean compulsory;
	
	protected final boolean isLaunchInfo;
	
	protected IBotsingPropertyListener propertyListener;
	
	protected final List<IPropertyDataListener> dataListeners;
	
	protected String tooltip;
	
	private Label label;
	
	protected AbstractBotsingProperty(String defaultValue,String key,
			String name,boolean compulsory,boolean isLaunchInfo) {
		
		this.defaultValue = defaultValue;
		this.key = key;
		this.name = name;
		this.data = defaultValue;
		this.compulsory = compulsory;
		this.isLaunchInfo = isLaunchInfo;
		dataListeners = new LinkedList<IPropertyDataListener>();
	}
	
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
		if(label != null)if(!label.isDisposed())
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					label.setToolTipText(tooltip);
				}
			});
	}
	
	@Override
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy configuration) {
     configuration.setAttribute(key, getData());
	}

	@Override
	public void load(ILaunchConfigurationWorkingCopy configuration) {
     try {
		String value = configuration.getAttribute(key,defaultValue);
		setData(value);
	} catch (CoreException e) { 
		e.printStackTrace(); 
		}
	}
	
	public void addPropertyListener(IBotsingPropertyListener listener) {
		this.propertyListener = listener;
	}
	
	public void addDataListener(IPropertyDataListener dataListener) {
		dataListeners.add(dataListener);
	}
	
	@Override
	public void callListeners() {
		if(propertyListener != null)	
		propertyListener.activate();
		for(IPropertyDataListener dataListener : dataListeners)
			dataListener.dataChange(data);
	}

	@Override
	public String[] getPropertyString() {
		
	    if(compulsory) return new String[] {key,getData()};
	    else if(getData() == null || getData().isEmpty()) return null;
	    else return new String[] {key + "=" + getData()};
	}
	
	public boolean isCompulsory() { return compulsory; }
	
	@Override
	public boolean containsLaunchInfo() { return isLaunchInfo; }
	
	protected String getKey() { return key; }
	
	protected String getName() { return name; }
	
	protected String getDefaultValue() { return defaultValue; }
	
	protected abstract String getData();
	
	/**
	 * this must be overrided
	 * @param data
	 */
	protected abstract void setData(String data);

	/**
	 * this must be override
	 * @param composite
	 */
	public void createControl(Composite composite) {
	       label = new Label(composite,SWT.NONE);
	       label.setText(name);
	       if(tooltip != null) label.setToolTipText(tooltip);
	}

}
