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
	
	/**
	 *  Instance of the information container class
	 */
	private DescartesWizardConfiguration wConf;
	
    /**
     *   List with the updatable parts of the wizard
     */
	private ArrayList<IDescartesWizardPart> partsList = new ArrayList<IDescartesWizardPart>(1);
	
	/**
	 *  Page 1
	 */
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
			DescartesWizardConstants.DESCARTES_PLUGIN_ID),new Path("images/stamp.png"),null);
	ImageDescriptor descriptor = ImageDescriptor.createFromURL(iconStampURL);
	return descriptor.createImage();
	}
	@Override
	public boolean performFinish() {
		String pomName = one.getPomName();
		DescartesEclipseJob job = new DescartesEclipseJob(wConf.getProjectPath(),pomName);
		String[] texts = one.getMutatorsSelection();
		wConf.getDescartesParser().preparePom(texts,pomName);
		job.schedule();
		return true;
	}
	/**
	 *  This method updates the wizard taking the updatable parts list and calling the 
	 *  update method of each element in the list, this method is used 
	 *  when the user changes of project
	 */
	public void updateWizardParts() {
		for(int i = 0; i < partsList.size(); i++) partsList.get(i).updateDescartesWizardPart(wConf);
	}
	/**
	 * This method allows to change the information container class and refresh the wizard
	 * @param wConf : the new configuration
	 */
	public void setWizardConfiguration(DescartesWizardConfiguration wConf) {
		this.wConf = wConf;
		updateWizardParts();
	}
}
