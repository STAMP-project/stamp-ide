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
package eu.stamp.eclipse.botsing.launch;

import java.util.LinkedList;
import java.util.List;

import eu.stamp.eclipse.botsing.interfaces.IBotsingProperty;

/**
 *  An instance of this class carries the complete list of properties 
 *  and the name of the configuration to provide information
 *  to the BotsingJob object 
 */
public class BotsingLaunchInfo {
	
	private final String name;

	private final List<IBotsingProperty> properties;
	
	public BotsingLaunchInfo(String name,List<IBotsingProperty> properties) {
		this.name = name;
		this.properties = properties;
	}
	/**
	 * the usual form of instantiate this class requires a list
	 * of BotsingPartialInfoObjects to take their properties and the name
	 *
	 * @param partialInfos, objects with a partial list of BotsingProperties
	 * @see eu.stamp.eclipse.botsing.launch.BotsingPartialInfo
	 * @see eu.stamp.eclipse.botsing.interfaces.IBotsingProperty
	 */
	public BotsingLaunchInfo (List<BotsingPartialInfo> partialInfos) {
		String name = "new_configuration"; // default
		properties = new LinkedList<IBotsingProperty>();
		
		for(BotsingPartialInfo partialInfo : partialInfos) {
			if(partialInfo.nameIsSet()) name = partialInfo.getName();
			List<IBotsingProperty> list = partialInfo.getProperties();
			for(IBotsingProperty property : list)
				properties.add(property);
		}
	
		this.name = name;
	}
	/**
	 * this method takes the information from the properties to 
	 * generate the command line
	 * @return an array with the Botsing command line
	 */
	public String[] getCommand() {
		List<String> resultList = new LinkedList<String>();
		for(IBotsingProperty property : properties) {
			String[] strings = property.getPropertyString();
			if(strings != null)for(String sr : strings) resultList.add(sr);
		}
		String[] result = new String[resultList.size() + 1];
	    int i = 0;
	    for(String sr : resultList) {
	    	result[i] = sr;
	    	i++;
	    }
	    result[resultList.size()] = "-Dno_runtime_dependency=false";
		return result;
	}
	/**
	 * @return the name of the configuration
	 */
	public String getName() { return name; }
}
