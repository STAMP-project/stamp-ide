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
package eu.stamp.eclipse.botsing.properties;

import java.util.LinkedList;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import eu.stamp.eclipse.botsing.call.InputManager;
import eu.stamp.eclipse.botsing.interfaces.IBotsingProperty;
import eu.stamp.eclipse.botsing.listeners.IBotsingPropertyListener;

/**
 * This property contains a list of properties that must be present all or nothing
 */
public class MultipleProperty implements IBotsingProperty {
	
	private final LinkedList<IBotsingProperty> innerProperties;
	
	public MultipleProperty() {
		innerProperties = new LinkedList<IBotsingProperty>();
	}
	
	public void addProperty(AbstractBotsingProperty property) {
		innerProperties.add(property);
	}

	@Override
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy configuration) {
		if(checkProperties())
			for(IBotsingProperty property : innerProperties) property.appendToConfiguration(configuration);
	}

	@Override
	public void load(ILaunchConfigurationWorkingCopy configuration) {
		for(IBotsingProperty property : innerProperties) property.load(configuration);
	}

	@Override
	public String[] getPropertyString() {
		if(checkProperties()) {
			StringBuilder builder = new StringBuilder();
			int i = 0;
			for(IBotsingProperty property : innerProperties) {
				String[] propertyStrings = property.getPropertyString();
				if(propertyStrings.length == 1) builder.append(propertyStrings[0]);
				else builder.append(propertyStrings[0]).append(' ').append(propertyStrings[1]);
				if(i < innerProperties.size() - 1) builder.append(InputManager.COMAND_SEPARATOR);
				i++;
			}
			return new String[] {builder.toString()};
		}
		return null;
	}

	/**
	 * @return true if all the properties are ok
	 */
    private boolean checkProperties() {
    	for(IBotsingProperty property : innerProperties)if(property.getPropertyString() == null
    			|| !property.isSet()) return false;
    	return true;
    }

	@Override
	public void callListeners() {
		for(IBotsingProperty property : innerProperties) property.callListeners();
	}

	@Override
	public boolean containsLaunchInfo() { return true; }
	
	@Override
	public boolean isSet() { return checkProperties(); }

	@Override
	public void addPropertyListener(IBotsingPropertyListener listener) {}
}
