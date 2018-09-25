/*******************************************************************************
 * Copyright (c) 2018 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Ricardo Jose Tejada Garcia (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.wp4.descartes.wizard.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.internal.Workbench;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Text;

@SuppressWarnings("restriction")
/**
 * an instance of this class contains the necessary information and methods 
 * to get the tree representation of the pom.xml, check the pitest plugin and mutators declarations,
 * complete the necessary pom structure to run Descartes with a certain mutators selection
 * and write the final tree to a new xml file with the given name into the project's folder
 */
public class DescartesWizardPomParser {
	
	private Document pomDocument;
	private String pomName;
	private String projectPath;
	private Node[] mutators;
	
	public DescartesWizardPomParser(IJavaProject jProject) 
			throws ParserConfigurationException, SAXException, IOException {
		this(jProject,"pom.xml");
	}
	
	public DescartesWizardPomParser(IJavaProject jProject, String pom) 
			throws ParserConfigurationException, SAXException, IOException {
		projectPath = jProject.getProject().getLocation().toString();
		constructPomParser(pom);
		pomName = pom;
	}
	
	public DescartesWizardPomParser(String projectLocation,String pom) throws ParserConfigurationException, SAXException, IOException {
		this.projectPath = projectLocation;
		constructPomParser(pom);
	}
	
