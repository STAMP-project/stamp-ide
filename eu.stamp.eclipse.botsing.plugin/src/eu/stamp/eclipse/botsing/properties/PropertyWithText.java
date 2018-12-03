package eu.stamp.eclipse.botsing.properties;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.richclientgui.toolbox.validation.ValidatingField;
import com.richclientgui.toolbox.validation.string.StringValidationToolkit;
import com.richclientgui.toolbox.validation.validator.IFieldValidator;

import eu.stamp.eclipse.text.validation.IObjectWithTextValidation;
import eu.stamp.eclipse.text.validation.TextFieldValidatorFactory;


public abstract class PropertyWithText 
         extends AbstractBotsingProperty implements IObjectWithTextValidation {

	protected Text text;
	
	private ValidatingField<String> valField;
	
	protected IFieldValidator<String> validator;
	
	protected StringValidationToolkit kit;
	
	protected int numColumns;
	
	protected boolean isReadOnly;
	
    public PropertyWithText(String defaultValue, String key, 
			String name, boolean compulsory, boolean isLaunchInfo,
			boolean isReadOnly,StringValidationToolkit kit) {
		super(defaultValue, key, name, compulsory, isLaunchInfo);
		this.kit = kit;
		this.isReadOnly = isReadOnly;
		numColumns = 1;
    }
    
    @Override
    public void setFieldValidator(IFieldValidator<String> validator) {
    	this.validator = validator;
    }
    
    @Override
    public void createControl(Composite composite) {
    	
    	if(validator == null) 
    		TextFieldValidatorFactory.getFactory().notEmpty(
    		TextFieldValidatorFactory.ERROR).applyTo(this); // default
    	
    	super.createControl(composite);
    	
    	if(isReadOnly && !compulsory)
    		text = new Text(composite,SWT.BORDER | SWT.READ_ONLY);
    	else {
    	valField = kit.createTextField(composite,validator,
    			compulsory,defaultValue);
    	text = (Text)valField.getControl();
    	text.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
            public void keyReleased(KeyEvent e) {
				setData(text.getText());
			}
    	});
    	}
    	GridDataFactory.fillDefaults().span(numColumns,1)
    	                     .grab(true, false).applyTo(text);
    	text.setText(data);
    }
    
  public boolean ok() { 
	  if(valField == null) return true;
	  return valField.isValid(); 
	  }
  
  @Override
  public String getName() { return name; }
}
