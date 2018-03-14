package eu.stamp.wp4.dspot.wizard;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.Workbench;
import org.junit.Test;

@SuppressWarnings("restriction")
/**
 *  This class contains the information about the project that the other classes need
 *  This information is obtained and kept when the object of this class is generated with new
 *  The wizard generates only one object of this class when it is called, (at the begin of the method execute in the handler)
 *   and the WizardConf object is given to the other objects : Wizard and Wizard pages in their constructors
 */
public class WizardConf {
	
	
	private IJavaProject jproject;  // the project to test
	private String projectPath;
	private IWorkbenchWindow activeWindow;
	private String[] sources;    // the files with code in the project to test
	private boolean[] isTest;   // true for that source files containing @Test
	private ArrayList<String> testCases = new ArrayList<String>(1);
	private ArrayList<String> testMethods = new ArrayList<String>(1);
	
	public WizardConf(){
		
		 jproject = obtainProject();  // obtain the project
		// obtain project's path
        IProject project = jproject.getProject(); // Convert to project
        IPath pa = project.getLocation();         // get it's absolute path
	    projectPath = pa.toString();      // put it into a string
	    
	    sources = findSour();  // obtain the sources
	    
	    isTest = findTest(); // obtain the boolean array (value true for the sources that contain @Test)
	    
	} 
	
	// getter methods
	
	public IJavaProject getPro() {
		return jproject;
	}
	
	public String getProjectPath() {
		return projectPath;
	}
	
	public String[] getSources() {
		return sources;
	}
	
	public boolean[] getIsTest() {
		return isTest;
	}
	
	public String[] getTestCases() {
		return testCases.toArray(new String[testCases.size()]);
	}
	
	public String[] getTestMethods() {
		return testMethods.toArray(new String[testMethods.size()]);
	}
	
	public IWorkbenchWindow getTheWindow() {
		return activeWindow;
	}
	
	public boolean lookForTest(String qname) {
		try {
			return analisis(qname,getProjectClassLoader(jproject));
		} catch (MalformedURLException | CoreException e) {
			e.printStackTrace(); return false;
		}
	}
	
   public String getqName(String partOfTheName) throws JavaModelException {
		IPackageFragment[] packs = jproject.getPackageFragments(); // An array with the packages in the project
		IPackageFragment p;
		for (int i = 0; i < packs.length; i++) { // we look the compilation units in each package
			p = packs[i];
			IJavaElement[] Com = p.getCompilationUnits(); // the array with the compilation units in the package p
			String pName = p.getElementName(); // name = Package.Class
			for (IJavaElement myJ : Com) { // we look all the sources in the package p
				String TheSource = myJ.getElementName();
				if(partOfTheName.contains(TheSource) || TheSource.contains(partOfTheName)) { // if true this is the class we are looking for
				int In = TheSource.indexOf('.'); // we remove the .Java
				if (In > 0) {
					TheSource = TheSource.substring(0, In);
				}
				String qname = pName + "." + TheSource;
				return qname; }
   }}
		return null;
   }
	
	/*
	 *  The following private methods are only called by the constructor to obtain all the information
	 */
	
	/**
	 * this method finds the selected project
	 * @return The IJavaProject object describing the selected project
	 */
    private IJavaProject obtainProject() {

        activeWindow =  Workbench.getInstance().getActiveWorkbenchWindow();
		ISelection selection = activeWindow.getSelectionService().getSelection();
        IJavaProject jproject = null;  
      
        if(selection instanceof IStructuredSelection) {    
            Object element = ((IStructuredSelection)selection).getFirstElement();    

             if (element instanceof IJavaElement) {    
                jproject= ((IJavaElement)element).getJavaProject();
                  return jproject;
           }  
             if(element instanceof IProject) {
            
                IProject pro = (IProject)element;
                jproject = new JavaProject(pro,null);
                 return jproject;
                 
            	 }
        }  
        return null;
      } 
    
    /**
     * this method finds the source folders
     * @return an String array with the paths of the source files (relatives to the project)
     */ 
    private String[] findSour() {
  	  
  	  ArrayList<String> MyS = new ArrayList<String>(1);
  	  IPackageFragmentRoot[] packs;
		try {
			packs = jproject.getAllPackageFragmentRoots();
  	  for(IPackageFragmentRoot p : packs) {
  		  if(p.getKind() == IPackageFragmentRoot.K_SOURCE && p.hasChildren()) {   			  
  			  boolean isComp = false;
  			  for(IJavaElement jE: getFinalChildren(p)) {  // without this, source folders without compilation units would generate an ArrayOutofBounds error when comparing				                                    
  				 if(jE.getElementType() == IJavaElement.COMPILATION_UNIT) {isComp = true; break; } // the string array sour to the boolean array isTest in Dspa1 line 81
  			  } // end of the for
  			  if(isComp) {
  			  MyS.add(p.getPath().makeRelativeTo(jproject.getPath()).toString());}
  		  }
  	  }  // end of the for
	  } catch (JavaModelException e) {
			e.printStackTrace();
		}
		  return MyS.toArray(new String[MyS.size()]);
  	  
    } 
    
    
    /**
     *  this method obtains the list of the files (not folders) in a folder
     *  these files may will be into a system of sub-folders/packages
     *  @param IParent p, a folder containing a system of files and folders
     *  @return an array with all the final files without taking into consideration the sub-folder/s they belong to                                
     */
    private  IJavaElement[] getFinalChildren(IParent p) {      	
	  ArrayList <IJavaElement> myJEList  = new ArrayList<IJavaElement>(1); 
	try {
		IJavaElement[] ProvisionalArray = p.getChildren();
		for(IJavaElement jE: ProvisionalArray) {  
			if(jE.getElementType() == IJavaElement.PACKAGE_FRAGMENT || jE.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT) {  // is not a final children
				IJavaElement[] ProvisionalArray2 = getFinalChildren((IParent)jE);
				for(int i = 0; i < ProvisionalArray2.length; i++) {
					myJEList.add(ProvisionalArray2[i]);
				}
			} else { myJEList.add(jE); }  // is a final children
		}
	} catch (JavaModelException e) {
		e.printStackTrace();
	}
	  
	return myJEList.toArray(new IJavaElement[myJEList.size()]); 
	  
  }
    
    /**
     * this method looks in the sources looking for the Test annotation
     * @return a boolean array saying the source folders that contains test classes
     * @throws JavaModelException, MalformedURLException, CoreException
     */
	private boolean[] findTest(){
		
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
					if(allowed) {
					testList.add(new Boolean(HasTest));
					allowed = false;
					}
				} // end of the compilation units for

			} 
			
						
			try {
				classLoader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}    // close the class Loader
			
						
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
	 * @param jproject
	 * @return the URLClassLoader of the given project
	 * @throws MalformedURLException, CoreException
	 */
	private URLClassLoader getProjectClassLoader(IJavaProject jproject)
			throws CoreException, MalformedURLException {
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
	 * @param qname : qualified name of the java class to inspect
	 * @param cl : classLoader of the class project
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
			//cl.close();
			return count;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * isAnnotationPresent
	 * @param m : Method to inspect
	 * @param annotation : annotation we are looking for
	 * @return boolean, true if the annotation is present
	 */
	private boolean isAnnotationPresent(Method m, Class <? extends Annotation> annotation) {
		// customized isAnnotation present to detect correctly @Test
		boolean result = false;
		Annotation[] annot = m.getDeclaredAnnotations();
		for (Annotation a: annot) {
			if ( a.annotationType().getName().equals(annotation.getName())) {
				result = true; break;
			}
		}
		
		return result;
	}
}   

