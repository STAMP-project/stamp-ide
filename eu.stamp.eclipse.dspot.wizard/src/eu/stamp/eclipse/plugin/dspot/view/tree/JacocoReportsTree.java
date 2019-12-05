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
package eu.stamp.eclipse.plugin.dspot.view.tree;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import eu.stamp.eclipse.plugin.dspot.json.DSpotTestClassJSON;
import eu.stamp.eclipse.plugin.dspot.json.DSpotTimeJSON;

public class JacocoReportsTree extends DSpotReportsTree {

private int amplifiedTests;

public JacocoReportsTree (DSpotTestClassJSON info,int amplifiedTests) {
super(info,"Jacoco"); this.amplifiedTests = amplifiedTests;
}

public JacocoReportsTree(DSpotTestClassJSON info) { 
super(info,"Jacoco"); this.amplifiedTests = 0; 
}

@Override
public void createTree(Tree tree,DSpotTimeJSON time) {

TreeItem rootItem = new TreeItem(tree,SWT.NONE);
rootItem.setText(0,"Test Class name : ");
rootItem.setText(1,info.name);

TreeItem item = new TreeItem(rootItem,SWT.NONE);
item.setText(0, "Amplification time : ");
item.setText(1, String.valueOf(time.getClassTime(info.name)) + " ms");

item = new TreeItem(rootItem,SWT.NONE);
item.setText(0,"Initial instruction coverage : ");
item.setText(1, info.initialInstructionCovered +"/"+ info.initialInstructionTotal +" ("+
String.format("%.2f",info.percentageinitialInstructionCovered) + " %)");

item = new TreeItem(rootItem,SWT.NONE);
item.setText(0,"Amplified instruction coverage: ");
item.setText(1, info.amplifiedInstructionCovered + "/" + info.amplifiedInstructionTotal +" ("+
String.format("%.2f",info.percentageamplifiedInstructionCovered) + " %)");

if(info.nbOriginalTestCases != 0) {
item = new TreeItem(rootItem,SWT.NONE);
item.setText(0,"Amplified tests/original tests : ");
item.setText(1,String.valueOf(String.valueOf(amplifiedTests) 
+ "/" + String.valueOf(info.nbOriginalTestCases)));
}
}
}
