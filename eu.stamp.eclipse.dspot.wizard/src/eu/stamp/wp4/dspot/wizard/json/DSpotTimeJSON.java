package eu.stamp.wp4.dspot.wizard.json;

import java.util.List;

public class DSpotTimeJSON {
	public String projectName;
	public List<DSpotClassTime>	classTimes;
	
	public class DSpotClassTime {
		
		public String fullQualifiedName;
		public int timeInMs;
	}
}
