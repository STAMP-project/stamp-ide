package eu.stamp.wp4.descartes.wizard;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
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

/**
 *  This class represents the dialog to add new mutators to the list
 *  the dialog contains a set of radio buttons to choose the mutator type 
 *  and a text, when there is only one possible text for the selected 
 *  mutator type (for example true) the text becomes disabled and the text is shown
 *  else the text is enabled and it provides the validation and quick fixer 
 *  corresponding to the selected mutator type, the labels in the left and right of the text
 *  show the required syntax, for example "" for Strings.
 */
public class AddMutatorDialog extends TitleAreaDialog implements IDescartesPage {
	
    // static strings to process each mutator code when generating the buttons listeners
	/**
	 *   this string separates the strings placed in the left and right sides of the text
	 *   for each mutator type
	 */
	private final static String CUT_CODE = "c";
	/**
	 *  if the mutator's code contains this string it is one of the mutators with only
	 *  one possible text for example void
	 */
	private final static String ITSELF_CODE = "i";
	/**
	 *  this mutator code part allows distinguishing a double from an int
	 */
	private final static String DOUBLE_CODE = "d";
	/**
	 *  this is to set the title area image, it's static to be easily set 
	 *  when the wizard loads the default image
	 */
	public static Image image;
	
	// useful booleans to determine when the special cases are active
	private boolean decimal;
	private boolean doubleActive;
	
	// the text to add to the first page mutators list after pressing ok
	private String result;
	
	// int variables to compute the dialog size
	private int xSize = 0;
	private int ySize = 0;
	
	// validation tools
    private StringValidationToolkit valKit = null;
    private final IFieldErrorMessageHandler errorHandler;
	
	// widgets
	private Label leftLabel;
	private Label rightLabel;
	private ValidatingField<String> mutatorField;
	// reference for the text in mutatorField in order not to get and cast it a lot of times
	private Text text;
	
	// this int flag allows coordination between the field validator and the quick fixer
	private int quickFixerFlag;
	
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
		if(image != null) setTitleImage(image);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
   	     Composite composite = (Composite)super.createDialogArea(parent);
		 GridLayout layout = new GridLayout(6,true);
		 composite.setLayout(layout);
		 
		Label space = new Label(composite,SWT.NONE);
		space.setText("");
		GridDataFactory.swtDefaults().span(5,1).applyTo(space);
		
		/* this label will contains the required text in the left of the mutator, 
		 * for example " for string and (byte) for byte, it is dinamically chaged by
		 * the buttons listeners  
		 */	
		leftLabel = new Label(composite,SWT.NONE);
		leftLabel.setText("");
		leftLabel.setAlignment(SWT.RIGHT);
		GridDataFactory.fillDefaults().applyTo(leftLabel);
		
		createField(composite);  // create a text with validation of content
		
		// the same for the right side of the text 
		rightLabel = new Label(composite,SWT.NONE);
		rightLabel.setText("");
		GridDataFactory.fillDefaults().applyTo(rightLabel);
		
		// the name of the mutators will appear in the right of their radio buttons
		String[] mutators = {"void","null","empty","true","false","string","character",
			"int","double","byte","short","float","long"};		
		/*
		 *  this codes are used to set dynamically the texts of the left and right labels
		 *  and to create the buttons listeners
		 */
		String[] codes = {ITSELF_CODE , ITSELF_CODE , ITSELF_CODE , ITSELF_CODE,
			 ITSELF_CODE, "\"" + CUT_CODE + "\"", "'"+ CUT_CODE + "'" , CUT_CODE,
			 DOUBLE_CODE , "(byte)" + CUT_CODE , "(short)" + CUT_CODE, CUT_CODE + "f",CUT_CODE + "L"};
		
		GridDataFactory factory = GridDataFactory.swtDefaults().span(2,1);		
		/*
		 *  create the MyMutator objects to manage together the labels texts, 
		 *  the buttons listeners, the text validation and the quick fixers
		 */
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
    	// before return we must add the required syntax (the text of the labels)
    	// the .0 for the decimals if it is not set, and a \ before the number of a char
    	if(decimal && !result.contains(".")) result = result +".0";
    	if(leftLabel.getText().equalsIgnoreCase("\'") && result
    			.equalsIgnoreCase(result.replaceAll("[^0-9]",""))) result = "\\" + result;
    	result = leftLabel.getText() + result + rightLabel.getText();
    	super.okPressed();
    }
    public String getResult() { return result; }
    
    /**
     * inner class to manage together the requirements for a mutator type
     * this class contains the radio button to select this mutator type, when an
     * object of this class is created it is given the mutator name (the text for the button)
     * and a string with a code to create the listener for the button
     */
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
     /**
      *  this method creates and sets the listener for the button using the information in the 
      *  code string, the listener sets the text in the left and right labels, and controls
      *  the activation, validation and quick fixer of the field text
      */
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
			Button okButton = getButton(IDialogConstants.OK_ID);
			if(text == null) return true; 
	
            if(!text.isEnabled()) { 
            	okButton.setEnabled(true); return true; } // these mutators are always right
            
            String right = rightLabel.getText();
            
            if(right.contains("\"") || right.contains("\'")) { // char and String
            	okButton.setEnabled(true); return true; }
            
            if(right.contains("f") || doubleActive) { // the decimal ones
            	
            	if(sr.isEmpty()) {
            		message = "this mutator must not be empty";
            		 okButton.setEnabled(false); return false;
            	}
            	boolean bo = sr.replaceAll("[^0-9.]","").equalsIgnoreCase(sr);
            	if(!bo) {
            		message = "only real numbers are allowed for this mutator";
            		quickFixerFlag = 1;
            		 okButton.setEnabled(false); return false;
            	}
            	bo = !(sr.replaceAll("[^.]","").length() > 1); // no more than one point
            	if(bo) {okButton.setEnabled(true); return true; }
            	message = "the text contains more than one point";
            	quickFixerFlag = 2;
            	 okButton.setEnabled(false); return false;
            }
            
            // here only int, short and long are possibles
                if(sr.isEmpty()) {
                	message = "this muttator must not be empty";
                	 okButton.setEnabled(false); return false;
                }
            	boolean bo = sr.replaceAll("[^0-9]","").equalsIgnoreCase(sr);
            	if(bo) { okButton.setEnabled(true); return true; }
            	message = "only numeric characters are allolwed for this mutator";
            	quickFixerFlag = 3;
            	 okButton.setEnabled(false); return false;
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
			if(quickFixerFlag == 2) {  // remove the non numerical characters except the first point
				String target = text.getText();
				String begin = target.substring(0,target.indexOf(".")+1);
				String end = target.substring(target.indexOf(".")+1);
				end = end.replaceAll("[^0-9]","");
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
