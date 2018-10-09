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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

import eu.stamp.eclipse.botsing.constants.BotsingPluginConstants;

/**
 * An instance of this class is responsible to provide access
 * to the list of existing Botsing launch configurations and 
 * get and set the one in use
 */
public class ConfigurationsManager {
	
	private ILaunchConfiguration[] configurations;
	
	private int configurationInUse;

	public ConfigurationsManager() {
	
		  // Find configurations
		try {
		ILaunchManager manager = DebugPlugin.getDefault()
				.getLaunchManager();
		configurations = 
				manager.getLaunchConfigurations(
				manager.getLaunchConfigurationType(
						BotsingPluginConstants.BOTSING_LAUNCH_ID));
				
		} catch (CoreException e) {
				e.printStackTrace();
			}
		
		configurationInUse = 0;
	}
	/**
	 * @return a string array containing the names of the existing configurations
	 */
	public String[] getConfigurationNames() {
		if(configurations == null) return new String[] {""};
		if(configurations.length < 1) return new String[] {""};
		
		String[] result = new String[configurations.length];
		for(int i = 0; i < configurations.length; i++)
            result[i] = configurations[i].getName();
		return result;
	}
	/**
	 * set what configuration will be used
	 * @param name the name of the configuration to set as "in use"
	 */
	public void setConfigurationInUse(String name) {
       for(int i = 0; i < configurations.length; i++) 
    	   if(configurations[i].getName().equalsIgnoreCase(name)) {
    	   configurationInUse = i;
    	   return;
       }
	}
	/**
	 * @return a working copy of the configuration in use
	 */
	public ILaunchConfigurationWorkingCopy getCopy() {
		try {
			return configurations[configurationInUse].getWorkingCopy();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

}
