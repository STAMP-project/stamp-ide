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

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import eu.stamp.eclipse.plugin.dspot.controls.Controller;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;
import eu.stamp.eclipse.plugin.dspot.properties.DSpotProperties;

/**
 * 
 */
@SuppressWarnings("restriction")
public class ProjectManager {
	/**
	 * 
	 */
	private Text text;
	
	public void setText(String string) {
		if(text == null || text.isDisposed()) return;
		text.setText(string);
	}
	/**
	 * 
	 * @param parent 
	 */
	public void createProjectControl(Composite parent) {
		Label projectLabel = new Label(parent,SWT.NONE);
		projectLabel.setText("Project : ");
		projectLabel.setToolTipText(" select the target maven project");
		GridDataFactory.swtDefaults().indent(DSpotProperties.INDENT).applyTo(projectLabel);
		
		text = new Text(parent,SWT.BORDER | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().indent(DSpotProperties.INDENT).grab(true,false).applyTo(text);
		text.setText(DSpotContext.getInstance().getProject()
				.getProject().getFullPath().toString());
		Button button = new Button(parent,SWT.PUSH);
		button.setText("Select");
		GridDataFactory.fillDefaults().indent(DSpotProperties.INDENT).applyTo(button);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IJavaProject project = showProjectDialog();
                loadProject(project);
			}
		});
	}
	
	public IJavaProject getProjectFromName(String name) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(name);
		if(project instanceof IJavaProject) return (IJavaProject) project;
		else if(project != null) return JavaCore.create(project);
		return null;
	}
	
	public void loadProject(IJavaProject project) {
		if(project == null) return;
		DSpotContext.getInstance().loadProject(project);
		List<Controller> controllers = DSpotMapping.getInstance().getAllControllers();
		for(Controller controller : controllers) controller.loadProject();
		text.setText(project.getElementName());
}
	
	private IJavaProject showProjectDialog() {

		Class<?>[] acceptedClasses = new Class[] {IJavaProject.class,IProject.class};
		TypedElementSelectionValidator validator = new TypedElementSelectionValidator(acceptedClasses,true);
		ViewerFilter filter= new TypedViewerFilter(acceptedClasses) {
		@Override
		public boolean select(Viewer viewer,Object parentElement, Object element) {
		if(element instanceof IProject) {
		try {
		return ((IProject)element).hasNature(DSpotProperties.MAVEN_NATURE);
		} catch (CoreException e) {
		e.printStackTrace();
		}
		}
		if(element instanceof IJavaProject) {
		try {
		return ((IJavaProject)element).getProject().hasNature(DSpotProperties.MAVEN_NATURE);
		} catch (CoreException e) {
		e.printStackTrace();
		}
		}
		return false;
		}
		};

		  IWorkspaceRoot fWorkspaceRoot= ResourcesPlugin.getWorkspace().getRoot();
		        
		        StandardJavaElementContentProvider provider= new StandardJavaElementContentProvider();
		        ILabelProvider labelProvider= new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
		        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		        ElementTreeSelectionDialog dialog= new ElementTreeSelectionDialog(shell, labelProvider, provider);
		        dialog.setValidator(validator);
		        dialog.setComparator(new JavaElementComparator());
		        dialog.setTitle(" Select a project ");
		        dialog.setMessage(" Select a project ");
		        dialog.setInput(JavaCore.create(fWorkspaceRoot));
		        dialog.addFilter(filter);
		        dialog.setHelpAvailable(false);

		        if(dialog.open() == Window.OK) {
		            Object[] results = dialog.getResult();
		            for(Object ob : results) {
		            if(ob instanceof IJavaProject) { 
		            IJavaProject jProject = (IJavaProject)ob;
		            return jProject;
		             }
		            }
		        }
		        return null;
		}
}