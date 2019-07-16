package eu.stamp.eclipse.plugin.dspot.controls;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.widgets.Composite;

import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;

public class MultiControllerProxy extends MultiController implements IControllerProxy {

	protected final MultiController innerController;
	
	protected String data;
	
	protected String temporalData;
	
	public MultiControllerProxy(MultiController innerController) {
		super(null,null,null,false,0,null,null,null);
		this.innerController = innerController;
		this.activationDirection = innerController.activationDirection;
		this.place = innerController.place;
		innerController.setProxy(this);
	}
	
	@Override
	public void createControl(Composite parent) {
		DSpotMapping.getInstance().setValue(innerController.key,data);
		innerController.createControl(parent);
	}
	
	@Override
	protected void setContent(String[] content) {
		innerController.setContent(content);
	}
	
	@Override
	public void loadConfiguration(ILaunchConfiguration configuration) {
		innerController.loadConfiguration(configuration);
	}

	@Override
	protected void setSelection(String[] selection) {
		innerController.setSelection(selection);
	}

	@Override
	public void notifyListener() {
		innerController.notifyListener();
	}

	@Override
	public void setEnabled(boolean enabled) {
		innerController.setEnabled(enabled);
	}

	@Override
	public void updateController(String data) {
		innerController.updateController(data);
	}

	@Override
	public int checkActivation(String condition) {
		return innerController.checkActivation(condition);
	}

	@Override
	public void save() {
		data = temporalData;
		DSpotMapping.getInstance().setValue(innerController.key,data);
	}
	
	public void setTemporalData(String temporalData) {
		this.temporalData = temporalData;
	}
}
