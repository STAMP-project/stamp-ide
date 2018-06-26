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

import com.richclientgui.toolbox.validation.IFieldErrorMessageHandler;
import com.richclientgui.toolbox.validation.IQuickFixProvider;
import com.richclientgui.toolbox.validation.ValidatingField;
import com.richclientgui.toolbox.validation.string.StringValidationToolkit;
import com.richclientgui.toolbox.validation.validator.IFieldValidator;

import eu.stamp.eclipse.descartes.wizard.validation.DescartesWizardErrorHandler;
import eu.stamp.eclipse.descartes.wizard.validation.IDescartesPage;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionAdapter;

public class AddMutatorDialog extends TitleAreaDialog implements IDescartesPage {
	
	private final static String CUT_CODE = "c";
	private final static String ITSELF_CODE = "i";
	private final static String DOUBLE_CODE = "d";
	
	private boolean decimal;
	private boolean doubleActive;
	private String result;
	
	private int xSize = 0;
	private int ySize = 0;
	
    private StringValidationToolkit valKit = null;
    private final IFieldErrorMessageHandler errorHandler;
	
	// widgets
	private Label leftLabel;
	private Label rightLabel;
	private ValidatingField<String> mutatorField;
	private int quickFixerFlag;
	private Text text;
	
	public AddMutatorDialog(Shell parentShell) { 
		super(parentShell);
		errorHandler = new DescartesWizardErrorHandler(this);
		valKit = new StringValidationToolkit(SWT.LEFT | SWT.TOP,1,true);
        valKit.setDefaultErrorMessageHandler(errorHandler);
		}

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
		
		createField(composite);
		
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
        return new Point(xSize + 20, ySize + 180);
    }
    @Override
    protected void okPressed() {
    	result = text.getText();
    	if(decimal && !result.contains(".")) result = result +".0";
    	if(leftLabel.getText().equalsIgnoreCase("\'") && result
    			.equalsIgnoreCase(result.replaceAll("[^0-9]",""))) result = "\\" + result;
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
    		xSize = xSize + button.computeSize(SWT.DEFAULT,SWT.DEFAULT).x;
    		ySize = ySize + button.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
    	} 
     
     public void configureListener() {
    	 if(code.contains(DOUBLE_CODE)) {
    		 button.addSelectionListener(new SelectionAdapter() {
    			 @Override
    			 public void widgetSelected(SelectionEvent e) {
    				 leftLabel.setText(""); rightLabel.setText("");
    				 text.setEnabled(true);
    				 decimal = true;
    				 doubleActive = true;
    				 text.setText("");
    			 } });
    		 return;
    	 }
    	 if(code.contains(ITSELF_CODE)) {
 		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			text.setEnabled(false);
			leftLabel.setText(""); rightLabel.setText("");
			decimal = false;
			doubleActive = false;
			text.setText(name);
			} }); 
 		return;
    	 }
    	 button.addSelectionListener(new SelectionAdapter() {
    		 @Override
    		 public void widgetSelected(SelectionEvent e) {
    			 int n = code.indexOf(CUT_CODE);
    			 leftLabel.setText(code.substring(0,n));
                 rightLabel.setText(code.substring(n + CUT_CODE.length()));
    			 text.setEnabled(true);
    			 decimal = isDecimal;
    			 doubleActive = false;
    			 text.setText("");
    		 }});
     }
    
    }
    /**
     * creates a text validation field to add the mutators
     * @param composite
     */
   private void createField(Composite composite) {
	   
	   mutatorField = valKit.createTextField(composite,new IFieldValidator<String>() {
		   private String message = "";
		@Override
		public String getErrorMessage() { return message;
		}
		@Override
		public String getWarningMessage() { return null; 
		}
		@Override
		public boolean isValid(String sr) {
			quickFixerFlag = 0;
			if(text == null) return true;
	
            if(!text.isEnabled()) return true; // these mutators are always right
            
            String right = rightLabel.getText();
            
            if(right.contains("\"") || right.contains("\'")) return true;
            
            if(right.contains("f") || doubleActive) {
            	
            	if(sr.isEmpty()) {
            		message = "this mutator must not be empty";
            		return false;
            	}
            	boolean bo = sr.replaceAll("[^0-9.]","").equalsIgnoreCase(sr);
            	if(!bo) {
            		message = "only real numbers are allowed for this mutator";
            		quickFixerFlag = 1;
            		return false;
            	}
            	bo = !(sr.replaceAll("[^.]","").length() > 1); // no more than one point
            	if(bo) return true;
            	message = "the text contains more than one point";
            	quickFixerFlag = 2;
            	return false;
            }
            
            // here only int, short and long are possibles
                if(sr.isEmpty()) {
                	message = "this muttator must not be empty";
                	return false;
                }
            	boolean bo = sr.replaceAll("[^0-9]","").equalsIgnoreCase(sr);
            	if(bo) return true;
            	message = "only numeric characters are allolwed for this mutator";
            	quickFixerFlag = 3;
            	return false;
		}
		@Override
		public boolean warningExist(String arg0) { return false;
		}
	   },false,"");
	   
	   mutatorField.setQuickFixProvider(new IQuickFixProvider<String>() {
		@Override
		public boolean doQuickFix(ValidatingField<String> field) {
			if(quickFixerFlag == 1) {
				text.setText(text.getText().replaceAll("[^0-9.]",""));
				return true;
			}
			if(quickFixerFlag == 2) {
				String target = text.getText();
				String begin = target.substring(0,target.indexOf(".")+1);
				String end = target.substring(target.indexOf(".")+1);
				end = end.replaceAll(".","");
				text.setText(begin + end);
				return true;
			}
			if(quickFixerFlag == 3) {
				text.setText(text.getText().replaceAll("[^0-9]",""));
			}
			return true;
		}
		@Override
		public String getQuickFixMenuText() { return "fix problems";
		}
		@Override
		public boolean hasQuickFix(String sr) {
			return quickFixerFlag != 0;
		}	   
	   });
	   
	   text = (Text)mutatorField.getControl();
	   GridDataFactory.fillDefaults().span(4,1).indent(8, 0).applyTo(text);
	   ySize = ySize + text.computeSize(SWT.DEFAULT,SWT.DEFAULT).y;
   }
	@Override
	public void error(String mess) { setErrorMessage(mess);	}
	@Override
	public void message(String mess, int style) { setMessage(mess,style);	}
    
}
