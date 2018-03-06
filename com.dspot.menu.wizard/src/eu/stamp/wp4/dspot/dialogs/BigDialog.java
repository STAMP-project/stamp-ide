package eu.stamp.wp4.dspot.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class BigDialog extends Dialog {
	
	/*
	 *  this is a dialog to perform the help of the wizard pages
	 *  the constructor requires a string with the general description of the page
	 *  and an string array with the text to display (each string of the array is a line)
	 */
	
	private String description;
	private String[] myText;
	
	public BigDialog(Shell parent,String description, String[] myText) {
		super(parent);
		this.description = description;
		this.myText = myText;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);
		
		Label visualLabel = new Label(container,SWT.NONE);   // first row contains an image and the general description
		visualLabel.setImage(Display.getDefault().getSystemImage(SWT.ICON_QUESTION));
		
		Label[] labels = new Label[myText.length + 2];   // labels to display the text
		GridData gData = new GridData(SWT.FILL,SWT.NONE,true,false);
		gData.horizontalSpan = 2;
		
		labels[0] = new Label(container,SWT.NONE); // without gData
		labels[0].setText(" " + description);
		Font font = new Font(labels[0].getDisplay(),new FontData("Mono",10,SWT.BOLD));
		labels[0].setFont(font);
		
		labels[1] = new Label(container,SWT.NONE);
		labels[1].setText("");
		labels[1].setLayoutData(gData); 
		
		for(int i = 2; i < labels.length; i++) {  // put the text in the composite
			labels[i] = new Label(container,SWT.NONE);
			labels[i].setText("  " + myText[i-2]);
			labels[i].setLayoutData(gData);
		}
		
		return container;
		}
@Override
protected void configureShell(Shell newShell) {
	super.configureShell(newShell);
	newShell.setText("  Help");
}

@Override
protected Point getInitialSize() {  // the size of the dialog depends on the text
	int w = 10;
	int w0 = 0;	
	for(String sr : myText) {
		w0 = sr.toCharArray().length;
		if(w0 > w) {
			w = w0;
		}}
	w0 = description.toCharArray().length;
	if(w0 > w) {
		w = w0;
	}
	if(w < 110) {
	w = 8*w; } else { w = 6*w;}
	return new Point(w,35*(myText.length+2));
}

}
