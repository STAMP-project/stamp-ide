package eu.stamp.eclipse.plugin.dspot.controls;

import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;

public class EnableCondition {
	
	private final boolean equal; // true means ==, false means !=
	
	private final String key,value;
	
	EnableCondition(String key,String value,boolean equal){
		this.key = key;
		this.value = value;
		this.equal = equal;
	}
	
	public boolean enable() {
       boolean result = DSpotMapping.getInstance().getValue(key)
    		   .equalsIgnoreCase(value);
       return equal == result;
	}

}
