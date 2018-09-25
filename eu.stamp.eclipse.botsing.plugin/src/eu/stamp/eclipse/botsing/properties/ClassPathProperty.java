package eu.stamp.eclipse.botsing.properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.PlatformUI;

import eu.stamp.botsing.Main;

public class ClassPathProperty extends BotsingExplorerField {
	
	private String folderPath;
	
	private final String folderKey;
	
	public ClassPathProperty(String defaultValue, 
			String key, String name) {
		super(defaultValue, key, name);
		folderKey = "folderKey";
	}
	@Override
	protected String openExplorer() {
		
		final DirectoryDialog dialog = new DirectoryDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell());
		
		folderPath = dialog.open();
		
		Main.bin_path = folderPath;
		return Main.getListOfDeps();
		
		/*
		IJavaProject project = showProjectDialog();
		if(project == null) return "";
		wizardPage.projectChanged(project);
		try {
			IClasspathEntry[] entries = project.getResolvedClasspath(true);
		    if(entries == null) return "";
		    if(entries.length < 1) return "";
			String result = entries[0].getPath().toString();
			String location = project.getProject().getLocation().toString();
			for(int i = 1; i < entries.length; i++)
		    	result += separator + location + pathSeparator +
		    	entries[i].getPath().toString();
			return result;
		} catch (JavaModelException e) {
			e.printStackTrace();
		}*/
	}
	/*
	private IJavaProject showProjectDialog() {
		Class<?>[] acceptedClasses = new Class[] {IJavaProject.class,IProject.class};
		TypedElementSelectionValidator validator = new TypedElementSelectionValidator(acceptedClasses,true);
		
		ViewerFilter filter= new TypedViewerFilter(acceptedClasses) {
			@Override
			public boolean select(Viewer viewer,Object parentElement, Object element) {
			return element instanceof IJavaProject ||
					element instanceof IProject;	
			}
		};
		
		    IWorkspaceRoot fWorkspaceRoot= ResourcesPlugin.getWorkspace().getRoot();
	        
	        StandardJavaElementContentProvider provider= new StandardJavaElementContentProvider();
	        ILabelProvider labelProvider= new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
	        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	        ElementTreeSelectionDialog dialog= new ElementTreeSelectionDialog(shell, labelProvider, provider);
	        dialog.setValidator(validator);
	        dialog.setComparator(new JavaElementComparator());
	        dialog.setTitle(" Select a project ");
	        dialog.setMessage(" Select a project ");
	        dialog.setInput(JavaCore.create(fWorkspaceRoot));
	        dialog.addFilter(filter);
	        dialog.setHelpAvailable(false);
		
	        if(dialog.open() == Window.OK) {
	            Object[] results = dialog.getResult();
	            for(Object ob : results) {
	            if(ob instanceof IJavaProject) { 
	            IJavaProject jProject = (IJavaProject)ob;
	            return jProject;
	             }
	            }
	        }     
		return null;
	}*/
	@Override
	public String[] getPropertyString() { 
		System.setProperty("user.dir",folderPath);
		return new String[] {key, data};
		}
	@Override
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy copy) {
		super.appendToConfiguration(copy);
		copy.setAttribute(folderKey,folderPath);
	}
	@Override
	public void load(ILaunchConfigurationWorkingCopy copy) {
		super.load(copy);
		try {
			folderPath = copy.getAttribute(folderKey, "");
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
