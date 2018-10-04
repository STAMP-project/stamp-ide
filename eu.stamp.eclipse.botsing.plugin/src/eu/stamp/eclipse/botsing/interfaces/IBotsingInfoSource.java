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
package eu.stamp.eclipse.botsing.interfaces;

import eu.stamp.eclipse.botsing.launch.BotsingPartialInfo;

/**
 *  Objects that provides Botsing launch information fragments
 *  implement this interface
 */
public interface IBotsingInfoSource {
    /**
     * This method is responsible of creating and return a
     * BotsingPartialInfo object containing the information fragment
     * provided by this object
     * 
     * @return a BotsingPartialInfo object containing a fragment of launching information
     * @see  eu.stamp.eclipse.botsing.launch.BotsingPartialInfo
     */
	public BotsingPartialInfo getInfo();
}
