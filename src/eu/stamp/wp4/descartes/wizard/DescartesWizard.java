package eu.stamp.wp4.descartes.wizard;

import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;

import eu.stamp.wp4.descartes.wizard.configuration.DescartesWizardConfiguration;
import eu.stamp.wp4.descartes.wizard.configuration.IDescartesWizardPart;
import eu.stamp.wp4.descartes.wizard.execution.DescartesEclipseJob;
import eu.stamp.wp4.descartes.wizard.utils.DescartesWizardConstants;

public class DescartesWizard extends Wizard {
	
	private DescartesWizardConfiguration wConf;

	private ArrayList<IDescartesWizardPart> partsList = new ArrayList<IDescartesWizardPart>(1);
	
	protected DescartesWizardPage1 one;
	
	public DescartesWizard(DescartesWizardConfiguration wConf) {
		super();
		setNeedsProgressMonitor(true);
		setHelpAvailable(true);
		this.wConf = wConf;
	}
	
	@Override
	public void addPages() {
		one = new DescartesWizardPage1(this);
		addPage(one);
		partsList.add(one);
		updateWizardParts();
	}
	@Override
	public String getWindowTitle() { 
		return "Descartes Wizard";	
	}
	@Override
	public Image getDefaultPageImage() {
	final URL iconStampURL = FileLocator.find(Platform.getBundle(
			DescartesWizardConstants.DESCARTES_PLUGIN_ID),new Path("images/Stamp.png"),null);
	ImageDescriptor descriptor = ImageDescriptor.createFromURL(iconStampURL);
	return descriptor.createImage();
	}
	@Override
	public boolean performFinish() {
		DescartesEclipseJob job = new DescartesEclipseJob(wConf.getProjectPath());
		job.schedule();
		return true;
	}
	
	public void updateWizardParts() {
		for(int i = 0; i < partsList.size(); i++) partsList.get(i).updateDescartesWizardPart(wConf);
	}
	public void setWizardConfiguration(DescartesWizardConfiguration wConf) {
		this.wConf = wConf;
		updateWizardParts();
	}
}
