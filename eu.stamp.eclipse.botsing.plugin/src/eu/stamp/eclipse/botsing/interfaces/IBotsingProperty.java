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

/**
 *  A BotsingPropery is the smallest unit of Botsing 
 *  launching information
 */
public interface IBotsingProperty {
    /**
     * get the string or strings to be added to the command line
     * to be parsed by Botsing
     * 
     * @return a String array with the command line contribution of this property
     */
	public String[] getPropertyString();
}
