package eu.stamp.eclipse.dspot.plugin.launch.info;

import java.util.LinkedList;
import java.util.List;

public class DSpotLaunchInfo {

	private List<DSpotParameter> parameters;
	
	public DSpotLaunchInfo() { 
		parameters = new LinkedList<DSpotParameter>();
	}
	
	public void addParameter(DSpotParameter parameter) {
		parameters.add(parameter);
	}
	
	public String getExecutionString() {
		String result = "";
		for(DSpotParameter parameter : parameters)
			result += parameter.getKey() + " " 
		+ parameter.getValue() + " ";
		return result;
	}
}
