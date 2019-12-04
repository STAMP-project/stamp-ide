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
package eu.stamp.eclipse.botsing.wizard;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaProject;

import eu.stamp.eclipse.botsing.constants.BotsingPluginConstants;
import eu.stamp.eclipse.botsing.dialog.BotsingAdvancedOptionsDialog;
import eu.stamp.eclipse.botsing.interfaces.IBotsingConfigurablePart;
import eu.stamp.eclipse.botsing.interfaces.IBotsingInfoSource;
import eu.stamp.eclipse.botsing.launch.BotsingLaunchInfo;
import eu.stamp.eclipse.botsing.launch.BostingJob;
import eu.stamp.eclipse.botsing.launch.BotsingPartialInfo;
import eu.stamp.eclipse.botsing.launch.ConfigurationsManager;
import eu.stamp.eclipse.botsing.model.generation.load.GenerationConfigurationLoader;

public class BotsingWizard extends Wizard
                      implements IBotsingConfigurablePart {

	protected BotsingWizardPage page; 
	
	/**
	 * The pages and dialog of the wizard are configurable parts 
	 * (and they are composed by configurable parts)
	 * 
	 * @see eu.stamp.eclipse.botsing.interfaces.IConfigurablePart
	 */
	private List<IBotsingConfigurablePart> configurableParts;
	
	/**
	 * The object to manage the launch configurations
	 * 
	 * @see eu.stamp.eclipse.botsing.launch.ConfigurationsManager
	 */
	private final ConfigurationsManager configurationsManager;
	
	private final IJavaProject project;
	
	public BotsingWizard(IJavaProject project) {
		configurableParts = new LinkedList<IBotsingConfigurablePart>();
		configurationsManager = new ConfigurationsManager();
		this.project = project;
	}
	
	@Override
	public void addPages() {
		BotsingAdvancedOptionsDialog dialog = 
				new BotsingAdvancedOptionsDialog(
						PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell());
		configurableParts.add(dialog);
		
		page = new BotsingWizardPage(this,dialog);
		addPage(page);
		configurableParts.add(page);
	}
	@Override
	public String getWindowTitle() { return " Bootsing Wizard "; }
	
	@Override
	public Image getDefaultPageImage() {
		final URL iconStampURL = FileLocator.find(
				Platform.getBundle(BotsingPluginConstants.BOTSING_PLUGIN_ID),
				new Path("images/stamp.png"),null);
		ImageDescriptor descriptor = ImageDescriptor.createFromURL(iconStampURL);
		Image image = descriptor.createImage();
		BotsingAdvancedOptionsDialog.image = image;
		return image;
	}
	
	public ConfigurationsManager getConfigurationsManager() { return configurationsManager; }
	
	@Override
	public boolean performFinish() {

		List<BotsingPartialInfo> partialInfos = 
				new LinkedList<BotsingPartialInfo>();
		
		// get the partial information objects from their sources
        for(IBotsingConfigurablePart part : configurableParts)
        	if(part instanceof IBotsingInfoSource)
        	   partialInfos.add(((IBotsingInfoSource)part).getInfo());
        		
		BostingJob job = new BostingJob(
				new BotsingLaunchInfo(partialInfos),this);
		GenerationConfigurationLoader modelLoader = page.getLoader();
		if(modelLoader != null && !modelLoader.getNoLoad())job.setModelConfiguration(modelLoader.toString());
		try { 
			job.schedule();
		} catch(Exception e){
			e.printStackTrace();
			job.showErrorDialog(job.isToolError(job.getLaunch()));	
		}
		return true;
	}
	
	public void reconfigure(String configurationName) {
		configurationsManager.setConfigurationInUse(configurationName);
		ILaunchConfigurationWorkingCopy copy =
				configurationsManager.getCopy();
        this.load(copy);
	}
	
	public String[] getConfigurationNames() { 
		return configurationsManager.getConfigurationNames();
	}

	@Override
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy configuration) {
		for(IBotsingConfigurablePart part : configurableParts)
			part.appendToConfiguration(configuration);
	}

	@Override
	public void load(ILaunchConfigurationWorkingCopy configuration) {
		for(IBotsingConfigurablePart part : configurableParts)
			part.load(configuration);
	}
	
	public IJavaProject getProject() { return project; }
}
