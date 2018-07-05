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
			for(String name : names)if(name.contains("report.txt") && name.contains("jacoco")) {
				txtReport = new File(folder + name); break;
			}
			// read the jacoco txt report to find the number of amplified tests
			if(!txtReport.isDirectory()) {
			BufferedReader reader = new BufferedReader(new FileReader(txtReport));
			String line;
			while((line = reader.readLine()) != null) {
				if(line.contains("results with") && line.contains("amplified tests")) {
					  //line = line.substring(line.i, endIndex)    TODO                                                         // TODO
				}
			}
			}
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
