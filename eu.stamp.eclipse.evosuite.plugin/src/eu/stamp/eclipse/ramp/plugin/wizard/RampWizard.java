/*******************************************************************************
 * Copyright (c) 2019 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Ricardo Jose Tejada Garcia (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.eclipse.ramp.plugin.wizard;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;

import eu.stamp.eclipse.ramp.plugin.constants.RampPluginConstants;
import eu.stamp.eclipse.ramp.plugin.job.RampConfiguration;
import eu.stamp.eclipse.ramp.plugin.job.RampJobQueue;

public class RampWizard extends Wizard {
	
	private final RampConfiguration evosuiteConfiguration;
	
	public RampWizard(IJavaProject project) throws IOException {
		evosuiteConfiguration = new RampConfiguration(project);
	}
	
	@Override
	public void addPages() {
		addPage(new RampPage1("First page",evosuiteConfiguration));
	}
	
	@Override
	public boolean performFinish() {
		RampJobQueue jobQueue = evosuiteConfiguration.createJob();
		jobQueue.schedule();
		return true;
	}
	
	@Override
	public Image getDefaultPageImage() {
		URL url = FileLocator.find(Platform.getBundle(
				RampPluginConstants.EVOSUITE_PLUGIN_ID),new Path("images/stamp.png"),null);
		return ImageDescriptor.createFromURL(url).createImage();
	}
	
	@Override
	public String getWindowTitle() {
		return "RAMP Wizard";
	}
}
