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

package eu.stamp.eclipse.plugin.dspot.properties;

import org.eclipse.swt.graphics.Point;

/**
 *
 */
public abstract class DSpotProperties {
	
	public static final String MAVEN_NATURE = "org.eclipse.m2e.core.maven2Nature";
	public static final String PLUGIN_ID = "eu.stamp.eclipse.dspot.wizard";
	public static final String CONFIGURATION_ID = "eu.stamp.dspot.configuration";
	public static final String PROJECT_KEY = "project";
	public static final String CHECK_EXTRA_KEY = "CHECK";
	public static final Point INDENT = new Point(4,6);
	public static final String SEPARATOR = ",";
	
	// System dependent
	private static String PATH_SEPARATOR;

	public static String getPathSeparator() {
		if(PATH_SEPARATOR == null) {
			if(System.getProperty("os.name")
				.contains("indow")) PATH_SEPARATOR = "\\";
			else PATH_SEPARATOR = "/";
		}
		return PATH_SEPARATOR;
	}
}
