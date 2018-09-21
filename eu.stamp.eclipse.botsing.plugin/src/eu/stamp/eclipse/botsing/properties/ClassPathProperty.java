package eu.stamp.eclipse.botsing.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import eu.stamp.eclipse.botsing.wizard.BotsingWizardPage;

@SuppressWarnings("restriction")
public class ClassPathProperty extends BotsingExplorerField {

	private final String separator;
	private final String pathSeparator;
	
	private final BotsingWizardPage wizardPage;
	
	public ClassPathProperty(String defaultValue, 
			String key, String name,BotsingWizardPage wizardPage) {
		super(defaultValue, key, name);
		if(System.getProperty("os.name").contains("indows")) {
			separator = ";";
			pathSeparator = "\\";
		}
		else{
			separator = ":";
			pathSeparator = "/";
		}
		this.wizardPage = wizardPage;
	}
	@Override
	protected String openExplorer() {
		
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
		}
		return "";
	}
	
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
	}
	@Override
	public String[] getPropertyString() { 
		return new String[] {key, data};
		}
}
