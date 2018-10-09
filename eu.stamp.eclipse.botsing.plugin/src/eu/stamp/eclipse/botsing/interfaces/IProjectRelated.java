package eu.stamp.eclipse.botsing.interfaces;

import org.eclipse.jdt.core.IJavaProject;

/**
 * Objects affected by a project change implement this interface
 */
public interface IProjectRelated {
    /**
     * this method is responsible for updating the object when
     * the project changes
     * 
     * @param newProject, the new project
     */
	public void projectChanged (IJavaProject newProject);
}
