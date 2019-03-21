package eu.stamp.eclipse.plugin.dspot.context;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaRuntime;
import org.junit.Test;

public class DSpotCompiledFileInfo {
	
	private boolean isTest;
	
	private List<String> testNames;
	
	private boolean ok;
	
	public DSpotCompiledFileInfo(IJavaProject project,String fullName) {
		
		testNames = new LinkedList<String>();
		try {
			URLClassLoader loader = getProjectClassLoader(project);
			try {
			isTest = analisis(fullName,loader);
			ok = true;
			}catch(NoClassDefFoundError e) { ok = false;}
			loader.close();
		} catch (CoreException | IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public boolean itsOk() { return ok; }
	
	public boolean containsTests() { return isTest; }
	
	public List<String> getTestMethods() { return testNames; }

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
	 * @throws ClassNotFoundException 
	 */
	private boolean analisis(String qname, URLClassLoader cl) throws ClassNotFoundException {
		// name : Package.Class path : path of the .class file
		    Class<?> cls = cl.loadClass(qname); // loading the name class
			Method[] methods = cls.getDeclaredMethods(); // Array with the methods in the class
			boolean count = false; // Has @Test
			for (int i = 0; i < methods.length; i++) { // looking in all the methods

				if (isAnnotationPresent(methods[i], Test.class)) {
					count = true;
                    testNames.add(methods[i].getName());
				}
			} // end of the i for
				// cl.close();
			return count;
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

}
