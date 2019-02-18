
package eu.stamp.eclipse.plugin.dspot.controls;

import java.awt.Point;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

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
	
	public SpinnerController(String key, String labelText, boolean checkButton,
			int initialSelection,int step,Point interval,int place,String tooltip) {
		super(key, labelText, checkButton,false,place,tooltip);
		this.initialSelection = initialSelection;
		this.step = step;
		this.interval = interval;
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
				if(listenerOn) listenerAction();
    		}
    	});
    	String value = DSpotMapping.getInstance().getValue(key);
    	updateController(value);
    }
	@Override
	public void loadProject() {
		// do nothing
	}

	@Override
	protected void setText(String text) {
		DSpotMapping.getInstance().setValue(key,text);
		if(spinner == null || text == null) return;
		if(spinner.isDisposed() || text.isEmpty()) return;
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
		if(data == null || spinner == null || data.isEmpty() || spinner.isDisposed()) return;
		spinner.setSelection(Integer.parseInt(data));
	}
	
	private void listenerAction() {
		DSpotMapping map = DSpotMapping.getInstance();
		map.setValue(key,spinner.getText());
	}
	@Override
	protected int checkActivation(String condition) {
		// TODO Auto-generated method stub
		return 0;
	}
}