package eu.stamp.eclipse.text.validation;

import com.richclientgui.toolbox.validation.validator.IFieldValidator;

public interface IObjectWithTextValidation {
	
	public String getName();

	public void setFieldValidator(IFieldValidator<String> validator);
}
