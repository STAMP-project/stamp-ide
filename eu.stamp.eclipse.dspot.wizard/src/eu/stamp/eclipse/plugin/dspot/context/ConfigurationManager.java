
package eu.stamp.eclipse.plugin.dspot.context;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SegmentEvent;
import org.eclipse.swt.events.SegmentListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.stamp.eclipse.dspot.wizard.validation.ValidationProvider;
import eu.stamp.eclipse.plugin.dspot.controls.Controller;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;
import eu.stamp.eclipse.plugin.dspot.properties.DSpotProperties;
/**
 * 
 */
public class ConfigurationManager {
	/**
	 * 
	 */
	public static final String LIST_SEPARATOR = "%";
	/**
	 * 
	 */
	private ILaunchConfiguration[] configurations;
	/**
	 * 
	 */
	private ProjectManager projectManager;
	
	public ConfigurationManager() {
		try {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType(DSpotProperties.CONFIGURATION_ID);
		configurations = manager.getLaunchConfigurations(type);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}
	/**
	 * 
	 * @param parent 
	 */
	public void createConfigurationControl(Composite parent,Object page) {
		
		Label loadLabel = new Label(parent,SWT.NONE);
		loadLabel.setText("Load configuration : ");
		loadLabel.setToolTipText(" load a stored DSpot configuration");
		GridDataFactory.swtDefaults().indent(DSpotProperties.INDENT).applyTo(loadLabel);
		
		Combo combo = new Combo(parent,SWT.BORDER | SWT.READ_ONLY);
	    GridDataFactory.fillDefaults().span(2,1)
	    .indent(DSpotProperties.INDENT).grab(true,false).applyTo(combo);
	    for(ILaunchConfiguration conf : configurations) combo.add(conf.getName());
	    combo.add("");
	    
	    Label newConfLabel = new Label(parent,SWT.NONE);
	    newConfLabel.setText("New configuration : ");
	    newConfLabel.setToolTipText("create a new configuration with the given name");
	    GridDataFactory.swtDefaults().indent(DSpotProperties.INDENT).applyTo(newConfLabel);
	    
	    Button newConfButton = new Button(parent,SWT.CHECK);
	    GridDataFactory.swtDefaults().indent(DSpotProperties.INDENT).applyTo(newConfButton);
	    
	    Text newConfText = ValidationProvider.getTextWithvalidation(parent,page,
	    		"configuration name ",ValidationProvider.VALIDATOR_DEFAULT,false);
	    
	    GridDataFactory.fillDefaults().indent(DSpotProperties.INDENT)
	    .grab(true,false).applyTo(newConfText);
	    newConfText.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
				DSpotMapping.getInstance().setConfigurationName(newConfText.getText());
			}
	    });
	    newConfButton.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		boolean selected = newConfButton.getSelection();
                newConfText.setEnabled(selected);
                combo.setEnabled(!selected);
                if(selected) {
                	newConfText.setText("new_configuration");
                	combo.setText("");
                }
                else{
                	newConfText.setText("");
                	combo.setText(combo.getItem(0));
                	combo.notifyListeners(SWT.Selection,new Event());
                }
	    	}
	    });
	    
	    combo.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
	    	String name = combo.getText();
	    	if(name == null || name.isEmpty()) {
	    		newConfButton.setSelection(true);
	    		DSpotMapping.getInstance().setConfigurationName(name);
	    		newConfButton.notifyAll();
	    		return;
	    	}
	    	DSpotMapping.getInstance().setConfigurationName(name);
	    	ILaunchConfiguration conf = null;
	    	for(ILaunchConfiguration confi : configurations)
	    		if(confi.getName().equalsIgnoreCase(name)) {
	    			conf = confi;
	    			break;
	    		}
	    	if(conf != null) loadConf(conf);
	    }
	    });
		newConfButton.setSelection(true);
		newConfButton.notifyListeners(SWT.Selection,new Event());
	}
	
	private void loadConf(ILaunchConfiguration conf) {
		if(projectManager != null)
			try {
				projectManager.loadProject(projectManager.getProjectFromName(
						conf.getAttribute(DSpotProperties.PROJECT_KEY,"")));
			} catch (CoreException e) {
				e.printStackTrace();
			}
		List<Controller> controllers = DSpotMapping.getInstance().getAllControllers();
		for(Controller controller : controllers) {
			controller.loadConfiguration(conf);
		}
	}
}