
package eu.stamp.wp4.dspot.view;

//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
//import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;

//import com.google.gson.Gson;

//import java.util.Arrays;
//import eu.stamp.wp4.dspot.wizard.json.DSpotTimeJSON.DSpotClassTime;
import eu.stamp.wp4.dspot.view.tree.DSpotCompleteReportTree;
//import eu.stamp.wp4.dspot.wizard.json.DSpotTestClassJSON;
//import eu.stamp.wp4.dspot.wizard.json.DSpotTestClassJSON.TestCase;
//import eu.stamp.wp4.dspot.wizard.json.DSpotTestClassJSON.TestCase.MutantKilled;
//import eu.stamp.wp4.dspot.wizard.json.DSpotTimeJSON;

public class DSpotView extends ViewPart {
	
	public static final String ID = "eu.stamp.wp4.dspot.view.DSpotView";
    
	private TabFolder tabFolder;
	
	private List<Tree> trees;

	@Inject IWorkbench workbench;

	@Override
	public void createPartControl(Composite parent) {
		
		GridLayoutFactory.fillDefaults().applyTo(parent);
		
		tabFolder = new TabFolder(parent,SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(tabFolder);
	}

	@Override
	public void setFocus() {}
    
	public void parseJSON(String jsonFolderPath) throws IOException {
		
		if(trees == null) trees = new LinkedList<Tree>();
		if(!trees.isEmpty()) for(Tree tree : trees) tree.removeAll();
		DSpotCompleteReportTree completeReport = new DSpotCompleteReportTree(
				tabFolder,jsonFolderPath,trees);
		completeReport.createTree();
		trees = completeReport.getTrees();

	}
	
}
