package eu.stamp.wp4.dspot.dialogs;

public class DSpotAdvancedOptionsDialogMemory {

private int timeOut = 10000;   
private int randomSeed = 23;
private String[] selection = {""};
    private String pathPitResult = ""; 
    
   /*
    *    getter methods
    */
    public int getTimeOut() {
    return timeOut;
    }
    public int getRandomseed() {
    return randomSeed;
    }
    public String[] getSelection() {
    return selection;
    }
    public String getPathPitResult() {
    return pathPitResult;
    }
    /*
     *    Setter methods
     */
    public void setTimeout(int timeOut) {
    this.timeOut = timeOut;
    }
    public void setRandomSeed(int randomSeed) {
    this.randomSeed = randomSeed;
    }
    public void setSelection(String[] selection) {
    this.selection = selection;
    }
    public void setPathPitResult(String pathPitResult) {
    this.pathPitResult = pathPitResult;
    }
    public void setData(int timeOut,int randomSeed, String[] selection,String pathPitResult) {
    this.timeOut = timeOut;        this.randomSeed = randomSeed;
    this.selection = selection;    this.pathPitResult = pathPitResult;
    }
}
