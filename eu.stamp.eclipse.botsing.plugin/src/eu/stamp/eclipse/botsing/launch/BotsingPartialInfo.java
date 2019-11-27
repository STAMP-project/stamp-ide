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
package eu.stamp.eclipse.botsing.launch;

import java.util.ArrayList;
import java.util.List;

import eu.stamp.eclipse.botsing.interfaces.IBotsingProperty;

/**
 * objects of this class are produced by objects that contains Botsing properties
 * this object carries a list of properties and one of these objects contains the
 * name of the configuration
 * 
 * @see eu.stamp.eclipse.botsing.interfaces.IBotsingInfoSource
 * @see eu.stamp.eclipse.botsing.launch.BotsingLaunchInfo
 */
public class BotsingPartialInfo {

	private String name;

	private final List<IBotsingProperty> properties;
	
	public BotsingPartialInfo(List<IBotsingProperty> properties) {
		this(null,properties);
	}
	
	public BotsingPartialInfo(String name,List<IBotsingProperty> properties) {
		this.name = name;
		this.properties = new ArrayList<IBotsingProperty>(properties.size());
	    for(IBotsingProperty property : properties)
	    	if(property.containsLaunchInfo())
	    		this.properties.add(property);
	}
	
	public boolean nameIsSet() {
		boolean result = (name != null);
		if(result)if(name.isEmpty()) result = false;
		return result; 
		}
	
	public String getName() { return name; }
	
	public  List<IBotsingProperty> getProperties(){
		return properties;
	}
	
}
