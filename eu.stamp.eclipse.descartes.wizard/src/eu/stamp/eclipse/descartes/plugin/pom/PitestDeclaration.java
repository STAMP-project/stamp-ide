package eu.stamp.eclipse.descartes.plugin.pom;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import eu.stamp.wp4.descartes.wizard.utils.DescartesWizardConstants;

public class PitestDeclaration {
	
private final List<String> mutators;

private final List<String> outputFormats;

public PitestDeclaration(List<String> mutators, List<String> outputFormats) {
this.mutators = mutators;
this.outputFormats = outputFormats;
}

public void appendDeclaration(Node pluginsNode, Document document) {
	
	Node pluginNode = document.createElement("plugin");
	
	putNodeWithText("groupId",DescartesWizardConstants.PITEST_PLUGIN_ID,pluginNode,document);
    putNodeWithText("artifactId",DescartesWizardConstants.PITEST_ARTIFACT_ID,pluginNode,document);
    putNodeWithText("version",DescartesWizardConstants.PITEST_VERSION,pluginNode,document);
    
    Node dependenciesNode = document.createElement("dependencies");
    Node dependencyNode = document.createElement("dependency");
    putNodeWithText("groutId",DescartesWizardConstants.DESCARTES_ID,dependencyNode,document);
    putNodeWithText("artifactId",DescartesWizardConstants.DESCARTES_ARTIFACT,dependencyNode,document);
    putNodeWithText("version",DescartesWizardConstants.DESCARTES_VERSION,dependencyNode,document);
    dependenciesNode.appendChild(dependencyNode);
    pluginNode.appendChild(dependenciesNode);
    
    Node configurationNode = document.createElement("configuration");
    putNodeWithText("mutationEngine",DescartesWizardConstants.DESCARTES_ARTIFACT,configurationNode,document);
    
    if(mutators.size() > 0) {
    	Node mutatorsNode = document.createElement("mutators");
    	for(String mutator : mutators)
    		putNodeWithText("mutator",mutator,mutatorsNode,document);
    	configurationNode.appendChild(mutatorsNode);
    }
    
    if(outputFormats.size() > 0) {
    	Node formatsNode = document.createElement("outPutFormats");
    	for(String format : outputFormats)
    		putNodeWithText("value",format,formatsNode,document);
    	configurationNode.appendChild(formatsNode);
    }
	
    pluginNode.appendChild(configurationNode);
    pluginsNode.appendChild(pluginNode);
}

/**
 * create a text node with a name and a text and append it to the given parent node
 * @param name : name of the node text to be created
 * @param text : text to be contained by the new node text
 * @param parent : the node to append the new child
 */
private void putNodeWithText(String name,String text, Node parent,Document document) {
	Node node = document.createElement(name);
	Node textNode = document.createTextNode(text);
	node.appendChild(textNode);
	parent.appendChild(node);
}

}
