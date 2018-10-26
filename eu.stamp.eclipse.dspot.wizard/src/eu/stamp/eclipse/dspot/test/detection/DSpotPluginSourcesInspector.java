package eu.stamp.eclipse.dspot.test.detection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaRuntime;

import org.junit.Test;

public class DSpotPluginSourcesInspector {

	private URLClassLoader loader;
	
	public DSpotPluginSourcesInspector(IJavaProject jproject) {	
		
		try {
			getUrlsAndLoader(jproject);

		} catch (MalformedURLException | CoreException e) {
			e.printStackTrace();
		}		
	}
	
	public boolean isTest(String qName) {
		try {
			Class<?> clazz = loader.loadClass(qName);
			Method[] methods = clazz.getDeclaredMethods();
			for(Method method : methods) {
				if(isAnnotationPresent(method,Test.class)) return true;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false; // TODO
	}
	

	private void getUrlsAndLoader(IJavaProject jproject) throws CoreException, MalformedURLException {
		// Retrieve the class loader of the Java Project
		String[] classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(jproject);
		
		LinkedList<URL> urlList = new LinkedList<URL>();
		for (int i = 0; i < classPathEntries.length; i++) {
		String entry = classPathEntries[i];
		IPath path = new Path(entry);
		URL url = path.toFile().toURI().toURL();
		urlList.add(url);
		}
		// this class loader loads the classes to inspect looking for @Test
		ClassLoader parentClassLoader = jproject.getClass().getClassLoader();
		URL[] urls = (URL[]) urlList.toArray(new URL[urlList.size()]);
		loader = new URLClassLoader(urls, parentClassLoader);
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
