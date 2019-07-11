package eu.stamp.eclipse.plugin.dspot.controls;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.widgets.Display;

import eu.stamp.eclipse.plugin.dspot.context.ConfigurationManager;
import eu.stamp.eclipse.plugin.dspot.context.DSpotContext;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;
import eu.stamp.eclipse.plugin.dspot.properties.DSpotProperties;

public abstract class MultiController extends Controller{
    /**
     * 
     */
	protected final String project;
	/**
	 * 
	 */
	protected String[] content;
	
	protected MultiControllerProxy proxy;
	
	public MultiController(String key, String project, String labelText, boolean checkButton,int place,String tooltip,String[] content) {
		super(key, labelText, checkButton,place,tooltip);
		this.project = project;
		this.content = content;
	}
	
	public void setProxy(MultiControllerProxy proxy) { this.proxy = proxy; }
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
		            if(!attribute.contains(ConfigurationManager.LIST_SEPARATOR)
		            		&& !attribute.contains(DSpotProperties.getSeparator())) {
		            	setSelection(new String[]{attribute});
			            //setSelection(content);
			            if(proxy != null) {
			            	proxy.setTemporalData(attribute);
			            	proxy.save();
			            	//proxy.updateController(attribute);
			            }
			            DSpotMapping.getInstance().setValue(key,attribute);
		            	return;
		            }
		            String[] mySelection;
		            if(attribute.contains(ConfigurationManager.LIST_SEPARATOR))
		            mySelection = attribute.split(ConfigurationManager.LIST_SEPARATOR);
		            else mySelection = attribute.split(DSpotProperties.getSeparator());
		            setSelection(mySelection);
		            if(proxy != null) {
		            	proxy.setTemporalData(attribute);
		            	proxy.save();
		            	//proxy.updateController(attribute);
		            }
		            DSpotMapping.getInstance().setValue(key,attribute);
				}
			});
		} catch (CoreException e) {
			e.printStackTrace();
		}
		firstTime = false;
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
				//setSelection(null);
				//DSpotMapping.getInstance().setValue(key,null);
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException 
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	protected abstract void setContent(String[] content);
	
	protected abstract void setSelection(String[] selection);
}
