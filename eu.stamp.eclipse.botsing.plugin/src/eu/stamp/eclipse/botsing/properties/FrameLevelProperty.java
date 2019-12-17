package eu.stamp.eclipse.botsing.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Spinner;

public class FrameLevelProperty extends AbstractBotsingProperty {
	
	protected Spinner spinner;
	
	protected int max;

	public FrameLevelProperty(String defaultValue, String key, String name) {
		super(defaultValue, key, name,true,true);
		max = 2;
	}

	@Override
	protected String getData() { return data; }

	@Override
	protected void setData(String data) {
		this.data = data;
		try {
		spinnerSelected(Integer.parseInt(data));
		} catch(NumberFormatException e) { 
			e.printStackTrace(); 
		}
	}
	
	@Override
	public void createControl(Composite composite) {
		
		super.createControl(composite);
		
		spinner = new Spinner(composite,SWT.BORDER);
		spinner.setMinimum(1);
		spinner.setIncrement(1);
		spinner.setMaximum(max);
		
        GridData gridData = new GridData(SWT.FILL,SWT.FILL,true,false);
        int n = ((GridLayout)composite.getLayout()).numColumns;
        gridData.horizontalSpan = n -1;
        spinner.setLayoutData(gridData);
       spinner.addSelectionListener(new SelectionAdapter() {
    	   @Override
    	   public void widgetSelected(SelectionEvent e) {
    		  data = String.valueOf(spinner.getSelection());
    	   }
       });
	}
	
	public void setMaximun(int max) {
		if(max < 1) return;
		else if (spinner != null) {
			if(spinner.getSelection() > max) {
				spinnerSelected(spinner.getSelection());
				this.data = String.valueOf(max);
			}
			spinner.setMaximum(max);
		}
		this.max = max;
	}
	
	public void spinnerSelected(int selection) {
      this.data = String.valueOf(selection);
	  if(spinner != null) Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				if(!spinner.isDisposed()) spinner.setSelection(selection);
			}	
		});
	}
}
