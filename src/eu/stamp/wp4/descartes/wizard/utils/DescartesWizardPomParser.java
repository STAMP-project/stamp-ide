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
	private String pomFilePath;
	private Node[] mutators;
	private final String[] defaultMutators = {"void","null","true","false","empty","0","1",
			"(byte)0","(byte)1","(short)1","(short)2","0L","1L","0.0","1.0","0.0f","1.0f",
			"'\\40'","'A'","\"\"","\"A\""};
	
	
	public DescartesWizardPomParser(IJavaProject jProject) 
			throws ParserConfigurationException, SAXException, IOException {
		
		pomFilePath = jProject.getProject().getLocation().toString() + "/pom.xml";
		File pomFile = new File(pomFilePath);
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    pomDocument = builder.parse(pomFile);	
	    NodeList mutatorsList = findMutators();
	    mutators = new Node[mutatorsList.getLength()];
		for(int i = 0; i < mutatorsList.getLength(); i++) mutators[i] = mutatorsList.item(i);
	}
	
	public Node[] getMutators() {
		return mutators;
	}
	
	public void preparePom(String[] texts) {
		removeMutators(texts);
		addMutators(texts);
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
	private NodeList findMutators() {
		Element theRoot = pomDocument.getDocumentElement();
		NodeList rootNodes = theRoot.getChildNodes();
		
		Node myNode = null;
		
		for(int i = 0; i < rootNodes.getLength(); i++) {
	           Node aNode = rootNodes.item(i);
				if(aNode.getNodeType() == Node.ELEMENT_NODE && aNode.getNodeName().equalsIgnoreCase("build")) {
					myNode = aNode; break;
				}
			}
			if(myNode != null) {
				rootNodes = ((Element)myNode).getElementsByTagName("mutator");
				return rootNodes;
	}
			return null;
	}
	
	private void removeMutators(String[] texts) {
		ArrayList<Node> mutatorsList = new ArrayList<Node>(Arrays.asList(mutators));
		boolean removeThis = true;
		for(int i = 0; i < mutatorsList.size(); i++) {
			for(String sr : texts) if(mutatorsList.get(i).getTextContent().equalsIgnoreCase(sr)) {
				removeThis = false; break;
			}
			if(removeThis) {
				mutatorsList.get(0).getParentNode().removeChild(mutatorsList.get(i));
			}
			removeThis = true;
		}
	}
	private void addMutators(String[] texts) {
		ArrayList<Node> mutatorsList = new ArrayList<Node>(Arrays.asList(mutators));
		boolean addThis = true;
		for(int i = 0; i < texts.length; i++) {
			for(int j = 0; j < mutatorsList.size(); j++) if(texts[i]
					.equalsIgnoreCase(mutatorsList.get(j).getTextContent())) { 
				addThis = false; break;
			}
			if(addThis) {
				Element element = pomDocument.createElement("mutator");
				Text elementText = pomDocument.createTextNode(texts[i]);
				element.appendChild(elementText);
			    mutators[0].getParentNode().appendChild(element);
			}
			addThis = true;
		}
	}
	private void savePom() {
	    try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			StreamResult result = new StreamResult(new FileWriter(pomFilePath));
			DOMSource source = new DOMSource(pomDocument);
			transformer.transform(source, result);
		} catch (TransformerFactoryConfigurationError |IOException |
				TransformerException e) {
			e.printStackTrace();
		}
	}
}
