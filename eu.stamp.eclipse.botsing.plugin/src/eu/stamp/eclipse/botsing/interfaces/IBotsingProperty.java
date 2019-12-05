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
package eu.stamp.eclipse.botsing.interfaces;

import eu.stamp.eclipse.botsing.listeners.IBotsingPropertyListener;

/**
 *  A BotsingPropery is the smallest unit of Botsing 
 *  launching information
 */
public interface IBotsingProperty extends IBotsingConfigurablePart {
    /**
     * get the string or strings to be added to the command line
     * to be parsed by Botsing
     * 
     * @return a String array with the command line contribution of this property
     */
	public String[] getPropertyString();

	public void callListeners();

	boolean containsLaunchInfo();

	public void addPropertyListener(IBotsingPropertyListener listener);
	
	public boolean isSet();
}
