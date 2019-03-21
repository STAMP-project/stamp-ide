
package eu.stamp.eclipse.dspot.controls.impl;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import eu.stamp.eclipse.plugin.dspot.controls.MultiController;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;
import eu.stamp.eclipse.plugin.dspot.properties.DSpotProperties;

/**
 * 
 */
public class ComboController extends MultiController {

	private Combo combo;
	
	public ComboController(String key, String project, String labelText, boolean checkButton,
			String activationDirection,String condition,int place,String tooltip,String[] content) {
		super(key, project, labelText, checkButton,place,tooltip,content);
		this.activationDirection = activationDirection;
		this.condition = condition;
	}
    @Override
    public void createControl(Composite parent) {
    	super.createControl(parent);
    	combo = new Combo(parent,SWT.BORDER|SWT.READ_ONLY);
        if(check) GridDataFactory.fillDefaults().indent(DSpotProperties.INDENT)
        .grab(true,false).applyTo(combo);
        else GridDataFactory.fillDefaults().span(2,1).indent(DSpotProperties.INDENT)
        .grab(true,false).applyTo(combo);

        combo.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {
    			if(proxy == null) {
    			DSpotMapping map = DSpotMapping.getInstance();
    			map.setValue(key,combo.getText());
    			} else {
    				proxy.setTemporalData(combo.getText());
    			}
                activations();
    		}
    	}); 
    	if(content != null)if(content.length > 0)
    		for(String entry : content) combo.add(entry);
    	if(combo.getText() != null && !combo.getText().isEmpty())
    		DSpotMapping.getInstance().setValue(key,combo.getText());
    	updateController(DSpotMapping.getInstance().getValue(key));
    }
	@Override
	protected void setContent(String[] content) {
		DSpotMapping.getInstance().setValue(key, null);
		if(content == null || combo == null) return;
		if(content.length < 1 || combo.isDisposed()) return;
		combo.removeAll();
		for(String entry : content) combo.add(entry);
    	if(combo.getText() != null && !combo.getText().isEmpty())
    		DSpotMapping.getInstance().setValue(key,combo.getText());
	}

	@Override
	protected void setSelection(String[] selection) {
		if(selection == null) return;
		if(selection.length < 1) {
			DSpotMapping.getInstance().setValue(key,"");
			return;
		}
		DSpotMapping.getInstance().setValue(key,selection[0]);
		if(combo == null) return;
	    if(combo.isDisposed()) return;
	    combo.setText(selection[0]);
	}
	@Override
	public void setEnabled(boolean enabled) {
		if(combo == null) return;
		if(combo.isDisposed()) return;
		combo.setEnabled(enabled);
	}
	@Override
	public void loadProject() {
		super.loadProject();
		if(combo == null || combo.isDisposed()) return;
		if(combo.getItemCount() > 0)
		combo.setText(combo.getItem(0));
	}
	@Override
	public void notifyListener() { 
		if(combo == null) return;
		combo.notifyListeners(SWT.Selection,new Event()); 
		}
	
	@Override
	public void updateController(String data) {
		if(data == null || combo == null || combo.isDisposed()) return;
		combo.setText(data);
	}
	@Override
	public int checkActivation(String condition) {
        if(combo.getText().equalsIgnoreCase(condition)) return ACTIVATION;
		return ANTI_ACTIVATION;
	}
}