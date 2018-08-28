package eu.stamp.wp4.dspot.wizard.json;

import java.util.List;

public class DSpotTestClassJSON {

public String name;
public int nbOriginalTestCases;

// Pit score
public int nbMutantKilledOriginally;
public List<TestCase> testCases;

// Jacoco
public int initialInstructionCovered;
public int initialInstructionTotal;
public double percentageinitialInstructionCovered;
public int amplifiedInstructionCovered;
public int amplifiedInstructionTotal;
public double percentageamplifiedInstructionCovered; 

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
