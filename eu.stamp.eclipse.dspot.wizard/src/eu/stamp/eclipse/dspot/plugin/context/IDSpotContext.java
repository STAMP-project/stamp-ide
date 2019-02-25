package eu.stamp.eclipse.dspot.plugin.context;

import java.io.File;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;

public interface IDSpotContext {

	public IJavaProject getProject();
	
	public List<String> getNoTestSourceFolders();
	
	public List<String> getTestSourceFolders();
	
	public List<File> getTestFiles();
	
	public String getFullName(String partialName);
	
	public List<String> getTestMethods();
	
	public void loadProject(IJavaProject project);
}
