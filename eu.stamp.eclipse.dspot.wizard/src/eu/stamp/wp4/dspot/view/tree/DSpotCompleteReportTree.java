package eu.stamp.wp4.dspot.view.tree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.google.gson.Gson;

import eu.stamp.wp4.dspot.wizard.json.DSpotTestClassJSON;
import eu.stamp.wp4.dspot.wizard.json.DSpotTimeJSON;


public class DSpotCompleteReportTree { // TODO java-docs and comments

	private LinkedList<DSpotReportsTree> partialReportTrees = new LinkedList<DSpotReportsTree>();
	
	private Tree tree;
	
	private DSpotTimeJSON time;
	
	public DSpotCompleteReportTree (Tree tree,String jsonFolderPath) throws IOException {
		
		this.tree = tree;
        
		File file = (new File(jsonFolderPath));  // the output folder 
		// get the files
	     ArrayList<String> fileList = new ArrayList(Arrays.asList(file.list()));
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
	    for(String name : fileList) createDSpotReportTree(jsonFolderPath + name,gson);
	}
	
	private void createDSpotReportTree(String file,Gson gson) 
			throws FileNotFoundException {
		
		
		BufferedReader json = new BufferedReader(new FileReader(new File(file)));
		DSpotTestClassJSON info = gson.fromJson(json, DSpotTestClassJSON.class);
		
		if(file.contains("jacoco")) {
			partialReportTrees.add(new JacocoReportsTree(info)); return;
		}
		
		partialReportTrees.add(new PitMutantScoreSelectorReportsTree(info));
	}
	
	public void createTree() {	
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				TreeItem item;
              for(DSpotReportsTree part : partialReportTrees) {
            	  part.createTree(tree, time);
            	  item = new TreeItem(tree,SWT.NONE); // space
            	  item.setText("");
              }
			}
		});
	}
	
}
