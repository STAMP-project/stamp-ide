
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

import eu.stamp.eclipse.dspot.wizard.validation.ExtraChecker;
import eu.stamp.eclipse.dspot.wizard.validation.ValidationProvider;
import eu.stamp.eclipse.plugin.dspot.controls.Controller;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;
import eu.stamp.eclipse.plugin.dspot.properties.DSpotProperties;
import eu.stamp.eclipse.plugin.dspot.wizard.DSpotPage1;
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
	/**
	 * 
	 */
	//private String confTx;
	
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

        NoEmptyChecker extraCheck = new NoEmptyChecker();
	    Text newConfText = ValidationProvider.getTextWithvalidation(parent,page,
	    		"configuration name ",ValidationProvider.VALIDATOR_DEFAULT,false,extraCheck);
        extraCheck.setText(newConfText);
	    GridDataFactory.fillDefaults().indent(DSpotProperties.INDENT)
	    .grab(true,false).applyTo(newConfText);
	    newConfText.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) { 
				DSpotMapping.getInstance().setConfigurationName(newConfText.getText());
			}
			});
	    
	    combo.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
	    	String name = combo.getText();
	    	if(name == null || name.isEmpty()) {
	    		newConfButton.setSelection(true);
	    		DSpotMapping.getInstance().setConfigurationName(name);
	    		newConfButton.notifyListeners(SWT.Selection,new Event());
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
	    
	    newConfButton.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		
        	boolean selection = newConfButton.getSelection();
        	newConfText.setEnabled(selection);
        	combo.setEnabled(!selection);
        	if(selection) {
        	newConfText.setText("new_configuration");
        	combo.setText("");
        	newConfText.notifyListeners(SWT.Segments,new Event());
        		}
        	if(!selection) {
        	newConfText.setText("");
        	for(String sr : combo.getItems())if(!sr.isEmpty()) {
        		combo.setText(sr);
        		break;
        	}
        	if(!combo.getText().isEmpty())
        		combo.notifyListeners(SWT.Selection,new Event());
        		}	
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
	
	private class NoEmptyChecker implements ExtraChecker {
		private Text text;
    	@Override
		public String before() {
            if(text == null || text.isDisposed() || !text.isEnabled())
            	return null;
            if(text.getText() == null || text.getText().isEmpty())
            	return "Configuration is empty";
			return null;
		}
		@Override
		public String after() { return null; }
		
		public void setText(Text text) {
			this.text = text;
		}
	}
}