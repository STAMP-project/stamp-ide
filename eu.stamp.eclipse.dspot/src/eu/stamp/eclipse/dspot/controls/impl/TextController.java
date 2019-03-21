
package eu.stamp.eclipse.dspot.controls.impl;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.events.SegmentEvent;
import org.eclipse.swt.events.SegmentListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import eu.stamp.eclipse.dspot.wizard.validation.ValidationProvider;
import eu.stamp.eclipse.plugin.dspot.controls.SimpleController;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;
import eu.stamp.eclipse.plugin.dspot.properties.DSpotProperties;
import eu.stamp.eclipse.plugin.dspot.wizard.IPageUserElement;
/**
 * 
 */
public class TextController extends SimpleController implements IPageUserElement {
	/**
     * 
     */
	private Text text;

	/**
	 * 
	 */
	private Object page;
	
    public TextController(String key, String labelText, boolean checkButton,
    		boolean projectDependent,int place,String tooltip) {
		super(key, labelText, checkButton, projectDependent,place,tooltip);
	}
    
	/**
	 * 
	 */
    @Override
    public void createControl(Composite parent) {
    	super.createControl(parent);
    	
    	text = ValidationProvider.getTextWithvalidation(parent,page,labelText.replaceAll(" :",""),
    			ValidationProvider.VALIDATOR_DEFAULT,false);
    	
    	if(check) GridDataFactory.fillDefaults().indent(DSpotProperties.INDENT)
    	.grab(true,false).applyTo(text);
    	else GridDataFactory.fillDefaults().indent(DSpotProperties.INDENT)
    	.span(2,1).grab(true,false).applyTo(text);
    	text.addSegmentListener(new SegmentListener(){
			@Override
			public void getSegments(SegmentEvent event) {
				if(listenerOn)
				DSpotMapping.getInstance().setValue(key,text.getText());
			}
    	});
    }
	@Override
	public void setText(String selection) {
		if(selection == null || text == null) {
			DSpotMapping.getInstance().setValue(key,null);
			return;
		}
		if(text.isDisposed() || selection.isEmpty()) {
			DSpotMapping.getInstance().setValue(key,null);
			return;
		}
		text.setText(selection);
	}

	@Override
	public void loadProject() {
		if(projectDependent)if(text != null)if(!text.isDisposed())
			text.setText("");
	}
	@Override
	public void setEnabled(boolean enabled) {
		if(text == null) return;
		if(text.isDisposed()) return;
		text.setEnabled(enabled);
	}
	@Override
	public void notifyListener() { text.notifyAll(); }
	
	@Override
	public void updateController(String data) {
		if(data == null || text == null || text.isDisposed()) return;
		text.setText(data);
	}
	
	@Override
	public void setPage(Object page) { this.page = page; }

	@Override
	public int checkActivation(String condition) {
		// TODO Auto-generated method stub
		return 0;
	}
}