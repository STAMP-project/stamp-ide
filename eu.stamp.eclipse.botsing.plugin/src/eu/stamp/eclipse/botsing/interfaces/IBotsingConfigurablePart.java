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

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 *  Objects affected when a configuration is loaded implement this interface
 */
public interface IBotsingConfigurablePart {
    /**
     * this method is responsible for setting a property 
     * describing the state of this object in the configuration
     * 
     * @param configuration, the working copy of the configuration that will be saved
     */
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy configuration);
	/**
	 * this method is responsible for loading the object's state of a configuration
	 * and update the object
	 * 
	 * @param configuration, the working copy of the configuration that will be saved
	 */
	public void load(ILaunchConfigurationWorkingCopy configuration);
}
