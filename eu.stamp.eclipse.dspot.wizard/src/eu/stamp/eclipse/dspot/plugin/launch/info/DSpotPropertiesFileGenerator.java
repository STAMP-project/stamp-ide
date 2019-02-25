package eu.stamp.eclipse.dspot.plugin.launch.info;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class DSpotPropertiesFileGenerator {

	List<DSpotParameter> parameters;
	
	public DSpotPropertiesFileGenerator() { 
		parameters = new LinkedList<DSpotParameter>();
	}
	
	public void addParameter(DSpotParameter parameter) {
		parameters.add(parameter);
	}
	
	public void writeFile(File file) {
		
		// TODO
	}
	
}
