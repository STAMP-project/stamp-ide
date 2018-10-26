package eu.stamp.eclipse.dspot.test.detection;

public class ProjectSourceFolder {

	private final String path;
	
    private final boolean tests;
    
    public ProjectSourceFolder(String path,boolean tests) {
    	this.path = path;
    	this.tests = tests;
    }
    
    public String getPath() { return path; }
	
    public boolean containsTests() { return tests; }
}
