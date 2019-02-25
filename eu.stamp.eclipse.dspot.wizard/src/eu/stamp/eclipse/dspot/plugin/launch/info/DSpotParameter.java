package eu.stamp.eclipse.dspot.plugin.launch.info;

public class DSpotParameter {
 
	private final String key;
	
	private String value;
	
	public DSpotParameter(String key) { this.key = key; }
	
	public String getKey() { return key; }
	
	public String getValue() { return value; }
	
	public void setValue(String value) { this.value = value; }
}
