package eu.stamp.eclipse.dspot.launch.configuration;

public class DSpotButtonsInformation {

private static DSpotButtonsInformation buttonsInfo; 

public boolean verbose;
public boolean nominimize;
public boolean comments;
public boolean clean;

private DSpotButtonsInformation() {
verbose = false;
nominimize = false;
comments = false;
clean = false;
}

   public static DSpotButtonsInformation getInstance() {
   if(buttonsInfo == null) buttonsInfo = new DSpotButtonsInformation();
   return buttonsInfo;
   }
   public String getButtonString() {
 if(!clean && !verbose && !nominimize && !comments) return "";
 String result = " ";
     if(clean) result = " --clean ";
     if(verbose) result = result + "--verbose ";
     if(nominimize) result = result + "--no-minimize ";
     if(comments) result = result + "--with-comment ";
 return result;
 }
public void reload(String configuration) {
verbose = configuration.contains("--verbose");
nominimize = configuration.contains("--no-minimize");
comments = configuration.contains("--with-comment");
clean = configuration.contains("--clean");
}
}
