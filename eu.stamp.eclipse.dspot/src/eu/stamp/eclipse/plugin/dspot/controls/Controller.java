
package eu.stamp.eclipse.plugin.dspot.controls;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;
import eu.stamp.eclipse.plugin.dspot.properties.DSpotProperties;
/**
 * 
 */
public abstract class Controller implements Comparable<Controller>{
    
	// activation constants
	protected static final int ACTIVATION = 1;
	protected static final int ANTI_ACTIVATION = 2;
	/**
	 * 
	 */
	private final int place;
	/**
	 * 
	 */
	protected final String tooltip;
	/**
	 *
	 */
	protected final String labelText;
	/**
	 * 
	 */
	protected final String key;
    /**
     * 
     */
	protected final boolean check;
	/**
	 * 
	 */
	protected Button checkButton;
	/**
	 * 
	 */
	protected boolean listenerOn;
	/**
	 * 
	 */
	protected boolean firstTime;
	/**
	 * 
	 */
	protected boolean isEnabled;
	/**
	 * 
	 */
	protected String activationDirection;
	/**
	 * 
	 */
	protected String condition;
	/**
	 * 
	 * @param key
	 * @param project
	 * @param labelText
	 * @param checkButton
	 */
	public Controller(String key,String labelText,boolean checkButton,int place,String tooltip){
		this.key = key;
		this.labelText = labelText;
        this.check = checkButton;
        listenerOn = true;
        this.place = place;
        this.tooltip = tooltip;
        firstTime = true;
        isEnabled = true;
	}
	@Override
	public int compareTo(Controller otherController) {
		return (this.place - otherController.place);
	}
	/**
	 * 
	 * @param parent 
	 */
	public void createControl(Composite parent) {

    	 Label label = new Label(parent,SWT.NONE);
    	 label.setText(labelText);
    	 if(tooltip != null) label.setToolTipText(tooltip);
    	 GridDataFactory.swtDefaults().indent(DSpotProperties.INDENT).applyTo(label);
    	 
         if(check) {
        	 checkButton = new Button(parent,SWT.CHECK);
        	 GridDataFactory.swtDefaults().indent(DSpotProperties.INDENT).applyTo(checkButton);
         }
	}
	protected void setData(String data) {
		DSpotMapping map = DSpotMapping.getInstance();
		map.setValue(key,data);
	}
	public String getKey() { return key; }
	/**
	 * 
	 * @param listenerOn
	 */
	public void setListenerOn(boolean listenerOn) {
		this.listenerOn = listenerOn;
	}
	/**
	 * 
	 */
	protected void activations() {
		if(activationDirection == null || activationDirection.isEmpty()) return;
		String[] activations;
		String[] conditions;
		if(activationDirection.contains(",")) {
			activations = activationDirection.split(",");
			conditions = condition.split(",");
		} else {
			activations = new String[]{activationDirection};
			conditions = new String[]{condition};
		}
		if(activations.length != conditions.length) return;
		for(int i = 0; i < activations.length; i++){
			int order = checkActivation(conditions[i]);
			if(order == ACTIVATION) DSpotMapping.getInstance()
			.getController(activations[i]).setEnabled(true);
			else if(order == ANTI_ACTIVATION)DSpotMapping.getInstance()
			.getController(activations[i]).setEnabled(false);
		}
	}
	/**
	 * 
	 */
    public abstract void notifyListener();
	/**
	 * 
	 * @param enabled
	 */
	public abstract void setEnabled(boolean enabled);
	/**
	 * 
	 */
	public abstract void loadConfiguration(ILaunchConfiguration configuration);
	/**
	 * 
	 */
	public abstract void loadProject();
	/**
	 * 
	 */
	public abstract void updateController(String data);
	/**
	 * 
	 */
	protected abstract int checkActivation(String condition);
}