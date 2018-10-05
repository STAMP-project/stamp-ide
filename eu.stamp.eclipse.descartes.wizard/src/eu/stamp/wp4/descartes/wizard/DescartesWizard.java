/*******************************************************************************
 * Copyright (c) 2018 Atos
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
package eu.stamp.wp4.descartes.wizard;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.xml.sax.SAXException;

import eu.stamp.eclipse.descartes.plugin.pom.DescartesPomParser;
import eu.stamp.eclipse.descartes.wizard.dialogs.AddMutatorDialog;
import eu.stamp.eclipse.descartes.wizard.dialogs.OutputFormatsDialog;
import eu.stamp.eclipse.descartes.wizard.interfaces.IDescartesConfigurablePart;
import eu.stamp.wp4.descartes.wizard.configuration.DescartesWizardConfiguration;
import eu.stamp.wp4.descartes.wizard.execution.DescartesEclipseJob;
import eu.stamp.wp4.descartes.wizard.utils.DescartesWizardConstants;

public class DescartesWizard extends Wizard 
                        implements IDescartesConfigurablePart {
	
	/**
	 *  Instance of the information container class
	 */
	private DescartesWizardConfiguration wConf;
	
    /**
     *   List with the updatable parts of the wizard
     */
	//private ArrayList<IDescartesWizardPart> partsList = new ArrayList<IDescartesWizardPart>(1);

	private List<IDescartesConfigurablePart> parts;
	private OutputFormatsDialog outputsDialog;
	/**
	 *  Page 1
	 */
	protected DescartesWizardPage1 one;
	
	public DescartesWizard(DescartesWizardConfiguration wConf) {
		super();
		setNeedsProgressMonitor(true);
		setHelpAvailable(true);
		this.wConf = wConf;
		parts = new LinkedList<IDescartesConfigurablePart>();
	}
	
	@Override
	public void addPages() {
		outputsDialog = 
				new OutputFormatsDialog(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow()
						.getShell());
		one = new DescartesWizardPage1(this,outputsDialog);
		addPage(one);
		parts.add(one);
		parts.add(outputsDialog);
		//partsList.add(one);
	}
	@Override
	public String getWindowTitle() { 
		return "Descartes Wizard";	
	}
	@Override
	public Image getDefaultPageImage() {
	final URL iconStampURL = FileLocator.find(Platform.getBundle(
			DescartesWizardConstants.DESCARTES_PLUGIN_ID),new Path("images/stamp.png"),null);
	ImageDescriptor descriptor = ImageDescriptor.createFromURL(iconStampURL);
	Image image = descriptor.createImage();
	AddMutatorDialog.image = image;
	return image;
	}
	@Override
	public boolean performFinish() {
		String pomName = one.getPomName();
		String configurationName = one.getConfigurationName();
		DescartesEclipseJob job = new DescartesEclipseJob(wConf.getProjectPath(),
				                          pomName,configurationName,this);
		//String[] texts = one.getMutatorsSelection();
		try {
			DescartesPomParser parser = new DescartesPomParser(wConf.getProjectPath(),pomName);
		    parser.preparePom(one.getMutatorsList(),outputsDialog.getFormatList());
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		//wConf.getDescartesParser().preparePom(texts,pomName,outputsDialog);
		job.schedule();
		return true;
	}

	/**
	 * This method allows to change the information container class and refresh the wizard
	 * @param wConf : the new configuration
	 */
	public void setWizardConfiguration(DescartesWizardConfiguration wConf) {
		this.wConf = wConf;
	}
	/**
	 * @return the DescartesWizardConfiguration object
	 */
	public DescartesWizardConfiguration getWizardConfiguration() {
		return wConf;
	}

	@Override
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy copy) {
		for(IDescartesConfigurablePart part : parts)
			part.appendToConfiguration(copy);
	}

	@Override
	public void load(ILaunchConfigurationWorkingCopy copy) {
		for(IDescartesConfigurablePart part : parts)
			part.load(copy);
	}
}
