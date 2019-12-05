/*******************************************************************************
 * Copyright (c) 2019 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * 	Ricardo Jose Tejada Garcia (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.eclipse.plugin.dspot.json;

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
