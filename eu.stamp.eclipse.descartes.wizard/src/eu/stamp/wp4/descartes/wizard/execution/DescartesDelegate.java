/*******************************************************************************
 * Copyright (c) 2018 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Ricardo José Tejada García (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.wp4.descartes.wizard.execution;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.m2e.internal.launch.MavenLaunchDelegate;

import eu.stamp.wp4.descartes.view.DescartesViewsActivator;

@SuppressWarnings("restriction")
/**
 *  this delegate calls their super class, the MavenLaunchDelegate to execute Descartes and
 *  after the execution displays the html reports in the Descartes Eclipse view
 */
public class DescartesDelegate extends MavenLaunchDelegate {
	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
		      throws CoreException {
         super.launch(configuration, mode, launch, monitor);
         
         // after finishing the process show the Descartes view for the html summaries
         while(!launch.isTerminated());
         DescartesViewsActivator viewsActivator = new DescartesViewsActivator(new File(
        		 configuration.getAttribute(ATTR_POM_DIR,"") + "/target/pit-reports"));
         viewsActivator.activate(); 
	}
}
