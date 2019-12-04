/*******************************************************************************
 * Copyright (c) 2019 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Ricardo Jose Tejada Garcia (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.eclipse.ramp.plugin.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;

import eu.stamp.eclipse.botsing.model.generation.launch.ModelGenerationJob;
import eu.stamp.eclipse.ramp.plugin.constants.RampLaunchConstants;
import eu.stamp.eclipse.ramp.plugin.constants.RampPluginConstants;

public class RampConfiguration {

	/**
	 * this map contains the key, value for the Evosuite launch properties
	 * with the structure key=value, for example Dseed_clone=0.5
	 */
	private Map<String,String> composeProperties;
	
	/**
	 * this map contains the key, value for the Evosuite launch properties
	 * with the structure key value, for example projectCP "ricardo/project/CP.txt"
	 */
	private Map<String,String> noEqualSignComposeProperties;
	
	/**
	 * this map contains the Evosuite properties that appears only like
	 * a single string (when they appear), for example generateMO
	 */
	private Set<String> simpleProperties;
	
	/**
	 * The job for generating models provided by the model generation wizard acessed by the link
	 * @see eu.stamp.eclipse.botsing.model.generation.wizard.ModelGenerationJob (in model generation plugin)
	 */
	private ModelGenerationJob modelGenerationJob;
	
	private IJavaProject project;
	
	public RampConfiguration(IJavaProject project) throws IOException {
		this.project = project;
		
		// initializing simple properties
		simpleProperties = new HashSet<String>();
		simpleProperties.add(RampLaunchConstants.MO_SUITE);
		
		// initializing no equal sign properties
		noEqualSignComposeProperties = new HashMap<String,String>();
		noEqualSignComposeProperties
		  .put(RampLaunchConstants.CLASS_PROPERTY,"");
		String projectLocation = project.getProject().getLocation().toString();
        BufferedReader reader = null;

			reader = new BufferedReader(new FileReader(
					new File(projectLocation + "/classpath.txt")));
			String classPath = reader.readLine();
			reader.close();
		classPath = processClassPath(classPath,new File(projectLocation));

		noEqualSignComposeProperties
		  .put(RampLaunchConstants.PROJECT_CP,classPath);
		
		// initializing composeProperties
		composeProperties = new HashMap<String,String>();
		composeProperties.put(RampLaunchConstants.ALGORITHM,"");
		composeProperties.put(RampLaunchConstants.SEARCH_BUDGET,"");
		composeProperties.put(RampLaunchConstants.SEED_CLONE,"");
		composeProperties.put(RampLaunchConstants.ONLINE_SEEDING,"");
		composeProperties.put(RampLaunchConstants.MODEL_PATH,"");
		composeProperties.put(RampLaunchConstants.TEST_DIR,"");
		composeProperties.put(RampLaunchConstants.REPORT_DIR,"");
		composeProperties.put(RampLaunchConstants.REPORT_DIR,"");
		composeProperties.put(RampLaunchConstants.NO_RUNTIME,"FALSE");
		composeProperties.put(RampLaunchConstants.SANDBOX,"FALSE");
		
		// setting default properties
	    URL defaultValuesUrl = FileLocator.find(
	    		Platform.getBundle(RampPluginConstants.EVOSUITE_PLUGIN_ID),
	    		new Path("files/default_values.properties"),null);
	    Properties defaultValuesProperties = new Properties();
	    try {
	    	defaultValuesProperties.load(defaultValuesUrl.openStream());
	    	Enumeration<Object> defaultValues = defaultValuesProperties.keys();
	        String defaultValueKey;
	    	while(defaultValues.hasMoreElements()) {
	        	defaultValueKey = (String)defaultValues.nextElement();
                setProperty(defaultValueKey,
                		defaultValuesProperties.getProperty(defaultValueKey));
	        }
	     
	    } catch(IOException e) {
	    	System.out.println("[ERROR] error in Evosuite plugin when loading default values");
	    	System.out.println(
	            "[ERROR] error in eu.stamp.eclipse.evosuite.plugin.job.EvosuiteConfiguration");
	    	e.printStackTrace();
	    }
	}
	
	// properties setter and getters
	
	/**
	 * @param key : the string key for the property
	 * @return the string value of the property, if it is a simple property the return string will be "true" or "false"
	 */
	public String getProperty(String key) {
		if(composeProperties.containsKey(key))return composeProperties.get(key);
		if(noEqualSignComposeProperties.containsKey(key))
			return noEqualSignComposeProperties.get(key);
		if(simpleProperties.contains(key)) return "true";
		return "false";
	}
	
	/**
	 * set a property
	 * @param key : property string key
	 * @param value : property string value, if it is a simple property use "true" or "false" strings
	 */
	public void setProperty(String key, String value) {
		if(composeProperties.containsKey(key)) composeProperties.put(key,value);
		else if(noEqualSignComposeProperties.containsKey(key))
			noEqualSignComposeProperties.put(key,value);
		else if(value.contains("rue")) simpleProperties.add(key);
		else simpleProperties.remove(key);
	}
	
	public IJavaProject getProject() {
		return project;
	}
	
	public void setProject(IJavaProject project) {
		this.project = project;
	}
	/**
	 * @return the job to be executed after wizard finish
	 */
	public RampJobQueue createJob() {
		if(modelGenerationJob == null) return new RampJobQueue(generateLaunchMap(),project.getElementName());
		return new RampJobQueue(modelGenerationJob,generateLaunchMap(),project.getElementName());
	}
	
	public void setModelGenerationJob(ModelGenerationJob modelGenerationJob) {
		this.modelGenerationJob = modelGenerationJob;
	}
	
	/**
	 * @return A string array with the launch strings for each class
	 */
	private Map<String,String> generateLaunchMap() {
		
		// create base string
		StringBuilder builder = new StringBuilder();
		for(String key : noEqualSignComposeProperties.keySet()) {
			if(key.equalsIgnoreCase(RampLaunchConstants.CLASS_PROPERTY)) {
				builder.append(key).append(" % ");
			} else {
			builder.append(key).append(' ')
			   .append(noEqualSignComposeProperties.get(key)).append(' ');
			}
		}
		for(String key : composeProperties.keySet())
			builder.append(key).append('=').append(composeProperties.get(key)).append(' ');
		for(String simpleProperty : simpleProperties)
			builder.append(simpleProperty).append(' ');
		String baseString = builder.toString();
		
		// separate classes
		String[] classes = noEqualSignComposeProperties
				.get(RampLaunchConstants.CLASS_PROPERTY).replaceAll(";",":")
				.replaceAll(",",":").split(":");
		
		Map<String,String> launchMap = new HashMap<String,String>();
         
		for(String clazz : classes)
             launchMap.put(clazz,baseString.replaceFirst("%",clazz));
		
		return launchMap;
	}
	
	private String processClassPath(String classPath,File rootFolder) {
		String separator = System.getProperty("path.separator");
		String[] fragments = classPath.split(separator);
		List<String> pathList = getPathsList(rootFolder);
		StringBuilder builder = new StringBuilder();
		for(String fragment : fragments)if(fragment != null && !fragment.isEmpty())
			if(checkExists(fragment,pathList)) {
				builder.append(fragment).append(separator);
			}
		
		return builder.toString();
	}
   
    private List<String> getPathsList(File rootFolder){
		List<File> files = fileSearch(rootFolder);
		List<String> filePaths = new ArrayList<String>(files.size());
		for(File file : files) {
			filePaths.add(file.getAbsolutePath());
		}
		return filePaths;
    }
	
	private boolean checkExists(String filePath, List<String> filePaths) {
		if(filePath.startsWith(".")) filePath = filePath.substring(1);
		for(String path : filePaths)if(matchStrings(path,filePath)) return true;
		return false;
	}
	
	private boolean matchStrings(String target, String sr) {
		if(!sr.contains("*")) return target.contains(sr);
			else {	
			String[] fragments = sr.split("\\*");
			for(String fragment : fragments)if(fragment != null) {
				if(!target.contains(fragment)) return false;
			}
			return true;
		}
	}
	
	private List<File> fileSearch(File rootFolder){
		return fileSearch(rootFolder,0);
	}
	
	private List<File> fileSearch(File rootFolder,int level){
		
		List<File> result = new LinkedList<File>();
		if(level > 1000) return result;
		
		File[] files = rootFolder.listFiles();
		for(File file : files)if(file.exists()) {
			if(file.isFile()) result.add(file);
			else{
				result.add(file); // folders are in the list
				result.addAll(fileSearch(file,level+1)); // recursive call
			}
		}
		return result;
	}

}
