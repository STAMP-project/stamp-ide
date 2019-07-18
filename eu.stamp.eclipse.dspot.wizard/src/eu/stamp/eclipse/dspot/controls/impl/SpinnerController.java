
package eu.stamp.eclipse.dspot.controls.impl;

import java.awt.Point;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

import eu.stamp.eclipse.plugin.dspot.controls.SimpleController;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;
import eu.stamp.eclipse.plugin.dspot.properties.DSpotProperties;

/**
 * 
 */
public class SpinnerController extends SimpleController {
    /**
     * 
     */
	private Spinner spinner;
	
	private final int initialSelection;
	
	private final int step;
	
	private final Point interval;
	
	private int decimals;
	
	
	public SpinnerController(String key, String labelText, boolean checkButton,
			int initialSelection,int step,Point interval,int place,String tooltip,int decimals) {
		super(key, labelText, checkButton,false,place,tooltip);
		this.initialSelection = initialSelection;
		this.step = step;
		this.interval = interval;
		this.decimals = decimals;
	}
	/**
	 * 
	 */
    @Override
    public void createControl(Composite parent) {
    	super.createControl(parent);
    	spinner = new Spinner(parent,SWT.BORDER);
    	spinner.setSelection(initialSelection);
    	spinner.setIncrement(step);
    	spinner.setMinimum(interval.x);
    	spinner.setMaximum(interval.y);
    	if(decimals != 0) {
    		spinner.setDigits(decimals);
    	}
    	if(check)GridDataFactory.fillDefaults().indent(DSpotProperties.INDENT)
    	.grab(true,false).applyTo(spinner);
    	else GridDataFactory.fillDefaults().indent(DSpotProperties.INDENT)
    	.span(2,1).grab(true,false).applyTo(spinner);
    	if(firstTime) {
    		DSpotMapping.getInstance().setValue(key,String.valueOf(initialSelection));
    	    firstTime = false;
    	}
    	spinner.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {
				if(proxy == null) listenerAction();
				else proxy.setTemporalData(spinner.getText().replaceAll(",","."));
    		}
    	});
    	
    	if(check) {
    		checkButton.addSelectionListener(new SelectionAdapter() {
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				boolean boo = checkButton.getSelection();
    				spinner.setEnabled(boo);
    				if(!boo) spinner.setSelection(0);
    			}
    		});
    	     checkButton.setSelection(false);
    	     spinner.setEnabled(false);
    	}
    	
    	String value = DSpotMapping.getInstance().getValue(key);
    	updateController(value);
    	
    	if(check && spinner.getSelection() != 0) {
    		checkButton.setSelection(true);
    		spinner.setEnabled(true);
    	}
    	
    }
	@Override
	public void loadProject() {
		// do nothing
	}

	@Override
	public void setText(String text) {
		DSpotMapping.getInstance().setValue(key,text);
		if(spinner == null || spinner.isDisposed()) return;
		if(text == null || text.isEmpty()) spinner.setSelection(initialSelection);
		if(text.contains(".")) {
			text = text.replaceAll("0\\.","");
			text = text.replaceAll("\\.","");
		}
		spinner.setSelection(Integer.parseInt(text));
		listenerAction();
	}

	@Override
	public void setEnabled(boolean enabled) {
		if(spinner == null) return;
		if(spinner.isDisposed()) return;
		spinner.setEnabled(enabled);
	}

	@Override
	public void notifyListener() { 
		DSpotMapping map = DSpotMapping.getInstance();
		String t = spinner.getText();
		map.setValue(key,t);
	}
	@Override
	public void updateController(String data) {
		if(spinner == null || spinner.isDisposed()) return;
		if(data == null || data.isEmpty()) {
			spinner.setSelection(initialSelection);
		    return;
		}
		String myData = data;
		if(myData.contains(",")) myData = myData.substring(myData.indexOf(",")+1);
		if(myData.contains(".")) myData = myData.substring(myData.indexOf(".")+1);
		spinner.setSelection(Integer.parseInt(myData));
	}
	
	private void listenerAction() {
		DSpotMapping map = DSpotMapping.getInstance();
		map.setValue(key,spinner.getText().replaceAll(",","."));
	}
	@Override
	public int checkActivation(String condition) {
		return 0;
	}
}