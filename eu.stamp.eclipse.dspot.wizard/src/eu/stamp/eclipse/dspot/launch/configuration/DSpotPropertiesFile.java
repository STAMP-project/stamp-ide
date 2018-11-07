package eu.stamp.eclipse.dspot.launch.configuration;

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

public static final String PROJECT_NAME_KEY = "projectName";

private File file;

private DSpotPropertiesFile() {
separator = ";";
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
    public String projectPath;
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
    this.projectPath = strings[0];
    this.src = strings[1];
    this.testSrc = strings[2];
    this.javaVersion = strings[3];
    this.outputDirectory = strings[4];
    if(strings.length > 5) this.filter = strings[5];
    else this.filter = "";
    }
    }
      public ILaunchConfigurationWorkingCopy appendToConfiguration(
        ILaunchConfigurationWorkingCopy copy) {
      
      String info = projectPath + separator + src + separator + testSrc 
      + separator + javaVersion + separator + outputDirectory + 
      separator + filter;
      copy.setAttribute(key, info);
      
      info = projectPath;
      if(info.contains("\\")) info = info.replaceAll("\\","/");
      info = info.substring(info.lastIndexOf("/")+1);
      copy.setAttribute(PROJECT_NAME_KEY, info);
      
      return copy;
      }
      public void writeTheFile(String configurationName) {
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
