/*******************************************************************************
 * Copyright (c) 2019 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * 	Ricardo Jose Tejada Garcia (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/

package eu.stamp.eclipse.plugin.dspot.processing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import eu.stamp.eclipse.plugin.dspot.context.DSpotContext;
import eu.stamp.eclipse.plugin.dspot.controls.Controller;
import eu.stamp.eclipse.plugin.dspot.properties.DSpotProperties;
/**
 * 
 */
public class DSpotMapping {
	/**
	 * 
	 */
	private static DSpotMapping INSTANCE;
	/**
	 * 
	 */
	private final Map<String,String> fileParameters;
	/**
	 * 
	 */
	private final Map<String,String> parameters;
	/**
	 * 
	 */
	private final Map<String,Map<String,Controller>> controllers;
	/**
	 * 
	 */
	private String configurationName;
	/*
	 * 
	 */
	private String pathToProperties;
	
	private DSpotMapping() {
		parameters = new HashMap<String,String>();
		fileParameters = new HashMap<String,String>();
		controllers = new HashMap<String,Map<String,Controller>>();
	    configurationName = "new_configuration";
	}
	
	public void setPathToProperties(String pathToProperties) {
		this.pathToProperties = pathToProperties;
	}
	
	public String getConfigurationName() { return configurationName; }
	
	public void setConfigurationName(String configurationName) {
		this.configurationName = configurationName;
	}
	/**
	 * 
	 */
	public static DSpotMapping getInstance() {
		if(INSTANCE == null) INSTANCE = new DSpotMapping();
		return INSTANCE;
	}
	/**
	 * 
	 */
	public static void reset() {
		INSTANCE = null;
	}
	
	public void prepareConfiguration(ILaunchConfigurationWorkingCopy copy) {
		copy.setAttribute(DSpotProperties.PROJECT_KEY,DSpotContext.getInstance()
				.getProject().getElementName());
		for(String key : fileParameters.keySet()) {
			String value = fileParameters.get(key);
			  if(value != null && !value.isEmpty())
				  copy.setAttribute(key,value);
		}
		for(String key : parameters.keySet()) {
			String value = parameters.get(key);
			  if(value != null && !value.isEmpty())
			      copy.setAttribute(key,value);
		}
	}
	
	/**
	 * 
	 * @param direction 
	 * @return 
	 */
	public Controller getController(String direction) {
		if(!direction.contains("/")) {
			System.out.println("ERROR bad direction");
			return null;
		}
		String[] parts = direction.split("/");
		Map<String,Controller> map = controllers.get(parts[0]);
		return map.get(parts[1]);
	}
	
	public String getValue(String key) {
		if(fileParameters.containsKey(key)) return fileParameters.get(key);
		return parameters.get(key);
	}
	
	public void setController(Controller controller,String direction) {
		if(!direction.contains("/")) {
			System.out.println("ERROR setting controller, bad direction");
			return;
		}
		String[] parts = direction.split("/");
		if(controllers.containsKey(parts[0])) {
			controllers.get(parts[0]).put(parts[1],controller);
		} else {
			Map<String,Controller> map = new HashMap<String,Controller>();
			map.put(parts[1],controller);
			controllers.put(parts[0],map);
		}
	}
	/**
	 * 
	 * @param key
	 */
	public void setFileParameter(String key) {
       fileParameters.put(key,null);
	}
	
    public void setCommandParameter(String key) {
    	parameters.put(key,null);
    }
	/**
	 * 
	 * @param page 
	 * @return 
	 */
	public List<Controller> getControllersList(String page) {
	    Map<String,Controller> map = controllers.get(page);
	    Collection<Controller> values = map.values();
	    List<Controller> result = new ArrayList<Controller>(values);
	    Collections.sort(result);
	    return result;
	}
	/**
	 * 
	 * @return 
	 */
	public String[] getFileStrings() {
		
		if(fileParameters.containsKey("src") && fileParameters.get("src") == null)
		fileParameters.put("src",DSpotContext.getInstance().getNoTestSourceFolders()[0]);
	    if(fileParameters.containsKey("testSrc") && fileParameters.get("testSrc") == null)
		fileParameters.put("testSrc",DSpotContext.getInstance().getTestSourceFolders()[0]);
	    
	    
	    List<String> resultList = new ArrayList<String>(fileParameters.keySet().size());
	    String value;
	    for(String key : fileParameters.keySet()) {
	    	value = fileParameters.get(key);
	    	if(value != null && !value.isEmpty()) resultList.add(key + "=" + value);
	    }
	    String result[] = new String[resultList.size()];
	    for(int i = 0; i < result.length; i++) result[i] = resultList.get(i);
		
		/*Set<String> set = fileParameters.keySet();
		String[] keys = set.toArray(new String[set.size()]);
		String[] result = new String[keys.length];
		for(int i = 0; i < keys.length; i++)
			result[i] = keys[i] + "=" + fileParameters.get(keys[i]);*/
		return result;
	}
	/**
	 * 
	 * @return 
	 */
	public String getCommand() {
		String result = " -Dpath-to-properties="+pathToProperties;
	    Set<String> set = parameters.keySet();
	    String value;
		for(String k : set)if((value = parameters.get(k)) != null)
	    if(!value.isEmpty()) {
			if(k.contains(DSpotProperties.CHECK_EXTRA_KEY)) {
				k = k.replaceAll(DSpotProperties.CHECK_EXTRA_KEY,"");
				if(value.contains("rue")) result += " -D" + k;
			}
			else result += " -D" + k + "=" + parameters.get(k);
		}
		return result;
	}
	/**
	 * 
	 * @param key 
	 * @param value 
	 */
	public void setValue(String key, String value) {
		if(fileParameters.containsKey(key))
			     fileParameters.put(key,value);
		else if(parameters.containsKey(key))
		       parameters.put(key,value);
		else System.out.println("ERROR non registered parameter");
	}
	/**
	 * 
	 * @return 
	 */
	public List<Controller> getAllControllers() {
		List<Controller> result = new LinkedList<Controller>();
	    Collection<Map<String,Controller>> col = controllers.values();
	    for(Map<String,Controller> map : col) {
	    	Collection<Controller> controls = map.values();
	    	for(Controller control : controls) result.add(control);
	    }
	    return result;
	}
}