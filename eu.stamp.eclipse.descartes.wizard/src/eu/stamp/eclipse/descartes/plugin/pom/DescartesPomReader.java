/*******************************************************************************
 * Copyright (c) 2018 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Ricardo José Tejada García (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.eclipse.descartes.plugin.pom;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * An instance of this class is responsible for getting
 * the list of mutators declared in the project's POM
 * @see eu.stamp.eclipse.descartes.plugin.pom.AbstractDescartesPomParser
 */
public class DescartesPomReader extends AbstractDescartesPomParser{

	private String[] mutators;
	
	public DescartesPomReader(String projectLocation) 
			throws ParserConfigurationException, SAXException, IOException {
		super(projectLocation);
		NodeList list = findNodeList("mutator",root);
	   
		mutators = new String[list.getLength()];
		for(int i = 0; i < list.getLength(); i++)
			mutators[i] = getTextContent(list.item(i));
	}
	
	public String[] getMutators() { return mutators; }	
}
