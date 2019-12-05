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
package eu.stamp.eclipse.plugin.dspot.controls;

import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;

public class EnableCondition {
	
	private final boolean equal; // true means ==, false means !=
	
	private final String key,value;
	
	EnableCondition(String key,String value,boolean equal){
		this.key = key;
		this.value = value;
		this.equal = equal;
	}
	
	public boolean enable() {
       boolean result = DSpotMapping.getInstance().getValue(key)
    		   .equalsIgnoreCase(value);
       return equal == result;
	}

}
