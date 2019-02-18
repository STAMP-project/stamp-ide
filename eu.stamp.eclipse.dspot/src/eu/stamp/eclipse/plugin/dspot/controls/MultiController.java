package eu.stamp.eclipse.plugin.dspot.controls;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.widgets.Display;

import eu.stamp.eclipse.plugin.dspot.context.ConfigurationManager;
import eu.stamp.eclipse.plugin.dspot.context.DSpotContext;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;

public abstract class MultiController extends Controller{
    /**
     * 
     */
	protected final String project;
	/**
	 * 
	 */
	protected final String[] content;
	
	public MultiController(String key, String project, String labelText, boolean checkButton,int place,String tooltip,String[] content) {
		super(key, labelText, checkButton,place,tooltip);
		this.project = project;
		this.content = content;
	}
	/**
	 * 
	 */
	public void loadConfiguration(ILaunchConfiguration configuration) {
		try {
			String attribute = configuration.getAttribute(key,"");
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
		            if(attribute == null) {
		            	return;
		            }
		            if(!attribute.contains(ConfigurationManager.LIST_SEPARATOR)) {
		            	setSelection(new String[]{attribute});
		            	return;
		            }
		            String[] content = attribute.split(ConfigurationManager.LIST_SEPARATOR);
		            setSelection(content);
				}
			});
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
    /**
     * 
     */
	public void loadProject() {
		if(project == null) return;
		if(project.isEmpty()) return;
		DSpotContext context = DSpotContext.getInstance();
		try {
			Method method = context.getClass().getMethod("get" + project);
			Object ob = method.invoke(context);
			if(ob instanceof String[]) {
				String[] content = (String[])ob;
				setContent(content);
				setSelection(null);
				DSpotMapping.getInstance().setValue(key,null);
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException 
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	protected abstract void setContent(String[] content);
	
	protected abstract void setSelection(String[] selection);
}
