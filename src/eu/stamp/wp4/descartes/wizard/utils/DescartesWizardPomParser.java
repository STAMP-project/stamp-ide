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
public class DescartesWizardPomParser {
	
	private Document pomDocument;
	private String projectPath;
	private Node[] mutators;
	
	public DescartesWizardPomParser(IJavaProject jProject) 
			throws ParserConfigurationException, SAXException, IOException {
		
		projectPath = jProject.getProject().getLocation().toString();
		File pomFile = new File(projectPath + "/pom.xml");
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    pomDocument = builder.parse(pomFile);
	    NodeList mutatorsList = findNodeList("mutator",findBaseNode());
	    if(mutatorsList != null) { 
	    mutators = new Node[mutatorsList.getLength()];
		for(int i = 0; i < mutatorsList.getLength(); i++) mutators[i] = mutatorsList.item(i);
	    } 
	}
	
	public Node[] getMutators() {
		return mutators;
	}
	
	public void preparePom(String[] texts) {
		if(mutators.length < 1)createPomDescartesStructure();
		Node parent = mutators[0].getParentNode();
		if(mutators.length > 0) removeMutators(texts,parent);
		addMutators(texts,parent);
		savePom();
	}
	
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
	private void savePom() {
	    try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			StreamResult result = new StreamResult(new FileWriter(projectPath+"/descartes_pom.xml"));
			DOMSource source = new DOMSource(pomDocument);
			transformer.transform(source, result);
		} catch (TransformerFactoryConfigurationError |IOException |
				TransformerException e) {
			e.printStackTrace();
		}
	}
	private void createPomDescartesStructure () {
		Node node = findBaseNode();
		NodeList nodeList = findNodeList("mutators",node);
		if(nodeList.item(0) == null) {
	      nodeList = findNodeList("configuration",node);
	       if(nodeList.item(0) == null) {
	    	   nodeList = findNodeList("plugin",node);
	    	   if(nodeList.item(0) == null) {
	    		   nodeList = findNodeList("plugins",node);
	    		   if(nodeList.item(0) == null) {
	    			   nodeList = findNodeList("build",node);
	    			   if(nodeList.item(0) == null) {
	    				   structureFrom("build",node);
	    			   } else structureFrom("plugins",nodeList.item(0));
	    		   } else structureFrom("plugin",nodeList.item(0));
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
	private void structureFrom(String name,Node parentNode) {
		String[] names = {"build","plugins","plugin","configuration","mutators"};
		ArrayList<String> fragment = new ArrayList<String>(1);
		boolean start = false;
		for(String sr : names) { 
			if(name.equalsIgnoreCase(sr)) start = true;
			if(start)fragment.add(sr);
		}
		Element[] elements = new Element[fragment.size()];
		for(int i = 0; i < elements.length; i++) {
			elements[i] = pomDocument.createElement(fragment.get(i));
		}
		for(int i = 0; i < elements.length - 1; i++) {
			elements[i].appendChild(elements[i+1]);
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
	private void putNodeWithText(String name,String text, Node parent) {
		Node node = pomDocument.createElement(name);
		Node textNode = pomDocument.createTextNode(text);
		node.appendChild(textNode);
		parent.appendChild(node);
	}
}
