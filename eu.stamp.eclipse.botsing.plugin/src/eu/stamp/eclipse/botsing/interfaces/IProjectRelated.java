package eu.stamp.eclipse.botsing.interfaces;

import org.eclipse.jdt.core.IJavaProject;

public interface IProjectRelated {

	public void projectChanged (IJavaProject newProject);
}
