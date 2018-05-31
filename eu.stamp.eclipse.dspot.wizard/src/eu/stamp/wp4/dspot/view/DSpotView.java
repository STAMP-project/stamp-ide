
package eu.stamp.wp4.dspot.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;

import com.google.gson.Gson;

import java.util.Arrays;
import eu.stamp.wp4.dspot.wizard.json.DSpotTimeJSON.DSpotClassTime;
import eu.stamp.wp4.dspot.wizard.json.DSpotTestClassJSON;
import eu.stamp.wp4.dspot.wizard.json.DSpotTestClassJSON.TestCase;
import eu.stamp.wp4.dspot.wizard.json.DSpotTestClassJSON.TestCase.MutantKilled;
import eu.stamp.wp4.dspot.wizard.json.DSpotTimeJSON;

public class DSpotView extends ViewPart {
	
	public static final String ID = "eu.stamp.wp4.dspot.view.DSpotView";

	private Tree tree;
	private TreeColumn keyTreeColumn;
    private TreeColumn valueTreeColumn;
	
	@Inject IWorkbench workbench;

	@Override
	public void createPartControl(Composite parent) {
		
		GridLayoutFactory.fillDefaults().applyTo(parent);
		
		tree = new Tree(parent,SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(tree);
		
		keyTreeColumn = new TreeColumn(tree,SWT.LEFT);
		keyTreeColumn.setText("key");
		keyTreeColumn.setWidth(50);
		
		valueTreeColumn = new TreeColumn(tree,SWT.CENTER);
		valueTreeColumn.setText("value");
		valueTreeColumn.setWidth(50);
	}

	@Override
	public void setFocus() {}
    
	public void parseJSON(String jsonPath) throws IOException {
		
		File file = (new File(jsonPath));  // the output folder 
		
		// looking for the times JSON
		List<String> fileList = new LinkedList<String>(Arrays.asList(file.list()));
		String timeFile = "";
		for(int i = 0; i < fileList.size(); i++)if(!fileList.get(i).contains(".json")) {
			fileList.remove(i); i--; }
		BufferedReader json = null;
		for(int i = 0; i < fileList.size(); i++) {
			json = new BufferedReader(new FileReader(new File(jsonPath + fileList.get(i))));
			json.readLine();
			if(json.readLine().contains("classTimes")) {
				timeFile = fileList.get(i);
				fileList.remove(i);
				break;
			}
		}
		String[] files = fileList.toArray(new String[fileList.size()]);
		BufferedReader json2 = new BufferedReader(new FileReader(new File(jsonPath+timeFile)));
		Gson gson = new Gson();
		
        DSpotTimeJSON dSpotTimeJSON = gson.fromJson(json2, DSpotTimeJSON.class);
        json.close();
        json2.close();
        CompactTimeList times = new CompactTimeList(dSpotTimeJSON.classTimes);
        List<CompactTime> timesList = times.getTimes();
        Display.getDefault().syncExec(new Runnable() {
        	@Override
        	public void run() {
        		
        		tree.removeAll();
        		
        		TreeItem item = new TreeItem(tree,SWT.NONE);
        		item.setText("class times");
        		TreeItem subItem;
        		
        		for(CompactTime time : timesList) {
        			subItem =  new TreeItem(item,SWT.NONE);
        			subItem.setText(0, time.fullQualifiedName);
        			subItem.setText(1,time.timeInMs + " ms");
        		}
        		
        		item = new TreeItem(tree,SWT.NONE);
        		item.setText("");
        	}
        });
        
		for(String sr : files) { System.out.println(jsonPath+sr);
			parseMutantsKilled(
				jsonPath+sr,gson);}

        }
	
	private void parseMutantsKilled(String path, Gson gson) throws IOException {
		BufferedReader json = new BufferedReader(new FileReader(path));
		DSpotTestClassJSON dSpotJSON = gson.fromJson(json, DSpotTestClassJSON.class);
		json.close();
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				
				TreeItem item = new TreeItem(tree,SWT.NONE);
				item.setText(0, "name");
				item.setText(1,dSpotJSON.name);
				
				item = new TreeItem(tree,SWT.NONE);
				item.setText(0, "nbMutantKilledOriginally");
				item.setText(1,String.valueOf(dSpotJSON.nbMutantKilledOriginally));
				
				item = new TreeItem(tree,SWT.NONE);
				item.setText(0, "nbOriginalTestCases");
				item.setText(1,String.valueOf(dSpotJSON.nbOriginalTestCases));
				
				item = new TreeItem(tree,SWT.NONE);
				item.setText(0, "test cases");
				List<TestCase> testCases = dSpotJSON.testCases;
				
				for(TestCase testCase : testCases) {
					TreeItem subItem = new TreeItem(item,SWT.NONE);
					subItem.setText(0,"name");
					subItem.setText(1,testCase.name);
					
					subItem = new TreeItem(item,SWT.NONE);
					subItem.setText(0, "nbAssertionAdded");
					subItem.setText(1, String.valueOf(testCase.nbAssertionAdded));
					
					subItem = new TreeItem(item,SWT.NONE);
					subItem.setText(0, "nbInputAdded");
					subItem.setText(1, String.valueOf(testCase.nbInputAdded));
					
					subItem = new TreeItem(item,SWT.NONE);
					subItem.setText(0, "nbMutantKilled");
					subItem.setText(1, String.valueOf(testCase.nbMutantKilled));
					
					subItem = new TreeItem(item,SWT.NONE);
					subItem.setText(0, "mutants killed");
					List<MutantKilled> mutantsKilled = testCase.mutantsKilled;
					
					for(MutantKilled mutant : mutantsKilled) {
						TreeItem subSubItem = new TreeItem(subItem,SWT.NONE);
						subSubItem.setText(0,"ID");
						subSubItem.setText(1,mutant.ID);
						
						subSubItem = new TreeItem(subItem,SWT.NONE);
						subSubItem.setText(0,"lineNumber");
						subSubItem.setText(1,String.valueOf(mutant.lineNumber));
						
						subSubItem = new TreeItem(subItem,SWT.NONE);
						subSubItem.setText(0,"locationMethod");
						subSubItem.setText(1,mutant.locationMethod);
					}
					
				}
				
				  item = new TreeItem(tree,SWT.NONE);
				  item.setText(0, "");
			}
		});
		
	}
	
	private class CompactTimeList {
		
		private List<CompactTime> times;
		
		public CompactTimeList(List <DSpotClassTime> dSpotTimes) {
			times = new ArrayList<CompactTime>(1);
			times.add(new CompactTime(dSpotTimes.get(0).fullQualifiedName,
					String.valueOf(dSpotTimes.get(0).timeInMs)));
			for(int i = 1; i < dSpotTimes.size(); i++) updateList(dSpotTimes.get(i));
		}
		
		private void updateList(DSpotClassTime dSpotTime) {
			for(int i = 0; i < times.size(); i++  ) if(times.get(i).fullQualifiedName
					.equalsIgnoreCase(dSpotTime.fullQualifiedName)) {
					times.get(i).addTime(dSpotTime.timeInMs); 
					return;
					}
				times.add(new CompactTime(dSpotTime.fullQualifiedName,
					String.valueOf(dSpotTime.timeInMs)));
		}
		
		public List<CompactTime> getTimes() { return times; }	
		
	}
	
	private class CompactTime {
		
		private String fullQualifiedName;
		private String timeInMs;
		
		public CompactTime(String fullQualifiedName,String timeInMs) {
			this.fullQualifiedName = fullQualifiedName;
			this.timeInMs = timeInMs;
		}
		
		public void addTime(int time) {
			timeInMs = timeInMs + ", "+ String.valueOf(time);
		}
	}
	
	}
