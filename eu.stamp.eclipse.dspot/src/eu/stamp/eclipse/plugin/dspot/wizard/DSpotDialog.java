
package eu.stamp.eclipse.plugin.dspot.wizard;

import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import eu.stamp.eclipse.plugin.dspot.controls.Controller;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;

/**
 * 
 */
public class DSpotDialog extends TitleAreaDialog {
	
	public static Image IMAGE;
	
	protected final String ID;

	private final String title;
	
	private final String message;
	
	public DSpotDialog(Shell shell,String title,String message,String ID) {
		super(shell);
		this.title = title;
		this.message = message;
		this.ID = ID;
	}
	
	@Override
	public void create() {
		super.create();
		setTitle(title);
		setMessage(message);
		if(IMAGE != null) setTitleImage(IMAGE);
	}
	
	@Override
	public Control createDialogArea(Composite parent) {
		
		Composite composite = (Composite)super.createDialogArea(parent);
		GridLayout layout = new GridLayout(3,true);
		composite.setLayout(layout);
		
		Label space = new Label(composite,SWT.NONE);
		space.setText("");
		GridDataFactory.fillDefaults().span(2,1).grab(true,false).applyTo(space);
		
		List<Controller> controllers = DSpotMapping.getInstance().getControllersList(ID);
		for(Controller controller : controllers) {
			if(controller instanceof IPageUserElement)((IPageUserElement)controller).setPage(this);
			controller.createControl(composite);
			controller.setListenerOn(false);
		}
	    return composite;
	}
	
	@Override
	public void okPressed() {
		List<Controller> controllers = DSpotMapping.getInstance().getControllersList(ID);
		for(Controller controller : controllers) {
			controller.setListenerOn(true);
			controller.notifyListener();
			controller.setListenerOn(false);
		}
		super.okPressed();
	}
	@Override
	public boolean isResizable() { return true; }
	/*
	public void update() {
		DSpotMapping map = DSpotMapping.getInstance();
		List<Controller> controllers = map.getControllersList(ID);
		for(Controller control : controllers) {
			String key = control.getKey();
			String value = map.getValue(key);
			control.updateController(value);
		}
	}*/
}