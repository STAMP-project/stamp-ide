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
		constructPomParser(jProject,"pom.xml");
	}
	
	public DescartesWizardPomParser(IJavaProject jProject, String pom) 
			throws ParserConfigurationException, SAXException, IOException {
		constructPomParser(jProject, pom);
		pomName = pom;
	}
	
	private void constructPomParser(IJavaProject jProject, String pom) 
			throws ParserConfigurationException, SAXException, IOException {
		
		projectPath = jProject.getProject().getLocation().toString();
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
		if(mutators.length < 1)createPomDescartesStructure();
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
			StreamResult result = new StreamResult(new FileWriter(projectPath+"/"+pomName));
			DOMSource source = new DOMSource(pomDocument);
			transformer.transform(source, result);
		} catch (TransformerFactoryConfigurationError |IOException |
				TransformerException e) {
			e.printStackTrace();
		}
	}
	/**
	 * this method takes the tree representation of the project's pom.xml 
	 * and checks if the mutators declaration in the project build is complete
	 * or not. 
	 * The hierarchy is build-plugins-plugin-configuration-mutators-mutator
	 * some of this element have anther child, 
	 * if this structure is partially complete the method completes it
	 */
	private void createPomDescartesStructure () {
		Node node = findBaseNode();
		NodeList nodeList = findNodeList("mutators",node);  // find the mutators node
		if(nodeList.item(0) == null) {                     // (this node list should have only one element)
	      nodeList = findNodeList("configuration",node);   // if there is not mutators declared look for the configuration node
	       if(nodeList.item(0) == null) {                 // if there is not a mutators node look for the plugin node
	    	   nodeList = findNodeList("plugin",node);    // and continue going up until you find an existing node
	    	   if(nodeList.item(0) == null) {             // in the worst case the build node should exist
	    		   nodeList = findNodeList("plugins",node);
	    		   if(nodeList.item(0) == null) {
	    			   nodeList = findNodeList("build",node);
	    			   if(nodeList.item(0) == null) {
	    				   structureFrom("build",node);        // now the else blocks corresponding to the if
	    			   } else structureFrom("plugins",nodeList.item(0)); // they contain what to do to complete the tree
	    		   } else structureFrom("plugin",nodeList.item(0));      // starting at each point
	    	   } else {    		   
	    		   if(lookForPitestPlugin(nodeList))structureFrom("configuration",nodeList.item(0));
	    		   else structureFrom("plugin",nodeList.item(0).getParentNode());
	    	   }
	        } else{
	        	if(thisPluginIsPitest(nodeList.item(0))) structureFrom("mutators",nodeList.item(0));
	        	else structureFrom("plugin",nodeList.item(0).getParentNode().getParentNode());
	        }
	       } else {
               Node element = pomDocument.createElement("mutator");
	    	   node.appendChild(element);
	    	   mutators = new Node[1];
	    	   mutators[0] = element;
	       }
	} // end of createPomDescartesStructure
	/**
	 * complete the tree structure fragment in whose bottom will be declared the mutators,
	 * from a start point the tree structure before the start point is assume to be complete
	 * the tree structure is a hierarchy of nodes some of them containing another nodes with
	 * required information as group ID or version ...
	 * @param name : the name of the starting level (the upper node in the fragment of the tree to build
	 * @param parentNode : the specific node to append the child structure
	 */
	private void structureFrom(String name,Node parentNode) {
		// the names of the parent nodes ordered parent-child-<child of the child>
		String[] names = {"build","plugins","plugin","configuration","mutators"}; 
		ArrayList<String> fragment = new ArrayList<String>(1);               
		boolean start = false;  
		for(String sr : names) { 
			if(name.equalsIgnoreCase(sr)) start = true; // if the string of this iteration is the given name start
			if(start)fragment.add(sr);
		}
		Element[] elements = new Element[fragment.size()]; // the array to contain the elements to be created
		for(int i = 0; i < elements.length; i++) {
			elements[i] = pomDocument.createElement(fragment.get(i)); // create the new elements
		}
		for(int i = 0; i < elements.length - 1; i++) { // this loop will add the extra childs ant the related information 
			elements[i].appendChild(elements[i+1]);    // corresponding to each node in the hierarchy
			if((((Node)elements[i]).getNodeName()).equalsIgnoreCase("plugin")){
				putNodeWithText("groupId",DescartesWizardConstants.PITEST_PLUGIN_ID,elements[i]);
                putNodeWithText("artifactId",DescartesWizardConstants.PITEST_ARTIFACT_ID,elements[i]);
				putNodeWithText("version","1.2.0",elements[i]);
				putNodeWithText("mutationEngine","descartes",elements[i]
						.getElementsByTagName("configuration").item(0));
				Node node = pomDocument.createElement("dependencies");
				Node child = pomDocument.createElement("dependency");
				putNodeWithText("groupId",DescartesWizardConstants.PITEST_DEPENDENCY_ID,child);
				putNodeWithText("artifactId",DescartesWizardConstants.PITEST_DEPENDENCY_ARTIFACT,child);
				putNodeWithText("version",DescartesWizardConstants.PITEST_DEPENDENCY_VERSION,child);
				node.appendChild(child);
				elements[i].appendChild(node);		
			}
		}
		Node mutator = pomDocument.createElement("mutator");
		elements[elements.length-1].appendChild(mutator);
		parentNode.appendChild(elements[0]);
		mutators = new Node[1];
		mutators[0] = mutator;
	}
	/**
	 * this method checks a NodeList looking for the pitest plugin
	 * @param pluginList : the NodeList to check for pitest
	 * @return : true if the pitest plugin is present
	 */
	private boolean lookForPitestPlugin(NodeList pluginList) {
		for(int i = 0; i < pluginList.getLength(); i++) {
			 Node node = ((Element)pluginList.item(i)).getElementsByTagName("groupId").item(0);
			 Node node2 = ((Element)pluginList.item(i)).getElementsByTagName("artifactId").item(0);
			 if(node != null) if(node.getTextContent().equalsIgnoreCase("org.pitest")) {
				 if(node2.getTextContent().equalsIgnoreCase("pitest-maven")) return true;
			 }
		}
		return false;
	}
	/**
	 * check if a given node corresponds to the pitest plugin
	 * @param configurationNode : the node to check
	 * @return : true if the node corresponds to pitest
	 */
	private boolean thisPluginIsPitest(Node configurationNode) {
		NodeList nodeList = configurationNode.getParentNode().getChildNodes();
		for(int i = 0; i < nodeList.getLength(); i++) {
			if(nodeList.item(i).getNodeName().equalsIgnoreCase("artifactId")) {
				if(nodeList.item(i).getTextContent()
						.equalsIgnoreCase(DescartesWizardConstants.PITEST_ARTIFACT_ID)) return true;	
			}
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
}