	private void constructPomParser(String pom) 
			throws ParserConfigurationException, SAXException, IOException {
		
		File pomFile = new File(projectPath +"/"+ pom);  // the pom file in every maven project
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    pomDocument = builder.parse(pomFile);  // use DOM to parse the pom.xml
	    NodeList mutatorsList = findNodeList("mutator",findBaseNode()); // look for the mutators
	    if(mutatorsList != null) { 
	    mutators = new Node[mutatorsList.getLength()];
		for(int i = 0; i < mutatorsList.getLength(); i++) mutators[i] = mutatorsList.item(i);
	    } 
	}
	/**
	 * @return the array with the mutators nodes
	 */
	public Node[] getMutators() {
		return mutators;
	}
	public String getPomName() {
		return pomName;
	}
    /**
     * this method prepares the pom tree to be write to a xml file making the necessary method calls
     * after this invokes the save method to write the file
     * @param texts : the text of all the mutators that will be declared in the pom
     * @param pomName : the name of the file projectPath/pomName.xml
     */
	public void preparePom(String[] texts,String pomName) {
		if(mutators.length < 1) createPitestPluginTree();     //createPomDescartesStructure();
		Node parent = mutators[0].getParentNode();
		if(mutators.length > 0) removeMutators(texts,parent);
		addMutators(texts,parent);
		savePom(pomName);
	}
    /**
     * this method obtains the selected java project when the wizard is tigered 
     * @return the current java project 
     */
	public static IJavaProject obtainProject() {
		
		ISelectionService selectionService  = Workbench.getInstance().getActiveWorkbenchWindow()
				.getSelectionService();
		ISelection selection = selectionService.getSelection();
		IJavaProject jproject = null;
		Object element;

		if (selection instanceof IStructuredSelection) {
			element = ((IStructuredSelection) selection).getFirstElement();

			if (element instanceof IJavaElement) {
				jproject = ((IJavaElement) element).getJavaProject();
				return jproject;
			}
			if (element instanceof IProject) {

				IProject pro = (IProject) element;
				jproject = new JavaProject(pro, null);
				return jproject;

			}
		}
		if(jproject == null) {
		
			selection = selectionService.getSelection("org.eclipse.jdt.ui.PackageExplorer");
			
			if (selection instanceof IStructuredSelection) {
				element = ((IStructuredSelection) selection).getFirstElement();

				if (element instanceof IJavaElement) {
					jproject = ((IJavaElement) element).getJavaProject();
					return jproject;
				}}
            selection = selectionService.getSelection("org.eclipse.ui.navigator.ProjectExplorer");
			
			if (selection instanceof IStructuredSelection) {
				element = ((IStructuredSelection) selection).getFirstElement();

				if (element instanceof IProject) {
					IProject pro = (IProject) element;
					jproject = new JavaProject(pro, null);
					return jproject;
				}}
		}
		return null;
	}
    private Node findBaseNode() {
		Element theRoot = pomDocument.getDocumentElement();
		NodeList rootNodes = theRoot.getChildNodes();

		
		for(int i = 0; i < rootNodes.getLength(); i++) {
	           Node aNode = rootNodes.item(i);
				if(aNode.getNodeType() == Node.ELEMENT_NODE && aNode.getNodeName().equalsIgnoreCase("build")) {
					return aNode;
				}
			}
		return (Node)theRoot;
    }
	private NodeList findNodeList(String name, Node node) {

			if(node != null) {
				return ((Element)node).getElementsByTagName(name);

	}
			return null;
	}
	/**
	 * check the mutators list and remove those mutator nodes whose text is not in the array
	 * @param texts : the array with the texts of the nodes to stay in the list
	 * @param parent : the parent node of the nodes to be removed
	 */
	private void removeMutators(String[] texts,Node parent) {
		ArrayList<Node> mutatorsList = new ArrayList<Node>(Arrays.asList(mutators));
		boolean removeThis = true;
		for(int i = 0; i < mutatorsList.size(); i++) {
			for(String sr : texts) if(mutatorsList.get(i).getTextContent().equalsIgnoreCase(sr)) {
				removeThis = false; break;
			}
			if(removeThis) {
				parent.removeChild(mutatorsList.get(i));
			}
			removeThis = true;
		}
	}
	/**
	 * for each text of the array adds a child text node to the parent node if the parent hasn't a child containing this text
	 * @param texts : when the method finish there will be a node containing each of the text in the array 
	 * @param parent : the node to append the child nodes
	 */
	private void addMutators(String[] texts,Node parent) {
		ArrayList<Node> mutatorsList = new ArrayList<Node>(1);
		if(mutators.length > 0) mutatorsList = new ArrayList<Node>(Arrays.asList(mutators));
		boolean addThis = true;
		for(int i = 0; i < texts.length; i++) {
			if(mutators.length > 0) for(int j = 0; j < mutatorsList.size(); j++) if(texts[i]
					.equalsIgnoreCase(mutatorsList.get(j).getTextContent())) { 
				addThis = false; break;
			}
			if(addThis) {
				Element element = pomDocument.createElement("mutator");
				Text elementText = pomDocument.createTextNode(texts[i]);
				element.appendChild(elementText);
			    parent.appendChild(element);
			}
			addThis = true;
		}
	}
	/**
	 * writes the nodes tree to a xml file
	 * @param pomName : the name of the xml file to be write
	 */
	private void savePom(String pomName) {
	    try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			File dir = new File(projectPath);
			if(!dir.exists()) dir.mkdir();
			StreamResult result = new StreamResult(new FileWriter(projectPath + "/" + pomName));
			DOMSource source = new DOMSource(pomDocument);
			transformer.transform(source, result);
		} catch (TransformerFactoryConfigurationError |IOException |
				TransformerException e) {
			e.printStackTrace();
		}
	}
	/**
	 * check if a given node corresponds to the pitest plugin
	 * @param configurationNode : the node to check
	 * @return : true if the node corresponds to pitest
	 */
	private boolean thisPluginIsPitest(Node configurationNode) {
		NodeList nodeList = configurationNode.getParentNode().getChildNodes();
		for(int i = 0; i < nodeList.getLength(); i++) {
				if(nodeList.item(i).getTextContent()
						.contains(DescartesWizardConstants.PITEST_ARTIFACT_ID)) return true;	
		}
		return false;
	}
	/**
	 * create a text node with a name and a text and append it to the given parent node
	 * @param name : name of the node text to be created
	 * @param text : text to be contained by the new node text
	 * @param parent : the node to append the new child
	 */
	private void putNodeWithText(String name,String text, Node parent) {
		Node node = pomDocument.createElement(name);
		Node textNode = pomDocument.createTextNode(text);
		node.appendChild(textNode);
		parent.appendChild(node);
	}
		/**
	     * creates the declaration of the Pitest plugin with the necessary
	     * configuration for Descartes execution and replaces the pitest declaration in the tree
	     * that will be used to create the Descartes pom
		 */
		private void createPitestPluginTree(){
			Node parent = findBaseNode();
			Node pitestTree = pomDocument.createElement("plugin");
			
			/*
			 *   create pitest plugin tree
			 */
			
			eliminateRepetitiveDeclarations();
			
			Node dependenciesNode = pomDocument.createElement("dependencies");
			Node dependencyNode = pomDocument.createElement("dependency");
			putNodeWithText("groupId",DescartesWizardConstants.PITEST_DEPENDENCY_ID,dependencyNode);
			putNodeWithText("artifactId",DescartesWizardConstants.PITEST_DEPENDENCY_ARTIFACT,dependencyNode);
			putNodeWithText("version",DescartesWizardConstants.PITEST_DEPENDENCY_VERSION,dependencyNode);
			
			dependenciesNode.appendChild(dependencyNode);
			pitestTree.appendChild(dependenciesNode);
			
			Node configurationNode = pomDocument.createElement("configuration");
			putNodeWithText("mutationEngine","descartes",configurationNode);

				Node mutatorsNode = pomDocument.createElement("mutators");
				putNodeWithText("mutator","",mutatorsNode);
				configurationNode.appendChild(mutatorsNode);
				
				mutators = new Node[1]; mutators[0] = mutatorsNode.getFirstChild();
			
			pitestTree.appendChild(configurationNode);
			
			putNodeWithText("groupId",DescartesWizardConstants.PITEST_PLUGIN_ID,pitestTree);
			putNodeWithText("artifactId",DescartesWizardConstants.PITEST_ARTIFACT_ID,pitestTree);
			putNodeWithText("version",DescartesWizardConstants.PITEST_VERSION,pitestTree);
			
			/*
			 *   look for the pitest plugin in the DOM tree
			 */
		    NodeList list = findNodeList("plugin", parent);
		
		    if(list.item(0) != null) {
			
		    Node node = list.item(0).getParentNode();
	
		    for(int i = 0; i < list.getLength(); i++) {
		    	if(thisPluginIsPitest(list.item(i))) {  // if it's pit remove the plugin 
			 node.removeChild(list.item(i)); break; }}  // to put our declaration
		
		 // put our declaration of the pitest plugin in the tree
		   node.appendChild(pitestTree); return;  
		    }
		
		   Node buildNode = pomDocument.createElement("build");
		   Node pluginsNode = pomDocument.createElement("plugins");
		   pluginsNode.appendChild(pitestTree);
		
		   list = findNodeList("build",parent);
		
		   if(list.item(0) != null) {
			list.item(0).appendChild(pluginsNode); return;
		 }
		
		  buildNode.appendChild(pluginsNode);
		  parent.appendChild(buildNode);
		
	  }
		private void eliminateRepetitiveDeclarations() {
			
         NodeList nodeList = pomDocument.getElementsByTagName("artifactId");
         
		 for(int i = 0; i < nodeList.getLength(); i++) {
			 String text = nodeList.item(i).getTextContent();
			 if(text.equalsIgnoreCase(
					 DescartesWizardConstants.PITEST_DEPENDENCY_ARTIFACT) ||
			 text.equalsIgnoreCase(DescartesWizardConstants.PITEST_ARTIFACT_ID))
				 pomDocument.removeChild(nodeList.item(i).getParentNode());
		 }
		 }
	}
