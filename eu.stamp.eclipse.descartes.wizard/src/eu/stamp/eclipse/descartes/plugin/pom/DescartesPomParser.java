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

import eu.stamp.wp4.descartes.wizard.utils.DescartesWizardConstants;

public class DescartesPomParser extends AbstractDescartesPomParser {
	
	private final String pomName;
	
	public DescartesPomParser(String projectPath,String pomName)
			throws ParserConfigurationException, SAXException, IOException {
		
		super(projectPath);
		this.pomName = pomName;
	}
	public void preparePom(List<String> mutators,List<String> outputFormats) {
		removePit();
		createPitDeclaration(mutators,outputFormats);
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
	
	private void createPitDeclaration(List<String> mutators,List<String> outputFormats) {
		PitestDeclaration declaration = 
				new PitestDeclaration(mutators,outputFormats);
		NodeList pluginsList = findNodeList("plugins",root);
		// TODO case plugins doesn't exist
		declaration.appendDeclaration(pluginsList.item(0), document);
	}
	
	private void removePit() {
		NodeList pluginList = findNodeList("plugin",root);
		if(pluginList != null)if(pluginList.getLength() > 0) {
			Node pitNode = getPitNode(pluginList);
			if(pitNode != null)
				pitNode.getParentNode().removeChild(pitNode);
		}
	}
	
	private Node getPitNode(NodeList pluginList) {
		
        for(int i = 0; i < pluginList.getLength(); i++) {
        	NodeList groupList = findNodeList("groupId",pluginList.item(i));
            if(examine(groupList,DescartesWizardConstants.PITEST_PLUGIN_ID)) {
            	NodeList artifactList = findNodeList("artifactId",pluginList.item(i));
            	if(examine(artifactList,DescartesWizardConstants.PITEST_ARTIFACT_ID))
            		return pluginList.item(i);
            }
        }	
		return null;
	}
	private boolean examine(NodeList list,String text) {
		if(list == null) return false;
		if(list.getLength() < 1) return false;
		
		for(int i = 0; i < list.getLength(); i++)
			if(list.item(i).getTextContent().equalsIgnoreCase(text))
				return true;
		
		return false;
	}
}
