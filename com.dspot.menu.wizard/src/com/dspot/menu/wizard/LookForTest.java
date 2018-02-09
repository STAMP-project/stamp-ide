package com.dspot.menu.wizard;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;

import org.junit.Test;


public class LookForTest {

	public static boolean[] findTest(){ // This method returns a String
																					// Array with the packages of the
		 // @Test sources
		ArrayList<Boolean> testList = new ArrayList<Boolean>(1);
		IJavaProject jproject = MyHan.getPro(); // The Java Project
		
		try {
			URLClassLoader classLoader = getProjectClassLoader(jproject);
			
			// the path of the folder with the .class files
			IPackageFragment[] packs = jproject.getPackageFragments(); // An array with the packages in the project

			IPackageFragment p;
			for (int i = 0; i < packs.length; i++) { // we look the compilation units in each package
				p = packs[i];
				boolean HasTest = false; // if a compilation unit in this package has @Test this will become true
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
					testList.add(new Boolean(HasTest));
					break; // this package contains a compilation unit with @Test
				} // end of the compilation units for

			} // end of the packs for
			
						
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

	private static URLClassLoader getProjectClassLoader(IJavaProject jproject)
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
		ClassLoader parentClassLoader = jproject.getClass().getClassLoader();
		URL[] urls = (URL[]) urlList.toArray(new URL[urlList.size()]);
		URLClassLoader classLoader = new URLClassLoader(urls, parentClassLoader);
		return classLoader;
	}
	
	private static boolean analisis(String qname, URLClassLoader cl) {
		// this method returns true if the source has an @Test annotation
		// name : Package.Class path : path of the .class file
		try {
			Class<?> cls = cl.loadClass(qname); // loading the name class

			Method[] methods = cls.getDeclaredMethods(); // Array with the methods in the class
			boolean count = false; // Has @Test
			for (int i = 0; i < methods.length; i++) { // looking in all the methods

				//System.out.println("Method: " + methods[i].getName());
				//System.out.println("Test: " + isAnnotationPresent(methods[i], Test.class));
				//System.out.println("Deprecated: " + isAnnotationPresent(methods[i], Deprecated.class));
				//System.out.println("RequestMapping: " + isAnnotationPresent(methods[i], RequestMapping.class));

				if (isAnnotationPresent(methods[i], Test.class)) {
					count = true;
				}
			} // end of the i for
			//cl.close();
			return count;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isAnnotationPresent(Method m, Class <? extends Annotation> annotation) {
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
