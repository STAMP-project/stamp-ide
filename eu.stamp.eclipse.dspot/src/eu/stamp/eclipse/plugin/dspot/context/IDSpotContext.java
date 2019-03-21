package eu.stamp.eclipse.plugin.dspot.context;

import java.io.File;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;

public interface IDSpotContext {

	public IJavaProject getProject();
	
	public String[] getNoTestSourceFolders();
	
	public String[] getTestSourceFolders();
	
	public List<File> getTestFiles();
	
	public String getFullName(String partialName);
	
	public String[] getTestMethods();
	
	public void loadProject(IJavaProject project);
}
