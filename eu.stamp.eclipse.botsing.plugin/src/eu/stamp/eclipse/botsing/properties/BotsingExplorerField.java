package eu.stamp.eclipse.botsing.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public abstract class BotsingExplorerField extends AbstractBotsingProperty {

	private Text text;
	
	private String data;
	
	protected BotsingExplorerField(String defaultValue, String key, String name) {
		super(defaultValue, key, name);
	}

	@Override
	protected String getData() { 
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				data = text.getText();
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
				text.setData(data);
			}
		});
	 }

	@Override
	public void createControl(Composite composite) {

       Label label = new Label(composite,SWT.NONE);
       label.setText(name);
		
	   text = new Text(composite,SWT.READ_ONLY | SWT.BORDER);

	   int n = ((GridLayout)composite.getLayout()).numColumns;
	   GridData data = new GridData(SWT.FILL,SWT.FILL,true,false);
	   data.horizontalSpan = n - 2;
	   text.setLayoutData(data);
	   
	   Button button = new Button(composite,SWT.PUSH);
	   button.setText(" Select ");
	   
	   GridData buttonData = new GridData(SWT.FILL,SWT.FILL,true,false);
	   button.setLayoutData(buttonData);
	   
	   button.addSelectionListener(new SelectionAdapter() {
		   @Override
		   public void widgetSelected(SelectionEvent e) {
			   String selection = openExplorer();
			   if(selection != null) text.setText(selection);
		   }
	   });
	}
       protected abstract String openExplorer();
}
