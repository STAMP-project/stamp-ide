
package eu.stamp.wp4.dspot.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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

import eu.stamp.wp4.dspot.wizard.json.DSpotTimeJSON.DSpotClassTime;
import eu.stamp.wp4.dspot.wizard.json.DSpotTestClassJSON;
import eu.stamp.wp4.dspot.wizard.json.DSpotTestClassJSON.TestCase;
import eu.stamp.wp4.dspot.wizard.json.DSpotTestClassJSON.TestCase.MutantKilled;
import eu.stamp.wp4.dspot.wizard.json.DSpotTimeJSON;
/**
 *  this class describes the DSpot properties view, this view is formed by a tree table to
 *  display the hierarchy of information in the JSON files produced by DSpot
 *
 */
public class DSpotView extends ViewPart {
	
	public static final String ID = "eu.stamp.wp4.dspot.view.DSpotView";

	private Tree tree;
	private TreeColumn keyTreeColumn;
    private TreeColumn valueTreeColumn;
	
	private boolean isDone = false;
	
	@Inject IWorkbench workbench;

	@PostConstruct
	public void createPartControl(Composite parent) {
		
		if(isDone) return;  // this avoids executing the method several times (producing several tables)
		
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

		isDone = true;
		
	}

	@Override
	public void setFocus() {}
    /**
     * this method takes the DSpot JSON files, read them using gson, process the information 
     * and display it in the DSpo properties view
     * @param jsonPath : the path of the DSpot times JSON (the other JSONs are in the same folder)
     * @throws IOException
     */
	public void parseJSON(String jsonPath) throws IOException {
		
		BufferedReader json = new BufferedReader(new FileReader(jsonPath));
		Gson gson = new Gson();
		
        DSpotTimeJSON dSpotTimeJSON = gson.fromJson(json, DSpotTimeJSON.class);
        json.close();
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
        
		File file = (new File(jsonPath)).getParentFile();
		String[] fileList = file.list();
		for(String sr : fileList) if(sr.contains("mutants_killed.json")) { System.out.println(file.getAbsolutePath()+"/"+sr);
			parseMutantsKilled(
				file.getAbsolutePath()+"/"+sr,gson);}

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
	/**
	 *  an instance of this class contains and manages the CompactTime objects of the test classes list
	 *
	 */
	private class CompactTimeList {
		
		private List<CompactTime> times;
		/**
		 * the CompactTimeList object is created from a list of DSpotTime objects
		 * transforming this list into a list of CompactTime objects	
		 * @param dSpotTimes : list of DSpotimes objects
		 */
		public CompactTimeList(List <DSpotClassTime> dSpotTimes) {
			times = new ArrayList<CompactTime>(1);
			times.add(new CompactTime(dSpotTimes.get(0).fullQualifiedName,
					String.valueOf(dSpotTimes.get(0).timeInMs)));
			for(int i = 1; i < dSpotTimes.size(); i++) updateList(dSpotTimes.get(i));
		}
		/**
		 * this method add the time of a DSpottTime Object to their corresponding CompactTime object
		 * in the list, if there is not such object, a new CompactTime is created 
		 * from the DSpotTime and added to the list
		 * @param dSpotTime : DSpotTime object
		 */
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
	/**
	 *  Instances of this class contain the name and the times list corresponding to a test class
	 *  in  the correct form to be displayed in the DSpotView's tree
	 *
	 */
	private class CompactTime {
        /**
         *  the complete name of the test class
         */
		private String fullQualifiedName;
		/**
		 *  the list of times corresponding to this class in a string, each time is followed by a comma
		 */
		private String timeInMs;

		public CompactTime(String fullQualifiedName,String timeInMs) {
			this.fullQualifiedName = fullQualifiedName;
			this.timeInMs = timeInMs;
		}
		/**
		 * this method add a time and their comma and space to the time list string
		 * @param time : the new time to add
		 */
		public void addTime(int time) {
			timeInMs = timeInMs + ", "+ String.valueOf(time);
		}
	}
	
	}
