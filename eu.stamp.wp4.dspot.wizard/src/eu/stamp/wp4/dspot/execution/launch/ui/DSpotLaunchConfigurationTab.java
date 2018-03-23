package eu.stamp.wp4.dspot.execution.launch.ui;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SegmentListener;
import org.eclipse.swt.events.SegmentEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.swt.widgets.Shell;

import eu.stamp.wp4.dspot.execution.launch.DSpotProperties;

public class DSpotLaunchConfigurationTab extends AbstractLaunchConfigurationTab {

	private IJavaProject javaProject;
	private String parameters;
	private Text projectText;
	private Text parametersText;
	
	@Override
	public void createControl(Composite parent) {

		Composite container = new Group(parent,SWT.BORDER);
		setControl(container);
		
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(container);
		
		Label projectLabel = new Label(container,SWT.NONE);
		projectLabel.setText("Project : ");
		GridDataFactory.swtDefaults().applyTo(projectLabel);
		
		projectText = new Text(container,SWT.BORDER);
		projectText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				parameters = projectText.getText();
			}	
		});
		projectText.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
				parameters = projectText.getText();
			}	
		});
		GridDataFactory.fillDefaults().grab(true, false).applyTo(projectText);
		
		Button projectButton = new Button(container,SWT.PUSH);
		projectButton.setText("Browse");
		GridDataFactory.swtDefaults().applyTo(projectButton);
		projectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showProjectDialog();
			}
		});  // end of the selection listener
		
		Label parametersLabel = new Label(container,SWT.NONE);
		parametersLabel.setText("Dspot execution parameters : ");
        GridDataFactory.swtDefaults().applyTo(parametersLabel);
		
        parametersText = new Text(container,SWT.BORDER);		
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(parametersText);
		parametersText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {	
		/*
		String arguments = parametersText.getText();  // obtain the string with DSpot arguments

	      try {
	      configuration.setAttribute(
	        IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, 
	        javaProject.getElementName());
	      configuration.setAttribute(
	  	        IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, arguments);
	      configuration.doSave();  
	      } catch(CoreException e) {
	    	  e.printStackTrace();}
	     */ 
	}

	@Override
	public String getName() {
		return " DSpot launch configuration tab ";
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		
	}
	
	@SuppressWarnings("restriction")
	private void showProjectDialog() {
		
		Class<?>[] acceptedClasses = new Class[] {IJavaProject.class};
		TypedElementSelectionValidator validator = new TypedElementSelectionValidator(acceptedClasses,true) {
			@Override
			public boolean isSelectedValid(Object element) {
				if(element instanceof IJavaProject) {
					return true;
				}
				return false;
			}
		};
		
		ViewerFilter filter= new TypedViewerFilter(acceptedClasses);	
		
		  IWorkspaceRoot fWorkspaceRoot= ResourcesPlugin.getWorkspace().getRoot();
	        
	        StandardJavaElementContentProvider provider= new StandardJavaElementContentProvider();
	        ILabelProvider labelProvider= new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
	        ElementTreeSelectionDialog dialog= new ElementTreeSelectionDialog(new Shell(), labelProvider, provider);
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
	             javaProject =  (IJavaProject) ob;
	             projectText.setText(javaProject.getElementName());}
	            }
	        }
		
	}

}
