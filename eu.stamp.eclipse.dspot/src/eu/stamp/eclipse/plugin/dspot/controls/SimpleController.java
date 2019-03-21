package eu.stamp.eclipse.plugin.dspot.controls;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.widgets.Display;

import eu.stamp.eclipse.dspot.controls.impl.SimpleControllerProxy;

public abstract class SimpleController extends Controller {
	/**
	 * 
	 */
	protected final boolean projectDependent;
	
	protected SimpleControllerProxy proxy;
/**
 * 
 * @param projectDependent
 */
	public SimpleController(String key, String labelText, boolean checkButton,boolean projectDependent,int place,String tooltip) {
		super(key, labelText, checkButton,place,tooltip);
		this.projectDependent = projectDependent;
	}
	
	public void setProxy(SimpleControllerProxy proxy) {
		this.proxy = proxy;
	}

	@Override
	public void loadConfiguration(ILaunchConfiguration configuration) {
		try {
			String data = configuration.getAttribute(key,"");
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
				   setText(data);
				}
			});
			setData(data);
			if(proxy != null) {
				proxy.setTemporalData(data);
				proxy.save();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		firstTime = false;
	}
    /**
     * 
     * @param text
     */
	public abstract void setText(String text);
}
