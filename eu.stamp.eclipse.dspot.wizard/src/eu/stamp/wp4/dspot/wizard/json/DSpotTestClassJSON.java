package eu.stamp.wp4.dspot.wizard.json;

import java.util.List;

public class DSpotTestClassJSON {
	
	public int nbMutantKilledOriginally;
	public String name;
	public int nbOriginalTestCases;
	public List<TestCase> testCases;
	
	public class TestCase {
		
		public String name;
		public int nbAssertionAdded;
		public int nbInputAdded;
		public int nbMutantKilled;
		public List<MutantKilled> mutantsKilled;
		
		public class MutantKilled {
			public String ID;
			public int lineNumber;
			public String locationMethod;
		}
	}
}
