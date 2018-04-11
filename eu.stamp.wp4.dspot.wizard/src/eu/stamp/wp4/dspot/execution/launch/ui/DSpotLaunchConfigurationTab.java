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
package eu.stamp.wp4.dspot.execution.launch.ui;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationDialog;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SegmentListener;
import org.eclipse.swt.events.SegmentEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.swt.widgets.Shell;

import eu.stamp.wp4.dspot.constants.DSpotWizardConstants;
import eu.stamp.wp4.dspot.wizard.utils.WizardConfiguration;

@SuppressWarnings("restriction")
public class DSpotLaunchConfigurationTab extends AbstractLaunchConfigurationTab {

	private IJavaProject javaProject;
	private String parameters;
	private Text projectText;
	private Text parametersText;
	
	@Override
	public void createControl(Composite parent) {

		Composite container = new Group(parent,SWT.BORDER);
		setControl(container);
		
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(container);
		
		Label projectLabel = new Label(container,SWT.NONE);
		projectLabel.setText("Project : ");
		GridDataFactory.swtDefaults().applyTo(projectLabel);
		
		projectText = new Text(container,SWT.BORDER);
		projectText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				parameters = projectText.getText();
			}	
		});
		projectText.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
				parameters = projectText.getText();
			}	
		});
		GridDataFactory.fillDefaults().grab(true, false).applyTo(projectText);
		
		Button projectButton = new Button(container,SWT.PUSH);
		projectButton.setText("Browse");
		GridDataFactory.swtDefaults().applyTo(projectButton);
		projectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showProjectDialog();
			}
		});  // end of the selection listener
		
		Label parametersLabel = new Label(container,SWT.NONE);
		parametersLabel.setText("Dspot execution parameters : ");
        GridDataFactory.swtDefaults().applyTo(parametersLabel);
		
        parametersText = new Text(container,SWT.BORDER);		
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(parametersText);
        parametersText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {		
			}
			@Override
			public void keyReleased(KeyEvent e) {	
				LaunchConfigurationDialog.getCurrentlyVisibleLaunchConfigurationDialog().updateButtons();
			}	
        });
	}



	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		
	}
    @Override
    public boolean canSave() {
    	return true;
    }
    @Override
    public boolean isDirty() {
    	return true;
    }
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {	
		
		String arguments = parametersText.getText();  // obtain the string with DSpot arguments

	      if(javaProject != null) {
     configuration.setAttribute(
		IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, 
		javaProject.getElementName());
		  }
		  if(parameters != null && !parameters.isEmpty()) {
     configuration.setAttribute(
		    IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, arguments);
		  }
	}

	@Override
	public String getName() {
		return " DSpot launch configuration tab ";
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			projectText.setText(configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""));
			parametersText.setText(configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, ""));
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	/**
	 * this method creates and opens the project selection dialog and handles the user selection
	 */
	private void showProjectDialog() {
		
		Class<?>[] acceptedClasses = new Class[] {IJavaProject.class,IProject.class};
		TypedElementSelectionValidator validator = new TypedElementSelectionValidator(acceptedClasses,true);
		ViewerFilter filter= new TypedViewerFilter(acceptedClasses) {
			@Override
			public boolean select(Viewer viewer,Object parentElement, Object element) {
				if(element instanceof IProject) {
					try {
						return ((IProject)element).hasNature(DSpotWizardConstants.MAVEN_NATURE);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
				if(element instanceof IJavaProject) {
					try {
						return ((IJavaProject)element).getProject().hasNature(DSpotWizardConstants.MAVEN_NATURE);
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
	             javaProject =  (IJavaProject) ob;
	             projectText.setText(javaProject.getElementName());}
	            }
	        }
		
	}

}
