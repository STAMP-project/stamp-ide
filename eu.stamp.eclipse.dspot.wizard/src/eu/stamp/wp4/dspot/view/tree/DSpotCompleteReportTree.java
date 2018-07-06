package eu.stamp.wp4.dspot.view.tree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.google.gson.Gson;

import eu.stamp.wp4.dspot.wizard.json.DSpotTestClassJSON;
import eu.stamp.wp4.dspot.wizard.json.DSpotTimeJSON;


public class DSpotCompleteReportTree { // TODO java-docs and comments

	private LinkedList<DSpotReportsTree> partialReportTrees = new LinkedList<DSpotReportsTree>();
	
	private TabFolder tabFolder;
	
	private DSpotTimeJSON time;
	
	//private List<Tree> trees;
	private final TreeManager treeManager;
	
	public DSpotCompleteReportTree (TabFolder tabFolder,String jsonFolderPath, List<Tree> trees) throws IOException {
		
		this.tabFolder = tabFolder;
		//this.trees = trees;
		treeManager = new TreeManager(); 
        
		File file = (new File(jsonFolderPath));  // the output folder 
		// get the files
	     ArrayList<String> fileList = new ArrayList<String>(Arrays.asList(file.list()));
        // only JSON
	    for(int i = 0; i < fileList.size(); i++)if(!fileList.get(i).contains(".json")) {
	    	fileList.remove(i); i--;
	    }
		// look for the time file and parseIt it
	    BufferedReader json = null;
	    String timeFile = "";
		for(int i = 0; i < fileList.size(); i++) {
			json = new BufferedReader(new FileReader(new File(jsonFolderPath + fileList.get(i))));
			json.readLine();
			if(json.readLine().contains("classTimes")) {
				timeFile = fileList.get(i);
				fileList.remove(i);
				break; 
				}
			}
		// parse time json
	    json = new BufferedReader(new FileReader(new File(jsonFolderPath+timeFile)));
	    Gson gson = new Gson();
	    this.time = gson.fromJson(json, DSpotTimeJSON.class);
	    
	    // generate the partial report trees
	    for(String name : fileList) createDSpotReportTree(jsonFolderPath,name,gson);
	}
	
	private void createDSpotReportTree(String folder,String file,Gson gson) 
			throws IOException {
		
		
		BufferedReader json = new BufferedReader(new FileReader(new File(folder + file)));
		DSpotTestClassJSON info = gson.fromJson(json, DSpotTestClassJSON.class);
		
		if(file.contains("jacoco")) {
			File txtReport = new File(folder);
			String[] names = txtReport.list();
			// find the jacoco txt report
			for(String name : names)if(name.contains(info.name) &&
					name.contains("report.txt") && name.contains("jacoco")) {
				txtReport = new File(folder + name); break;
			}
			// read the jacoco txt report to find the number of amplified tests
			int nbAmplifiedTest = 0;
			if(!txtReport.isDirectory()) {
			BufferedReader reader = new BufferedReader(new FileReader(txtReport));
			String line;
			
			while((line = reader.readLine()) != null) {
				if(line.contains("results with") && line.contains("amplified tests")) {
					  line = line.replaceAll("[^0-9]","");
					  //System.out.println(line); 
					  nbAmplifiedTest = Integer.parseInt(line);
					  break;
				}
			}
			reader.close();
			}
			partialReportTrees.add(new JacocoReportsTree(info,nbAmplifiedTest)); return;
		}
		
		partialReportTrees.add(new PitMutantScoreSelectorReportsTree(info));
	}
	
	public void createTree() {	
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() { // TODO
              for(DSpotReportsTree part : partialReportTrees) {
            	  if(!treeManager.isPresent(part.name)) {
            	  TabItem tabItem = new TabItem(tabFolder,SWT.BORDER);
            	  Composite composite = new Composite(tabFolder,SWT.NONE);
            	  composite.setLayout(new FillLayout());
            	  Tree tree = createBaseTree(composite);
            	  part.createTree(tree, time);
            	  tabItem.setText(part.name);
            	  treeManager.addTree(part.name, tree);
                  tabItem.setControl(composite);
            	  }
            	  else {
            		  Tree tree = treeManager.getTree(part.name);
            		  part.createTree(tree, time);
            	  }
              }
			}
		});
	}
	
	public List<Tree> getTrees(){ return treeManager.getTrees(); }
	
	private Tree createBaseTree(Composite parent) {
		
		Tree tree = new Tree(parent,SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		
		TreeColumn keyTreeColumn = new TreeColumn(tree,SWT.LEFT);
		keyTreeColumn.setText("key");
		keyTreeColumn.setWidth(100);
		
		TreeColumn valueTreeColumn = new TreeColumn(tree,SWT.CENTER);
		valueTreeColumn.setText("value");
		valueTreeColumn.setWidth(200);
		
		return tree;
	}
	 private class TreeManager{
		 
		 private LinkedList<TreeWithName> trees;
		 
		 public TreeManager() { trees = new LinkedList<TreeWithName>(); }
		 
		 public void addTree(String tabName,Tree tree) {
			 trees.add(new TreeWithName(tabName,tree));
		 }
		 
		 public Tree getTree(String tabName){
			 for(TreeWithName tree : trees)if(tree.tabName.equalsIgnoreCase(tabName))
             return tree.tree;
			 return null;
		 }
		 
		 public boolean isPresent(String tabName) {
			 for(TreeWithName tree : trees)if(tree.tabName.equalsIgnoreCase(tabName))
				 return true;
			 return false;
		 }
		 
		 public List<Tree> getTrees() {
			 LinkedList<Tree> result = new LinkedList<Tree>();
			 for(TreeWithName treeWithName : trees) result.add(treeWithName.tree);
			 return result;
		 }
		 
		 private class TreeWithName{
			 private Tree tree;
			 private String tabName;
			 private TreeWithName(String tabName,Tree tree) {
				 this.tabName = tabName; this.tree = tree;
			 }
		 }
	 }
}