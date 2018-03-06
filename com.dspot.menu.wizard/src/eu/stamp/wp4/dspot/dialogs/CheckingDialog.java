package eu.stamp.wp4.dspot.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CheckingDialog extends Dialog {

	private String[] selection;
	private String[] checkList;
	
	public CheckingDialog(Shell parent,String[] checkList) {
		super(parent);
		this.checkList = checkList;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);
		
		int n = checkList.length;
		selection = new String[n];
		for(int i = 0; i < n; i++) {
			selection[i] = "";  // initialize selection
		}
		Label[] labels = new Label[n];
		Button[] buttons = new Button[n];
		
		for(int i = 0; i < n; i++) {
			final int I = i;
			labels[i] = new Label(container,SWT.NONE);  // create a column of labels and check buttons
			labels[i].setText(checkList[i]);
			buttons[i] = new Button(container,SWT.CHECK);
			buttons[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
                selection[I] = checkList[I];
				}
			});
		}
		
		return container;
	}

	public String getSelection() {
		String ampl = "";
		for(String sr : selection) {
			if(ampl == "") { ampl = sr; } 
			else if(sr != "") {
			ampl = ampl + ":" + sr; }
		}
		return ampl;
	}

	
}
