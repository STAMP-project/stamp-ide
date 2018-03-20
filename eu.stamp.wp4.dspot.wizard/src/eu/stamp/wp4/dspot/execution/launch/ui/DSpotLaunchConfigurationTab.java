package eu.stamp.wp4.dspot.execution.launch.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.stamp.wp4.dspot.execution.launch.DSpotProperties;

public class DSpotLaunchConfigurationTab extends AbstractLaunchConfigurationTab {

	private IJavaProject javaProject;
	private Text parametersText;
	
	@Override
	public void createControl(Composite parent) {

		Composite container = new Group(parent,SWT.BORDER);
		setControl(container);
		
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(container);
		
		Label projectLabel = new Label(container,SWT.NONE);
		projectLabel.setText("Project : ");
		GridDataFactory.swtDefaults().applyTo(projectLabel);
		
		Text projectText = new Text(container,SWT.BORDER);
		
		GridDataFactory.fillDefaults().grab(true, false).applyTo(projectText);
		
		Button projectButton = new Button(container,SWT.PUSH);
		projectButton.setText("Browse");
		GridDataFactory.swtDefaults().applyTo(projectButton);
		projectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}
		});  // end of the selection listener
		
		Label parametersLabel = new Label(container,SWT.NONE);
		parametersLabel.setText("Dspot execution parameters : ");
        GridDataFactory.swtDefaults().applyTo(parametersLabel);
		
        parametersText = new Text(container,SWT.BORDER);
        		
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(parametersText);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {	
		
		String arguments = parametersText.getText();  // obtain the string with DSpot arguments
		
		DebugPlugin plugin = DebugPlugin.getDefault();
	      ILaunchManager lm = plugin.getLaunchManager();
	      try {
	      configuration.setAttribute(
	        IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, 
	        javaProject.getElementName());
	      configuration.setAttribute(
	        IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, DSpotProperties.MAIN_CLASS);
	      configuration.setAttribute(
	  	        IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, arguments);
	      ILaunchConfiguration config = configuration.doSave();  
	      } catch(CoreException e) {
	    	  e.printStackTrace();}
	      
	}

	@Override
	public String getName() {
		return " DSpot launch configuration tab ";
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		
	}

}
