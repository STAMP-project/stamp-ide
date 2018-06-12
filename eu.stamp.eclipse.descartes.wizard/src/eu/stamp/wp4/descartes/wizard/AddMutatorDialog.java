package eu.stamp.wp4.descartes.wizard;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionAdapter;

public class AddMutatorDialog extends TitleAreaDialog {
	
	private final static String CUT_CODE = "c";
	private final static String ITSELF_CODE = "i";
	private final static String DOUBLE_CODE = "d";
	
	private boolean decimal;
	private String result;
	
	// widgets
	private Label leftLabel;
	private Label rightLabel;
	private Text text;
	
	public AddMutatorDialog(Shell parentShell) { super(parentShell); }

	@Override
	public void create() {
		super.create();
		setTitle("Add mutator");
		setMessage("Select the mutator type and write the text");
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		// create the composite
   	     Composite composite = (Composite)super.createDialogArea(parent);
		 GridLayout layout = new GridLayout(6,true);
		 composite.setLayout(layout);
		 
		Label space = new Label(composite,SWT.NONE);
		space.setText("");
		GridDataFactory.swtDefaults().span(5,1).applyTo(space);
		
		leftLabel = new Label(composite,SWT.NONE);
		leftLabel.setText("");
		leftLabel.setAlignment(SWT.RIGHT);
		GridDataFactory.fillDefaults().applyTo(leftLabel);
		
		text = new Text(composite,SWT.BORDER);
		text.setText("");
		GridDataFactory.fillDefaults().span(4,1).applyTo(text);
		
		rightLabel = new Label(composite,SWT.NONE);
		rightLabel.setText("");
		GridDataFactory.fillDefaults().applyTo(rightLabel);
		
		
		String[] mutators = {"void","null","empty","true","false","string","character",
			"int","double","byte","short","float","long"};
		
		String[] codes = {ITSELF_CODE , ITSELF_CODE , ITSELF_CODE , ITSELF_CODE,
			 ITSELF_CODE, "\"" + CUT_CODE + "\"", "'"+ CUT_CODE + "'" , CUT_CODE,
			 DOUBLE_CODE , "(byte)" + CUT_CODE , "(short)" + CUT_CODE, CUT_CODE + "f",CUT_CODE + "L"};
		
		GridDataFactory factory = GridDataFactory.swtDefaults().span(2,1);
				
		for(int i = 0; i < mutators.length; i++) {
			MyMutator mut = new MyMutator(mutators[i],codes[i],composite,factory);
			mut.configureListener();
		}
		
		return composite;
	}
	@Override
 	protected void configureShell(Shell shell) {  // set the title
 		super.configureShell(shell);
 		shell.setText(" Add mutator ");
 	}
    @Override
    protected Point getInitialSize() { // default size of the dialog
        return new Point(400, 500);
    }
    @Override
    protected void okPressed() {
    	result = text.getText();
    	if(decimal && !result.contains(".")) result = result +".0";
    	result = leftLabel.getText() + result + rightLabel.getText();
    	super.okPressed();
    }
    public String getResult() { return result; }
    
    private class MyMutator {
    	
    	final String name;
    	final String code;
    	final boolean isDecimal;
    	final Button button;
    	
     public MyMutator(String name, String code,Composite composite, GridDataFactory factory) {
    	 
    		this.name = name;
    		this.code = code;
    		if(code.contains("f") || code.contains("L") || 
    				code.contains(DOUBLE_CODE)) isDecimal = true;
    		else isDecimal = false;
    		button = new Button(composite,SWT.RADIO);
    		button.setText(name);
    		factory.applyTo(button);
    	} 
     
     public void configureListener() {
    	 if(code.contains(DOUBLE_CODE)) {
    		 button.addSelectionListener(new SelectionAdapter() {
    			 @Override
    			 public void widgetSelected(SelectionEvent e) {
    				 leftLabel.setText(""); rightLabel.setText("");
    				 text.setText(""); text.setEnabled(true);
    				 decimal = true;
    			 } });
    		 return;
    	 }
    	 if(code.contains(ITSELF_CODE)) {
 		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			leftLabel.setText(""); rightLabel.setText("");
			text.setText(name); text.setEnabled(false);
			} });
 		return;
    	 }
    	 button.addSelectionListener(new SelectionAdapter() {
    		 @Override
    		 public void widgetSelected(SelectionEvent e) {
    			 int n = code.indexOf(CUT_CODE);
    			 leftLabel.setText(code.substring(0,n));
                 rightLabel.setText(code.substring(n + CUT_CODE.length()));
    			 text.setText(""); text.setEnabled(true);
    			 decimal = isDecimal;
    		 }});
     }
     
    }
    
}
