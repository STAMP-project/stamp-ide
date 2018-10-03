package eu.stamp.eclipse.descartes.plugin.pom;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DescartesPomReader extends AbstractDescartesPomParser{

	private String[] mutators;
	
	public DescartesPomReader(String projectLocation) 
			throws ParserConfigurationException, SAXException, IOException {
		super(projectLocation);
		NodeList list = findNodeList("mutator",root);
		mutators = new String[list.getLength()];
		for(int i = 0; i < list.getLength(); i++)
			mutators[i] = list.item(i).getTextContent();
	}
	
	public String[] getMutators() { return mutators; }	
}
