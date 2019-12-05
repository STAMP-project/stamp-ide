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
package eu.stamp.eclipse.plugin.dspot.context;

import java.io.File;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;

public interface IDSpotContext {

	public IJavaProject getProject();
	
	public String[] getNoTestSourceFolders();
	
	public String[] getTestSourceFolders();
	
	public List<File> getTestFiles();
	
	public String getFullName(String partialName);
	
	public String[] getTestMethods();
	
	public void loadProject(IJavaProject project);
}
