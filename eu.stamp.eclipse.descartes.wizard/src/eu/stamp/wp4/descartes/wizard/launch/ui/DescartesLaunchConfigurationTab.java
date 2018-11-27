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
package eu.stamp.wp4.descartes.wizard.launch.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationDialog;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SegmentEvent;
import org.eclipse.swt.events.SegmentListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import eu.stamp.wp4.descartes.wizard.utils.DescartesWizardConstants;

@SuppressWarnings("restriction")
/**
 *  this class describes the tab to create, modify and run Descartes Configurations 
 *  from the Eclipse "Run configuration"
 */
public class DescartesLaunchConfigurationTab extends AbstractLaunchConfigurationTab {
	
	private Text projectText;
	private Text pomText; // in this case we only can specify the file, to modify it use the wizard
	private boolean dirtyTexts = false;

	@Override
	public void createControl(Composite parent) {
        
		Composite container = new Group(parent,SWT.BORDER);
		setControl(container);
		
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(container);
		
		/*
		 *   ROW 1 : project
		 */
		Label projectLabel = new Label(container,SWT.NONE);
		projectLabel.setText("Project : ");
		GridDataFactory.swtDefaults().applyTo(projectLabel);
		
		projectText = new Text(container,SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(projectText);
		
		Button projectButton = new Button(container,SWT.PUSH); // a button to select a project
		projectButton.setText("Select project");
		GridDataFactory.fillDefaults().applyTo(projectButton);
		
		/*
		 *   ROW 2 : pom.xml
		 */
		Label pomLabel = new Label(container,SWT.NONE); 
		pomLabel.setText("Name of the pom file : ");
		GridDataFactory.swtDefaults().applyTo(pomLabel);
		
		pomText = new Text(container,SWT.BORDER);   // the pom to execute Descartes
		GridDataFactory.fillDefaults().grab(true, false).applyTo(pomText);
		
		Button pomButton = new Button(container,SWT.PUSH);
		pomButton.setText("Select pom file");	
		GridDataFactory.fillDefaults().applyTo(pomButton);
		
		// file dialog to select the pom file
		FileDialog fileDiag = new FileDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		fileDiag.setText("select a POM file");
        fileDiag.setFilterExtensions(new String[] {"*.xml*"});
		
		
		// listeners
		projectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showProjectDialog();
			}
		});
		pomButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String myPath = projectText.getText(); // open the file dialog 
				fileDiag.setFilterPath(myPath);       // in the project folder
				String pomFile = fileDiag.open(); // .open() will return the pom's path
				if(pomFile != null) if(!pomFile.isEmpty()) {
					if(pomFile.contains("/"))pomFile = pomFile      // take the name
							.substring(pomFile.lastIndexOf("/")+1); // of the pom file
					if(pomFile.contains("\\"))pomFile = pomFile     // from the path
							.substring(pomFile.lastIndexOf("\\")+1);
					pomText.setText(pomFile);
				}
			}
		});	
		
		projectText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				LaunchConfigurationDialog                   // enable the buttons
				.getCurrentlyVisibleLaunchConfigurationDialog().updateButtons();
				dirtyTexts = true;
			}	
		});
        projectText.addSegmentListener(new SegmentListener() { // segment listener if the user copy-pastes
			@Override
			public void getSegments(SegmentEvent event) {
				LaunchConfigurationDialog
				.getCurrentlyVisibleLaunchConfigurationDialog().updateButtons();
				dirtyTexts = true;
			}	
        });
        pomText.addKeyListener(new KeyListener() { 
        	@Override
        	public void keyPressed(KeyEvent e) {}
        	@Override
        	public void keyReleased(KeyEvent e) {
				LaunchConfigurationDialog
				.getCurrentlyVisibleLaunchConfigurationDialog().updateButtons();
				dirtyTexts = true;
        	}
        });
       pomText.addSegmentListener(new SegmentListener() { // if the user copy-pastes
    	   @Override
    	   public void getSegments(SegmentEvent e) {
				LaunchConfigurationDialog
				.getCurrentlyVisibleLaunchConfigurationDialog().updateButtons();
				dirtyTexts = true;
    	   }
       }); 
	}
	

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// there is not a default configuration
		}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {  
			projectText.setText(configuration.getAttribute(MavenLaunchConstants.ATTR_POM_DIR,""));
			String pomName = "pom.xml";  // default pom file
			// theGoals : string with the maven command to invoke Descartes used by this configuration
			String theGoals = configuration.getAttribute(MavenLaunchConstants.ATTR_GOALS, "");
			// if the goals define the pom file to use
			if(theGoals.contains("-f ")) pomName = theGoals.substring(theGoals.indexOf("-f ")+3);
			pomText.setText(pomName);  // 
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
	   
		configuration.setAttribute(MavenLaunchConstants.PLUGIN_ID,
				DescartesWizardConstants.DESCARTES_PLUGIN_ID);
		configuration.setAttribute(MavenLaunchConstants.ATTR_GOALS,
				"clean package org.pitest:pitest-maven:mutationCoverage -f " + pomText.getText());
		configuration.setAttribute(MavenLaunchConstants.ATTR_POM_DIR, projectText.getText());
	}
	
    @Override
    public boolean canSave() {
    	if(!projectText.isDisposed()) return !projectText.getText().isEmpty() 
    			&& !pomText.getText().isEmpty();
    	return true;
    }
    @Override
    public boolean isDirty() {
    	return dirtyTexts;
    }

	@Override
	public String getName() {
		return "Descartes launch configuration tab";
	}
	
	/**
	 * this method creates and opens the project selection dialog and handles the user selection
	 */
	private void showProjectDialog() {
		
		// show only projects
		Class<?>[] acceptedClasses = new Class[] {IJavaProject.class,IProject.class};
		TypedElementSelectionValidator validator = new TypedElementSelectionValidator(acceptedClasses,true);
		ViewerFilter filter= new TypedViewerFilter(acceptedClasses) {
			@Override
			public boolean select(Viewer viewer,Object parentElement, Object element) {
				if(element instanceof IProject) {
					try {
						return ((IProject)element).hasNature(DescartesWizardConstants.MAVEN_NATURE_ID);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
				if(element instanceof IJavaProject) {
					try {
						return ((IJavaProject)element).getProject().hasNature(DescartesWizardConstants.MAVEN_NATURE_ID);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
				return false;
			}
		};	
		
		  IWorkspaceRoot fWorkspaceRoot= ResourcesPlugin.getWorkspace().getRoot();
	        
		  // set the dialog characteristics
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
	        
            // after pressing ok returns the selected project's path to set it into the project text
	        if(dialog.open() == Window.OK) {
	            Object[] results = dialog.getResult();
	            for(Object ob : results) {
	            	if(ob instanceof IJavaProject) { 
	             IJavaProject javaProject;
	             javaProject =  (IJavaProject) ob;
	             projectText.setText(javaProject.getProject().getLocation().toString());}
	            }
	        }
		
	}

}
