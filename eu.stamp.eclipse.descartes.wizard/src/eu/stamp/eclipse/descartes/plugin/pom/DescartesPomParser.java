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
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.stamp.eclipse.descartes.plugin.profile.DescartesProfile;
/**
 * An instance of this class is responsible for creating
 * the file to be used as POM
 * @see eu.stamp.eclipse.descartes.plugin.pom.AbstractDescartesPomParser
 */
public class DescartesPomParser extends AbstractDescartesPomParser {
	
	private final String pomName;
	private final String profileID;
	
	public DescartesPomParser(String projectPath,String pomName,String profileID)
			throws ParserConfigurationException, SAXException, IOException {
		
		super(projectPath);
		this.pomName = pomName;
		this.profileID = profileID;
	}
	public void preparePom(List<String> mutators,List<String> outputFormats) {
		appendProfile(mutators,outputFormats);
		savePom();
	}
	/**
	 * writes the nodes tree to a xml file
	 * @param pomName : the name of the xml file to be write
	 */
	private void savePom() {
	    try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			File dir = new File(projectLocation);
			if(!dir.exists()) dir.mkdir();
			StreamResult result = new StreamResult(new FileWriter(projectLocation + "/" + pomName));
			DOMSource source = new DOMSource(document);
			transformer.transform(source, result);
		} catch (TransformerFactoryConfigurationError |IOException |
				TransformerException e) {
			e.printStackTrace();
		}
	}
	
	private void appendProfile(List<String> mutators, List<String> outputFormats) {
		
		DescartesProfile profile = new DescartesProfile();
		Node profileNode = profile.generateProfileNode(profileID, mutators, outputFormats,document);
	
	    NodeList profilesList = findNodeList("profiles",document.getDocumentElement());
	    
	    if(profilesList != null)if(profilesList.getLength() > 0) {
	    	profilesList.item(0).appendChild(profileNode);
	    	return;
	    }
	    
	    Node projectNode = findNodeList("project",root).item(0);
	    if(projectNode == null) projectNode = document.getDocumentElement();
	    Node profilesNode = document.createElement("profiles");
	    profilesNode.appendChild(profileNode);
	    projectNode.appendChild(profilesNode);
	    
	    // TODO case several projects
	}
}
