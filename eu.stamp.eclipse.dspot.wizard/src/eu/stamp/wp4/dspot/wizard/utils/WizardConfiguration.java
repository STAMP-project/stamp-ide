/*******************************************************************************
 * Copyright (c) 2018 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ricardo Jose Tejada Garcia (Atos) - main developer
 * Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.wp4.dspot.wizard.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;

import eu.stamp.eclipse.dspot.plugin.context.DSpotContext;
import eu.stamp.wp4.dspot.execution.launch.DSpotProperties;

@SuppressWarnings("restriction")
/**
 * This class contains the information about the project that the other classes
 * need This information is obtained and kept when the object of this class is
 * generated with new The wizard generates only one object of this class when it
 * is called, (at the begin of the method execute in the handler) and the
 * WizardConf object is given to the other objects : Wizard and Wizard pages in
 * their constructors
 */
public class WizardConfiguration {
	
private DSpotContext dspotContext;

private IJavaProject jproject; // the project to test 

private IWorkbenchWindow activeWindow;

private boolean canContinue;

private List<ILaunchConfiguration> configurations;
private int indexOfCurrentConfiguration = 0;

private DSpotMemory dSpotMemory = new DSpotMemory(getSeparator());

public WizardConfiguration() throws CoreException{
canContinue = false;
 jproject = obtainProject();  // obtain the project
         create(jproject);
}

public WizardConfiguration(IJavaProject jproject) throws CoreException {
 canContinue = false;
         create(jproject);
}
private void create(IJavaProject jproject) throws CoreException {

this.jproject = jproject;

 Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
 
if (jproject == null) { 
MessageDialog.openInformation(
shell,
 "Execute DSpot",
 "Please, select a Java Project in the Package Explorer");
  return;
} else if(!jproject.getProject().hasNature("org.eclipse.m2e.core.maven2Nature")){ 
 MessageDialog.openError(
shell,
 "Execute DSpot",
 "The selected project must be a maven project");
 
 return;
} 
    canContinue = true;
    if(dspotContext == null) dspotContext = new DSpotContext(jproject);
    else dspotContext.loadProject(jproject);
    this.configurations = obtainLaunchConfigurations();
//}
}
// getter methods

public DSpotContext getContext() { return dspotContext; }

public boolean getCanContinue() { return canContinue; }

public boolean projectSelected() { return canContinue; } // TODO duplicity

public void setDSpotMemory(DSpotMemory dSpotMemory) { 
	this.dSpotMemory = dSpotMemory; }

public DSpotMemory getDSpotMemory() { return dSpotMemory; }
/**
 * @return the selected project
 */
public IJavaProject getPro() {
return jproject;
}

/**
 * @return the absolute path of the selected project
 */
public String getProjectPath() {
return dspotContext.getProject().getProject().getLocation().toString();
}

/**
 * @return a string array containing string of the form : <name of the test
 *         class>/<name of the annotated test method>
 */
public String[] getTestCases() {
	List<String> list = dspotContext.getTestMethods();
return list.toArray(new String[list.size()]);
}

/**
 * @return a string array containing the names of the test annotated methods
 */
public String[] getTestMethods() {
	List<String> list = dspotContext.getTestMethods();
	List<String> list2 = new LinkedList<String>();
	for(String sr : list)if(sr.contains("/")) {
		String[] strings = sr.split("/");
		list2.add(strings[1]);
	}		
return list2.toArray(new String[list2.size()]);
}

/**
 * @return the active workbench window
 */
public IWorkbenchWindow getTheWindow() {
if(activeWindow != null) return activeWindow;
activeWindow = Workbench.getInstance().getActiveWorkbenchWindow();
return activeWindow;
}

/**
 * @return an array with the DSpot launch configurations
 */
public List<ILaunchConfiguration> getLaunchConfigurations() {
return configurations;
}

/**
 * @return ":" in Mac,Linux or others, ";" in windows
 */
public static String getSeparator() {
String os = System.getProperty("os.name").toLowerCase();
if (os.contains("win") || os.contains("Win")) {
return ";";
}
return ":";
}

/**
 * @return the ILaunchConfiguration to use
 */
public ILaunchConfiguration getCurrentConfiguration() {
ILaunchConfiguration confi = configurations.get(indexOfCurrentConfiguration);
return confi;
}

/**
 * @param indexOfCurrentConfiguration
 *            the position in the array given by getLaunchConfigurations of the
 *            launch configuration to be used (default 0)
 */
public void setIndexOfCurrentConfiguration(int indexOfCurrentConfiguration) {
this.indexOfCurrentConfiguration = indexOfCurrentConfiguration;
}
/*
 * The following private methods are only called by the constructor to obtain
 * all the information
 */

/**
 * this method finds the selected project
 * 
 * @return The IJavaProject object describing the selected project
 */
private IJavaProject obtainProject() {

activeWindow = Workbench.getInstance().getActiveWorkbenchWindow();

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

/**
 * @return an array with DSpot launchConfigurations
 * @throws CoreException
 */
private List<ILaunchConfiguration> obtainLaunchConfigurations() throws CoreException {
List<ILaunchConfiguration> result = new ArrayList<>();
ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
ILaunchConfiguration[] confs = manager.getLaunchConfigurations(manager.getLaunchConfigurationType(DSpotProperties.LAUNCH_CONF_ID));
for (ILaunchConfiguration conf: confs) {
result.add(conf.getWorkingCopy());
}

return result;
}

}
