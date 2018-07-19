package eu.stamp.wp4.dspot.wizard.utils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

public class DSpotPropertiesFile {
   
	private static DSpotPropertiesFile dspotFile;
	
	private final String separator;
	private final String key;
	
	private DSpotPropertiesFile() {
		separator = "1f6l0nwq3";
		key = "dspotFileString";	
	}
	
	public static DSpotPropertiesFile getInstance(){
		if(dspotFile == null) dspotFile = new DSpotPropertiesFile();
		return dspotFile;
	}
	/*
	 *  File properties
	 */
    public String src;
    public String testSrc;
    public String javaVersion;
    public String outputDirectory;
    public String filter;
    
    public void reload(ILaunchConfiguration configuration) {
    	String string = "";
    	try {
			string = configuration.getAttribute(key,"");
		} catch (CoreException e) { e.printStackTrace(); }
    	if(!string.isEmpty())if(string.contains(separator)){
    		String[] strings = string.split(separator);
    		if(strings.length > 3) {
    		this.src = strings[0];
    		this.testSrc = strings[1];
    		this.javaVersion = strings[2];
    		this.outputDirectory = strings[3];
    		}
    		if(strings.length == 5) this.filter = strings[4];
    		else this.filter = "";
    	}
    }
      public ILaunchConfigurationWorkingCopy appendToConfiguration(
    		    ILaunchConfigurationWorkingCopy copy) {
    	  
    	  String info = src + separator + testSrc + separator
    			  + javaVersion + separator + outputDirectory + 
    			  separator + filter;
    	  copy.setAttribute(key, info);
    	  return copy;
      }
}
