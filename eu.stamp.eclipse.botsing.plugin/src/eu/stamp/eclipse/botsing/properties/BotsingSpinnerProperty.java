package eu.stamp.eclipse.botsing.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

public class BotsingSpinnerProperty extends AbstractBotsingProperty {
	
	private Spinner spinner;
	
	private final int step;
	private final int minimun;
	private final int maximun;
	
	private String data;
	
	public BotsingSpinnerProperty(String defaultValue,
			String key,String name) {
		this(defaultValue,key,name,1,1,0);
	}
	
	public BotsingSpinnerProperty(String defaultValue,
			String key,String name,int step,int minimun,int maximun) {
		
		super(defaultValue,key,name);
		this.step = step;
		this.minimun = minimun;
		this.maximun = maximun;
	}

	@Override
	protected String getData() { 
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				data = spinner.getText();
			}
		});
		return data; 
		}

	@Override
	protected void setData(String data) {
		this.data = data;
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				   spinner.setSelection(Integer.parseInt(data));
			}
		});
	}

	@Override
	public void createControl(Composite composite) {
        
        Label label = new Label(composite,SWT.NONE);
        label.setText(name);
        
        spinner = new Spinner(composite,SWT.BORDER);
        spinner.setMinimum(minimun);
        if(maximun > minimun + 1) spinner.setMaximum(maximun);
        spinner.setIncrement(step);
        
        GridData data = 
        		new GridData(SWT.FILL,SWT.FILL,true,false);
        int n = ((GridLayout)composite.getLayout()).numColumns;
        data.horizontalSpan = n -1;
       spinner.setLayoutData(data);
	}

}
