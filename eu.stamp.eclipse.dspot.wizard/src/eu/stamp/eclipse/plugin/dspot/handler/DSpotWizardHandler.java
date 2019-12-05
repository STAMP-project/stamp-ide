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

package eu.stamp.eclipse.plugin.dspot.handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.Workbench;

import eu.stamp.eclipse.plugin.dspot.context.DSpotContext;
import eu.stamp.eclipse.plugin.dspot.files.DSpotFileUtils;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;
import eu.stamp.eclipse.plugin.dspot.properties.DSpotProperties;
import eu.stamp.eclipse.plugin.dspot.wizard.DSpotPage;
import eu.stamp.eclipse.plugin.dspot.wizard.DSpotWizard;

/**
 * 
 */
@SuppressWarnings("restriction")
public class DSpotWizardHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DSpotContext.reset();
		DSpotMapping.reset();
		IJavaProject selectedProject = obtainProject();
		if(selectedProject == null) {
            MessageDialog.openError(HandlerUtil.getActiveShell(event),
            		"No project selected","Please, select a java, maven project and open this dialog again");
			return null;
		} else
			try {
				if(!selectedProject.getProject().hasNature(DSpotProperties.MAVEN_NATURE)) {
                    MessageDialog.openError(HandlerUtil.getActiveShell(event),
                    		"No maven project selected","The selected project must be a maven project");
					return null;
				}
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
		DSpotContext.getInstance().loadProject(selectedProject);
		final URL templateURL = FileLocator.find(Platform.getBundle(
				DSpotProperties.PLUGIN_ID),
				new Path("files/list.txt"),null);
		InputStream stream;
		try {
			stream = templateURL.openStream();
			List<DSpotPage> pages = DSpotFileUtils.parseTemplates(stream);
			if(pages == null) {
				System.out.println("ERROR parsing templates");
				return null;
			}
			DSpotWizard wizard = new DSpotWizard(pages);
			WizardDialog wizDiag = new WizardDialog(HandlerUtil.getActiveShell(event),wizard);
			wizDiag.open();   // open the wizard
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * this method finds the selected project
	 * 
	 * @return The IJavaProject object describing the selected project
	 */
	private IJavaProject obtainProject() {

	IWorkbenchWindow activeWindow = Workbench.getInstance().getActiveWorkbenchWindow();

	ISelectionService selectionService  = activeWindow.getSelectionService();
	ISelection selection = selectionService.getSelection();
	IJavaProject jproject = null;
	Object element;

	if (selection instanceof IStructuredSelection) {
	element = ((IStructuredSelection) selection).getFirstElement();

	if (element instanceof IJavaElement) {
	jproject = ((IJavaElement) element).getJavaProject();
	return jproject;
	}
	if (element instanceof IProject) {

	IProject pro = (IProject) element;
	jproject = new JavaProject(pro, null);
	return jproject;

	}
	}
	if(jproject == null) {

	selection = selectionService.getSelection("org.eclipse.jdt.ui.PackageExplorer");

	if (selection instanceof IStructuredSelection) {
	element = ((IStructuredSelection) selection).getFirstElement();

	if (element instanceof IJavaElement) {
	jproject = ((IJavaElement) element).getJavaProject();
	return jproject;
	}}
	            selection = selectionService.getSelection("org.eclipse.ui.navigator.ProjectExplorer");

	if (selection instanceof IStructuredSelection) {
	element = ((IStructuredSelection) selection).getFirstElement();

	if (element instanceof IProject) {
	IProject pro = (IProject) element;
	jproject = new JavaProject(pro, null);
	return jproject;
	}}
	}
	return null;
	}
}