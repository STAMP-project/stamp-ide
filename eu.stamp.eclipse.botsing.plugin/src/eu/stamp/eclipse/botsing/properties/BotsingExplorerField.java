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
	
	protected BotsingExplorerField(String defaultValue, String key, String name) {
		super(defaultValue, key, name);
	}

	@Override
	protected String getData() { return data; }

	@Override
	protected void setData(String data) { 
		super.data = data;
		if(text == null) return;
		if(text.isDisposed()) return;
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				text.setText(data);
			}
		});
	 }

	@Override
	public void createControl(Composite composite) {

       Label label = new Label(composite,SWT.NONE);
       label.setText(name);
		
	   text = new Text(composite,SWT.READ_ONLY | SWT.BORDER);
	   text.setText(data);

	   int n = ((GridLayout)composite.getLayout()).numColumns;
	   GridData gridData = new GridData(SWT.FILL,SWT.FILL,true,false);
	   gridData.horizontalSpan = n - 2;
	   text.setLayoutData(gridData);
	   
	   Button button = new Button(composite,SWT.PUSH);
	   button.setText(" Select ");
	   
	   GridData buttonData = new GridData(SWT.FILL,SWT.FILL,true,false);
	   button.setLayoutData(buttonData);
	   
	   button.addSelectionListener(new SelectionAdapter() {
		   @Override
		   public void widgetSelected(SelectionEvent e) {
			   String selection = openExplorer();
			   if(selection != null){
				   text.setText(selection);
			       data = selection;
			   }
		   }
	   });
	}
	
       protected abstract String openExplorer();
}
