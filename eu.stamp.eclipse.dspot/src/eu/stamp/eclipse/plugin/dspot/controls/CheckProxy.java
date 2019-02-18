package eu.stamp.eclipse.plugin.dspot.controls;

import eu.stamp.eclipse.dspot.controls.impl.CheckController;

public class CheckProxy extends CheckController implements IControllerProxy {

	protected final CheckController innerController;
	
	protected String data;
	
	protected String temporalData;
	
	public CheckProxy(CheckController innerController) {
		super(null,null,0,null,null,null);
		this.innerController = innerController;
		this.activationDirection = innerController.activationDirection;
		innerController.setProxy(this);
	}
	@Override
	public void save() {
		data = temporalData;
		innerController.updateController(data);
	}
	// TODO overrides
	
	 
}