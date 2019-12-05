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
package eu.stamp.eclipse.descartes.wizard.interfaces;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
/**
 * This interface is implemented by classes that provide
 * information to the Descartes launch
 */
public interface IDescartesConfigurablePart {
    /**
     * appends the information fragment of this class to the configuration
     * @param copy : The LaunchConfigurationWorkingCopy for this execution 
     */
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy copy);
	/**
	 * get the information fragment corresponding to this class from
	 * the launch object and makes the necessary updates
	 * @param copy : The LaunchConfigurationWorkingCopy to be loaded
	 */
	public void load(ILaunchConfigurationWorkingCopy copy);
}
