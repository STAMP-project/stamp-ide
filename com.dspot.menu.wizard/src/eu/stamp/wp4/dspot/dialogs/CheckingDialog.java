package eu.stamp.wp4.dspot.dialogs;

import java.util.ArrayList;

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

/**
 * this class describes a dialog composed by a column of labels and check button
 * each row has a label and a check button
 */
public class CheckingDialog extends Dialog {

	private String[] selection;
	private String[] checkList;
	private String title;
	private ArrayList<Integer> indexOfSelection = new ArrayList<Integer>(1);
	
	public CheckingDialog(Shell parent,String[] checkList,String title) {
		super(parent);
		this.checkList = checkList;
		this.title = title;
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
			labels[i].setText(checkList[i] + "  ");
			buttons[i] = new Button(container,SWT.CHECK);
			buttons[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
                selection[I] = checkList[I];
                indexOfSelection.add(new Integer(I));
                
				}
			});
		}
		
		return container;
	}
	
	@Override
	protected void configureShell(Shell shell) {  // set the title
		super.configureShell(shell);
		shell.setText(title);
	}
	
	
	/*
	 *  public methods to return the information set by the user
	 */
	
	/**
	 * getSelection
	 * @return a String with the user's selection (; as separator)
	 */
	public String getSelection() {
		String ampl = "";
		for(String sr : selection) {
			if(ampl == "") { ampl = sr; } 
			else if(sr != "") {
			ampl = ampl + ";" + sr; }
		}
		return ampl;
	}
	
	/**
	 * getSelectionIndex
	 * @return an int array with the index of the buttons selected by the user
	 */
   public int[] getSelectionIndex() {
	   int[] myIndex = new int[indexOfSelection.size()];
	   for(int i = 0; i < indexOfSelection.size(); i++) {
		   myIndex[i] = indexOfSelection.get(i).intValue();
	   }
	   return myIndex;
   }	
}
