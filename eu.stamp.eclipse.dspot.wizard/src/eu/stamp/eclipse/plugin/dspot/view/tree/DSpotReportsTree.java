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

import org.eclipse.swt.widgets.Tree;

import eu.stamp.eclipse.plugin.dspot.json.DSpotTestClassJSON;
import eu.stamp.eclipse.plugin.dspot.json.DSpotTimeJSON;

public abstract class DSpotReportsTree {

protected DSpotTestClassJSON info; // TODO put java-doc

public final String name;

public DSpotReportsTree(DSpotTestClassJSON info,String name) {
this.info = info;
this.name = name; 
}

/**
 *  this method must be called in the form Display.getDefafult().syncExec(new Runnable(){
 *  public void run() { DSpotReportsTree.createTree(tree,info,time); });
 */
public abstract void createTree(Tree tree,DSpotTimeJSON time);
}
