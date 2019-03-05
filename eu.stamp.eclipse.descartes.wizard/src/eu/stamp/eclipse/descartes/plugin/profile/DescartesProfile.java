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
package eu.stamp.eclipse.descartes.plugin.profile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eu.stamp.wp4.descartes.wizard.utils.DescartesWizardConstants;
import eu.stamp.eclipse.descartes.plugin.pom.AbstractDescartesPomParser;
/**
 * An instance of this class is responsible for generating the Maven profile 
 * to execute Descartes,to do this it uses the template in files/descartes_profile.xml
 * @see files/descartes_profile.xml
 * @see eu.stamp.eclipse.descartes.plugin.pom.DescartesPomParser
 */
public class DescartesProfile {
	
	private Document profileDocument;
	
	public DescartesProfile() {
		
		final URL profileURL = FileLocator.find(Platform.getBundle(
				DescartesWizardConstants.DESCARTES_PLUGIN_ID),new Path("files/descartes_profile.xml"),null);
		try {
			InputStream stream = profileURL.openStream();
			DocumentBuilder builder
			= DocumentBuilderFactory.newInstance().newDocumentBuilder();
			profileDocument = builder.parse(stream); 
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		} 
	}
	
	public Node generateProfileNode(String profileID,List<String> mutators,List<String> formats,Document document) {
		
		DescartesInterDocumentNode interNode = 
				new DescartesInterDocumentNode(
						prepareProfileNode(profileID,mutators,formats));
		 return interNode.conversion(document);
	}
	
	private Node prepareProfileNode(String profileID,List<String> mutators,List<String> formats) {
		
		Element root = profileDocument.getDocumentElement();
		Node rootNode = root.getElementsByTagName("id").item(0);
		AbstractDescartesPomParser.setTextContent(rootNode,profileID);
		
		Node mutatorsNode = root.getElementsByTagName("mutators").item(0);
		for(String mutator : mutators)
			putNodeWithText("mutator",mutator,mutatorsNode);
	
		
		Node formatsNode = root.getElementsByTagName("outputFormats").item(0);
		for(String format : formats)
            putNodeWithText("value",format,formatsNode);
		
	    return root;
	}
	/**
	 * create a text node with a name and a text and append it to the given parent node
	 * @param name : name of the node text to be created
	 * @param text : text to be contained by the new node text
	 * @param parent : the node to append the new child
	 */
	private void putNodeWithText(String name,String text, Node parent) {
		Node node = profileDocument.createElement(name);
		Node textNode = profileDocument.createTextNode(text);
		node.appendChild(textNode);
		parent.appendChild(node);
	}
}
