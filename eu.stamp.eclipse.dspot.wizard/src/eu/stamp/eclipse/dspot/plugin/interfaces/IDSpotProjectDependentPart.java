package eu.stamp.eclipse.dspot.plugin.interfaces;

import org.eclipse.jdt.core.IJavaProject;

public interface IDSpotProjectDependentPart  extends IDSpotElement{

	public void loadProject(IJavaProject project);
}
