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

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * This class defines the common base for the classes 
 * DescartesPomParser and DescartesPomReader
 * @see eu.stamp.eclipse.descartes.plugin.pom.DescartesPomParser
 * @see eu.stamp.eclipse.descartes.plugin.pom.DescartesPomReader
 */
public abstract class AbstractDescartesPomParser {
	
	protected String projectLocation;
	
	protected Document document;
	
	protected Node root;
	
	public AbstractDescartesPomParser(String projectLocation) 
			throws ParserConfigurationException, SAXException, IOException {
		
		this.projectLocation = projectLocation;
		
		File file = new File(projectLocation + "/pom.xml");
		DocumentBuilder builder
		= DocumentBuilderFactory.newInstance().newDocumentBuilder();
		document = builder.parse(file);  // use DOM to parse the pom.xml
		
		root = findBaseNode();
	}
	
	public static String getTextContent(Node node) {
        Node textNode = getTextNode(node);
        if(textNode != null) return textNode.getNodeValue();
		return "";
	}
	
	public static void setTextContent(Node node, String text) {
		Node textNode = getTextNode(node);
		if(textNode != null) textNode.setNodeValue(text);
	}
	
	private static Node getTextNode(Node node) {
		if(node.getNodeName().equalsIgnoreCase("#text"))
			return node;
	    NodeList list = node.getChildNodes();
	    for(int i = 0; i < list.getLength(); i++) {
	    	Node result = getTextNode(list.item(i));
	    	if(result != null) return result;
	    }
		return null;
	}
	
	protected NodeList findNodeList(String name, Node node) {

		if(node != null) {
			return ((Element)node).getElementsByTagName(name);

}
		return null;
}
	
    private Node findBaseNode() {
		Element theRoot = document.getDocumentElement();
		NodeList rootNodes = theRoot.getChildNodes();

		
		for(int i = 0; i < rootNodes.getLength(); i++) {
	           Node aNode = rootNodes.item(i);
				if(aNode.getNodeType() == Node.ELEMENT_NODE && aNode.getNodeName().equalsIgnoreCase("build")) {
					return aNode;
				}
			}
		return (Node)theRoot;
    }
}
