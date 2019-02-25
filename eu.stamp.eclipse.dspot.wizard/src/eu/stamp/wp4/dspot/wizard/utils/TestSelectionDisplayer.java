package eu.stamp.wp4.dspot.wizard.utils;

import java.io.File;
import java.util.LinkedList;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import eu.stamp.eclipse.dspot.plugin.context.DSpotContext;

@SuppressWarnings("restriction")
public class TestSelectionDisplayer {

	/**
	 * Method to create and show a dialog to select test classes
	 * @param jProject : the selected project
	 * @param window : the active workbench window
	 * @return a string with the class selected by the user
	 * @throws JavaModelException
	 */
   public static String showElementTreeSelectionDialog(IJavaProject jProject, IWorkbenchWindow window,
		WizardConfiguration wConf,LinkedList<Object> testSelection) throws JavaModelException {
	        Class<?>[] acceptedClasses= new Class[] { IPackageFragmentRoot.class, IJavaProject.class, IJavaElement.class };
			TypedElementSelectionValidator validator= new TypedElementSelectionValidator(acceptedClasses, true) {
	            @Override
	            public boolean isSelectedValid(Object element) { // this method is override to specify what objects can be selected
	                
	                    if (element instanceof ICompilationUnit || element instanceof IPackageFragment) {
	                        return true;
	                    }
	                    return false;
	            }
	        };

	        acceptedClasses= new Class[] { IJavaModel.class, IPackageFragmentRoot.class, IJavaProject.class, IJavaElement.class, ICompilationUnit.class };
	        ViewerFilter filter= new TypedViewerFilter(acceptedClasses) {
	            @Override
	            public boolean select(Viewer viewer, Object parent, Object element) {
	                if (element instanceof IJavaProject) {
	                    if (!((IJavaProject) element).getElementName().equals(jProject.getElementName())) {
	                        return false;
	                    }
	                }
	                if (element instanceof IPackageFragment) {
	                    try {
	                        return containsTestClasses ((IPackageFragment)element);
	                    } catch (JavaModelException e) {
	                        JavaPlugin.log(e.getStatus()); // just log, no UI in validation
	                        return false;
	                    }
	                }
	                if (element instanceof JarPackageFragmentRoot) {
	                    return false;
	                }
	                return super.select(viewer, parent, element);
	            }

	            private boolean containsTestClasses(IPackageFragment element) throws JavaModelException {
	                boolean result = false;
	                if (!(element instanceof JarPackageFragmentRoot)) {
	                    for (IJavaElement child:element.getChildren()) {
	                        if (child instanceof ICompilationUnit && isTestClass (child)) {
	                            result = true;
	                            break;
	                        }
	                    }
	                } 
	                return result;
	            }

	            private boolean isTestClass(IJavaElement child) {
	                String name = child.getElementName();
	                name = name.replaceAll(".java","").replaceAll(".class","");
	                java.util.List<File> files = 
	                		wConf.getContext().getTestFiles();
	                for(File file : files) {
	                	String filename = file.getName()
	                			.replaceAll(".java","").replaceAll(".class","");
	                	if(name.equalsIgnoreCase(filename))
	                		return true;
	                } 
	                return false;
	            }
	        };
	        
	        IWorkspaceRoot fWorkspaceRoot= ResourcesPlugin.getWorkspace().getRoot();
	        IJavaElement initElement = jProject.getPackageFragmentRoots()[0];
	        
	        StandardJavaElementContentProvider provider= new StandardJavaElementContentProvider();
	        ILabelProvider labelProvider= new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
	        ElementTreeSelectionDialog dialog= new ElementTreeSelectionDialog(window.getShell(), labelProvider, provider);
	        dialog.setValidator(validator);
	        dialog.setComparator(new JavaElementComparator());
	        dialog.setTitle(" Select a test-class ");
	        dialog.setMessage(" Select a class file or a package");
	        dialog.addFilter(filter);
	        dialog.setInput(JavaCore.create(fWorkspaceRoot));
	        dialog.setInitialSelection(initElement);
	        dialog.setHelpAvailable(false);
	        if(!testSelection.isEmpty())
	        dialog.setInitialSelections(testSelection.toArray());
	        
	        String selection = "";
	        if (dialog.open() == Window.OK) {
	            Object[] results = dialog.getResult();
	            testSelection = new LinkedList<Object>();
	            for(Object ob : results) {
	            testSelection.add(ob);
	            }
	            for(Object ob : results) {
	            if(ob instanceof ICompilationUnit) { 
	            if(!selection.isEmpty()) {
	             selection += WizardConfiguration.getSeparator() +
	            	 wConf.getContext().getFullName(((ICompilationUnit)ob).getElementName());}
	            else{ 
	            	DSpotContext context = wConf.getContext();
	            	selection = context.getFullName(((ICompilationUnit)ob).getElementName()); }}
	            else if(ob instanceof IPackageFragment) {
	            	if(!selection.isEmpty())
	            	selection += WizardConfiguration.getSeparator() + 
	            	                     ((IPackageFragment)ob).getElementName() + ".*";
	            	else selection = ((IPackageFragment)ob).getElementName() + ".*";
	       
	            }
	            }
	        }
	        return selection;
	    }
	
}
