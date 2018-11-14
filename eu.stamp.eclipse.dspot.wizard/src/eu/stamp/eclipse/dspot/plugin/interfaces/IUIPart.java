package eu.stamp.eclipse.dspot.plugin.interfaces;

import org.eclipse.swt.widgets.Composite;

public interface IUIPart extends IDSpotElement {
	
    public void createControl(Composite composite);
	
    public void setText(String text);
    
	public String getText();
	
}
