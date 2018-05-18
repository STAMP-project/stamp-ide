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
package eu.stamp.wp4.dspot.wizard.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.swt.widgets.Shell;

import org.junit.Test;

import eu.stamp.wp4.dspot.constants.DSpotWizardConstants;
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

	private IJavaProject jproject; // the project to test
	private String projectPath;
	private IWorkbenchWindow activeWindow;
	private String[] sources; // the files with code in the project to test
	private boolean[] isTest; // true for that source files containing @Test
	private ArrayList<String> testCases = new ArrayList<String>(1);
	private ArrayList<String> testMethods = new ArrayList<String>(1);
	private boolean projectSelected = false;
	private List<ILaunchConfiguration> configurations;
	private int indexOfCurrentConfiguration = 0;
	
	
	private DSpotMemory dSpotMemory = new DSpotMemory(getSeparator());

	
	public WizardConfiguration() throws CoreException{
		
		 jproject = obtainProject();  // obtain the project
		 Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		 
			if (jproject == null) {
				 MessageDialog.openInformation(
				shell,
				 "Execute DSpot",
				 "Please, select a Java Project in the Package Explorer");
			} else if(!jproject.getProject().hasNature("org.eclipse.m2e.core.maven2Nature")){ 
				 MessageDialog.openError(
				shell,
				 "Execute DSpot",
				 "The selected project must be a maven project");
			}else {
		 projectSelected = true;
	
		// obtain project's path
        IProject project = jproject.getProject(); // Convert to project
        IPath pa = project.getLocation();         // get it's absolute path
	    projectPath = pa.toString();      // put it into a string
	    
	    sources = findSour();  // obtain the sources
	    
	    isTest = findTest(); // obtain the boolean array (value true for the sources that contain @Test)
	    
	    this.configurations = obtainLaunchConfigurations();
			}
	} 
	
	public WizardConfiguration(IJavaProject jPro) throws CoreException {
		
		 jproject = jPro;  // obtain the project
		 Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		 
			if (jproject == null) {
				 MessageDialog.openInformation(
				shell,
				 "Execute DSpot",
				 "Please, select a Java Project in the Package Explorer");
			} else if(!jproject.getProject().hasNature("org.eclipse.m2e.core.maven2Nature")){ 
				 MessageDialog.openError(
				shell,
				 "Execute DSpot",
				 "The selected project must be a maven project");
			}else {
		 projectSelected = true;
	
		// obtain project's path
       IProject project = jproject.getProject(); // Convert to project
       IPath pa = project.getLocation();         // get it's absolute path
	    projectPath = pa.toString();      // put it into a string
	    
	    sources = findSour();  // obtain the sources
	    
	    isTest = findTest(); // obtain the boolean array (value true for the sources that contain @Test)
	    
	    this.configurations = obtainLaunchConfigurations();
			}
	}
	// getter methods

	public boolean projectSelected() {
		return projectSelected;
	}

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
		return projectPath;
	}

	/**
	 * @return a String array containing the names of the source files in the
	 *         project
	 */
	public String[] getSources() {
		return sources;
	}

	/**
	 * @return a boolean array containing true in the positions of the test classes
	 *         of the array obtain with getSources()
	 */
	public boolean[] getIsTest() {
		return isTest;
	}

	/**
	 * @return a string array containing string of the form : <name of the test
	 *         class>/<name of the annotated test method>
	 */
	public String[] getTestCases() {
		return testCases.toArray(new String[testCases.size()]);
	}

	/**
	 * @return a string array containing the names of the test annotated methods
	 */
	public String[] getTestMethods() {
		return testMethods.toArray(new String[testMethods.size()]);
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
	 * @param qname
	 *            : complete qualified name of a class
	 * @return true if the class contains a test annotation
	 */
	public boolean lookForTest(String qname) {
		try {
			return analisis(qname, getProjectClassLoader(jproject));
		} catch (MalformedURLException | CoreException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param partOfTheName
	 *            : a fragment of the qualified name of a class in the selected
	 *            project
	 * @return the complete qualified name of the class in the selected project
	 *         whose name contains the given string
	 * @throws JavaModelException
	 */
	public String getqName(String partOfTheName) throws JavaModelException {
		IPackageFragment[] packs = jproject.getPackageFragments(); // An array with the packages in the project
		IPackageFragment p;
		for (int i = 0; i < packs.length; i++) { // we look the compilation units in each package
			p = packs[i];
			IJavaElement[] Com = p.getCompilationUnits(); // the array with the compilation units in the package p
			String pName = p.getElementName(); // name = Package.Class
			for (IJavaElement myJ : Com) { // we look all the sources in the package p
				String TheSource = myJ.getElementName();
				if (partOfTheName.contains(TheSource) || TheSource.contains(partOfTheName)) { // if true this is the
																								// class we are looking
																								// for
					int In = TheSource.indexOf('.'); // we remove the .Java
					if (In > 0) {
						TheSource = TheSource.substring(0, In);
					}
					String qname = pName + "." + TheSource;
					return qname;
				}
			}
		}
		return null;
	}

	/**
	 * @return the ILaunchConfiguration to use
	 */
	public ILaunchConfiguration getCurrentConfiguration() {
		return configurations.get(indexOfCurrentConfiguration);
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
	 * this method finds the source folders
	 * 
	 * @return an String array with the paths of the source files (relatives to the
	 *         project)
	 */
	private String[] findSour() {

		ArrayList<String> MyS = new ArrayList<String>(1);
		IPackageFragmentRoot[] packs;
		try {
			packs = jproject.getAllPackageFragmentRoots();
			for (IPackageFragmentRoot p : packs) {
				if (p.getKind() == IPackageFragmentRoot.K_SOURCE && p.hasChildren()) {
					boolean isComp = false;
					for (IJavaElement jE : getFinalChildren(p)) { // without this, source folders without compilation
																	// units would generate an ArrayOutofBounds error
																	// when comparing
						if (jE.getElementType() == IJavaElement.COMPILATION_UNIT) {
							isComp = true;
							break;
						} // the string array sour to the boolean array isTest in Dspa1 line 81
					} // end of the for
					if (isComp) {
						MyS.add(p.getPath().makeRelativeTo(jproject.getPath()).toString());
					}
				}
			} // end of the for
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return MyS.toArray(new String[MyS.size()]);

	}

	/**
	 * this method obtains the list of the files (not folders) in a folder these
	 * files may will be into a system of sub-folders/packages
	 * 
	 * @param IParent
	 *            p, a folder containing a system of files and folders
	 * @return an array with all the final files without taking into consideration
	 *         the sub-folder/s they belong to
	 */
	public IJavaElement[] getFinalChildren(IParent p) {
		ArrayList<IJavaElement> myJEList = new ArrayList<IJavaElement>(1);
		try {
			IJavaElement[] ProvisionalArray = p.getChildren();
			for (IJavaElement jE : ProvisionalArray) {
				if (jE.getElementType() == IJavaElement.PACKAGE_FRAGMENT
						|| jE.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT) { // is not a final children
					IJavaElement[] ProvisionalArray2 = getFinalChildren((IParent) jE);
					for (int i = 0; i < ProvisionalArray2.length; i++) {
						myJEList.add(ProvisionalArray2[i]);
					}
				} else {
					myJEList.add(jE);
				} // is a final children
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		return myJEList.toArray(new IJavaElement[myJEList.size()]);

	}
	
	public DSpotMemory getDSpotMemory() {
		return dSpotMemory;
	}
	
	public void setDSpotMemory(DSpotMemory dSpotMemory) {
		this.dSpotMemory = dSpotMemory;
	}

	/**
	 * this method looks in the sources looking for the Test annotation
	 * 
	 * @return a boolean array saying the source folders that contains test classes
	 * @throws JavaModelException,
	 *             MalformedURLException, CoreException
	 */
	private boolean[] findTest() {

		// @Test sources
		ArrayList<Boolean> testList = new ArrayList<Boolean>(1);

		try {
			URLClassLoader classLoader = getProjectClassLoader(jproject);

			// the path of the folder with the .class files
			IPackageFragment[] packs = jproject.getPackageFragments(); // An array with the packages in the project

			IPackageFragment p;
			for (int i = 0; i < packs.length; i++) { // we look the compilation units in each package
				p = packs[i];
				boolean HasTest = false; // if a compilation unit in this package has @Test this will become true
				boolean allowed = true;
				IJavaElement[] Com = p.getCompilationUnits(); // the array with the compilation units in the package p
				String pName = p.getElementName(); // name = Package.Class
				for (IJavaElement myJ : Com) { // we look all the sources in the package p
					String TheSource = myJ.getElementName();
					int In = TheSource.indexOf('.'); // we remove the .Java
					if (In > 0) {
						TheSource = TheSource.substring(0, In);
					}
					String qname = pName + "." + TheSource;
					HasTest = analisis(qname, classLoader); // calling analysis to know if this source has the @Test
					if (allowed) {
						testList.add(new Boolean(HasTest));
						allowed = false;
					}
				} // end of the compilation units for

			}

			try {
				classLoader.close();
			} catch (IOException e) {
				e.printStackTrace();
			} // close the class Loader

		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		boolean[] isTest = new boolean[testList.size()];
		for (int i = 0; i < testList.size(); i++) {
			isTest[i] = testList.get(i).booleanValue();
		}
		return isTest;
	}

	/**
	 * getProjectClassLoader
	 * 
	 * @param jproject
	 * @return the URLClassLoader of the given project
	 * @throws MalformedURLException,
	 *             CoreException
	 */
	private URLClassLoader getProjectClassLoader(IJavaProject jproject) throws CoreException, MalformedURLException {
		// Retrieve the class loader of the Java Project
		String[] classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(jproject);
		List<URL> urlList = new ArrayList<URL>();
		for (int i = 0; i < classPathEntries.length; i++) {
			String entry = classPathEntries[i];
			IPath path = new Path(entry);
			URL url = path.toFile().toURI().toURL();
			urlList.add(url);
		}
		// this class loader loads the classes to inspect looking for @Test
		ClassLoader parentClassLoader = jproject.getClass().getClassLoader();
		URL[] urls = (URL[]) urlList.toArray(new URL[urlList.size()]);
		URLClassLoader classLoader = new URLClassLoader(urls, parentClassLoader);
		return classLoader;
	}

	/**
	 * this method looks for the Test annotation
	 * 
	 * @param qname
	 *            : qualified name of the java class to inspect
	 * @param cl
	 *            : classLoader of the class project
	 * @return boolean, true if the source has a Test annotation else false
	 */
	private boolean analisis(String qname, URLClassLoader cl) {
		// name : Package.Class path : path of the .class file
		try {
			Class<?> cls = cl.loadClass(qname); // loading the name class

			Method[] methods = cls.getDeclaredMethods(); // Array with the methods in the class
			boolean count = false; // Has @Test
			for (int i = 0; i < methods.length; i++) { // looking in all the methods

				if (isAnnotationPresent(methods[i], Test.class)) {
					count = true;
					this.testCases.add(qname + "/" + methods[i].getName());
					this.testMethods.add(methods[i].getName());
				}
			} // end of the i for
				// cl.close();
			return count;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * isAnnotationPresent
	 * 
	 * @param m
	 *            : Method to inspect
	 * @param annotation
	 *            : annotation we are looking for
	 * @return boolean, true if the annotation is present
	 */
	private boolean isAnnotationPresent(Method m, Class<? extends Annotation> annotation) {
		// customized isAnnotation present to detect correctly @Test
		Annotation[] annot = m.getDeclaredAnnotations();
		for (Annotation a : annot) {
			if (a.annotationType().getName().equals(annotation.getName())) {
				return true;
			}
		}

		return false;
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
