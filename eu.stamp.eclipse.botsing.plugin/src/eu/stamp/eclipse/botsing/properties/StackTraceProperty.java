package eu.stamp.eclipse.botsing.properties;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
//import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

import eu.stamp.eclipse.botsing.interfaces.IProjectRelated;

public class StackTraceProperty 
             extends BotsingExplorerField implements IProjectRelated {

    private String filterPath;
    
    private FileDialog dialog;
	
	public StackTraceProperty(String defaultValue, String key, String name) {
		super(defaultValue, key, name);
	}
	@Override
	protected String openExplorer() {
		 
		 dialog = new FileDialog(
				 PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				 .getShell(),SWT.OK | SWT.MULTI);
		 dialog.setFilterExtensions(new String[] {"*.log"});
		 dialog.setText("Select log file");
		 if(filterPath != null)if(!filterPath.isEmpty())
		 dialog.setFilterPath(filterPath);
		 
	    /* final DirectoryDialog dialog = new DirectoryDialog(
	      PlatformUI.getWorkbench().getActiveWorkbenchWindow()
	      .getShell(),SWT.OK);
	     dialog.setText("Select a folder");*/
	     
	      String result = dialog.open();
	      return result;
	}
	@Override
	public void projectChanged(IJavaProject newProject) {
		filterPath = newProject.getProject().getLocation().toString();
		if(dialog != null)dialog.setFilterPath(filterPath);
	}
}