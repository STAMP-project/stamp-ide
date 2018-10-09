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
