package eu.stamp.eclipse.botsing.model.generation.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.internal.Workbench;

@SuppressWarnings("restriction")
public abstract class StampHandler extends AbstractHandler {

  protected IJavaProject getProject() {
		ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow()
				.getSelectionService();
		ISelection selection = selectionService.getSelection();
		IJavaProject jproject = null;
		Object element;

		if (selection instanceof IStructuredSelection) {
		element = ((IStructuredSelection) selection).getFirstElement();

		if (element instanceof IJavaElement) {
		jproject = ((IJavaElement) element).getJavaProject();
		return jproject;
		}
		if (element instanceof IProject) {

		IProject pro = (IProject) element;
		jproject = new JavaProject(pro, null);
		return jproject;

		}
		}
		if(jproject == null) {

		selection = selectionService.getSelection("org.eclipse.jdt.ui.PackageExplorer");

		if (selection instanceof IStructuredSelection) {
		element = ((IStructuredSelection) selection).getFirstElement();

		if (element instanceof IJavaElement) {
		jproject = ((IJavaElement) element).getJavaProject();
		return jproject;
		}}
		            selection = selectionService.getSelection("org.eclipse.ui.navigator.ProjectExplorer");

		if (selection instanceof IStructuredSelection) {
		element = ((IStructuredSelection) selection).getFirstElement();

		if (element instanceof IProject) {
		IProject pro = (IProject) element;
		jproject = new JavaProject(pro, null);
		return jproject;
		}}
		}
		return null;
	}

}
