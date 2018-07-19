package eu.stamp.wp4.dspot.wizard.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

public class DSpotPropertiesFile {
   
	private static DSpotPropertiesFile dspotFile;
	
	private final String separator;
	private final String key;
	
	private File file;
	
	private DSpotPropertiesFile() {
		separator = "1f6l0nwq3";
		key = "dspotFileString";
		file = null;
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
      public void writeTheFile(String projectPath,String configurationName) {
    	  configurationName.replaceAll(" ","_");
    	  File folder = new File(projectPath +"/dspot_properties_files/");
		  if(!folder.exists()) folder.mkdir();
		  file = new File(projectPath+"/dspot_properties_files/"
		  +configurationName+"_dspot.properties");
		  
		  try {
			file.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write("# DSpot properties file #");
			bw.newLine();
			bw.write("project="+projectPath);
			bw.newLine();
			bw.write("src="+src);
			bw.newLine();
			bw.write("testSrc="+testSrc);
			bw.newLine();
			bw.write("javaVersion="+javaVersion);
			bw.newLine();
			bw.write("outputDirectory="+outputDirectory);
			bw.newLine();
			bw.write("filter="+filter);
			bw.close();
		} catch (IOException e) { e.printStackTrace(); }
      }
      
      public boolean fileReady() { return file != null; }
      
      public String getFileLocation() { return file.getAbsolutePath(); }
}
