package eu.stamp.wp4.dspot.wizard.json;

import java.util.List;
/**
 * this is the class to deserialize the DSpot times JSON
 *
 */
public class DSpotTimeJSON {
	public String projectName;
	public List<DSpotClassTime>	classTimes;
	
	public class DSpotClassTime {
		
		public String fullQualifiedName;
		public int timeInMs;
	}
}
