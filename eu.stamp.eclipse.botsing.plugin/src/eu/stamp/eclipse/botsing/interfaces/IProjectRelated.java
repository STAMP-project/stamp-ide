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

import org.eclipse.jdt.core.IJavaProject;

/**
 * Objects affected by a project change implement this interface
 */
public interface IProjectRelated {
    /**
     * this method is responsible for updating the object when
     * the project changes
     * 
     * @param newProject, the new project
     */
	public void projectChanged (IJavaProject newProject);
}
