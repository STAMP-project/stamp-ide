package eu.stamp.eclipse.plugin.dspot.controls;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.widgets.Display;

public abstract class SimpleController extends Controller {
	/**
	 * 
	 */
	protected final boolean projectDependent;
/**
 * 
 * @param projectDependent
 */
	public SimpleController(String key, String labelText, boolean checkButton,boolean projectDependent,int place,String tooltip) {
		super(key, labelText, checkButton,place,tooltip);
		this.projectDependent = projectDependent;
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
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
    /**
     * 
     * @param text
     */
	protected abstract void setText(String text);
}
