package com.dspot.menu.wizard;

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

public class AmplifiersDialog extends Dialog{
	
	private String[] selection;
	
	public AmplifiersDialog(Shell parent) {
		super(parent);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);
		
		String[] amplifiers = {"StringLiteralAmplifier","NumberLiteralAmplifier","CharLiteralAmplifier",
				"BooleanLiteralAmplifier","AllLiteralAmplifier","MethodAdd","MethodRemove","TestDataMutator",
				"StatementAdd",""};  // the possible amplifiers
		
		int n = amplifiers.length;
		selection = new String[n];
		for(int i = 0; i < n; i++) {
			selection[i] = "";  // initialize selection
		}
		Label[] labels = new Label[n];
		Button[] buttons = new Button[n];
		
		for(int i = 0; i < n; i++) {
			final int I = i;
			labels[i] = new Label(container,SWT.NONE);  // create a column of labels and check buttons
			labels[i].setText(amplifiers[i]);
			buttons[i] = new Button(container,SWT.CHECK);
			buttons[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
                selection[I] = amplifiers[I];
				}
			});
		}
		
		return container;
	}
	
	@Override
	protected void okPressed() {
		super.okPressed();
	}
	
	public String getAmplifiers() {
		String ampl = "";
		for(String sr : selection) {
			if(ampl == "") { ampl = sr; } 
			else if(sr != "") {
			ampl = ampl + ":" + sr; }
		}
		return ampl;
	}

}
