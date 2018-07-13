package eu.stamp.wp4.dspot.view.tree;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import eu.stamp.wp4.dspot.wizard.json.DSpotTestClassJSON;
import eu.stamp.wp4.dspot.wizard.json.DSpotTimeJSON;
import eu.stamp.wp4.dspot.wizard.json.DSpotTestClassJSON.TestCase;
import eu.stamp.wp4.dspot.wizard.json.DSpotTestClassJSON.TestCase.MutantKilled;

public class PitMutantScoreSelectorReportsTree extends DSpotReportsTree{

	public PitMutantScoreSelectorReportsTree(DSpotTestClassJSON info) { 
		super(info,"Pit"); }

	@Override
	public void createTree(Tree tree, DSpotTimeJSON time) {
		
		TreeItem rootItem = new TreeItem(tree,SWT.NONE);
		rootItem.setText(0,"Test Class name : ");
		rootItem.setText(1,info.name);
		
		TreeItem item;
		
		if(time != null) {
	    item = new TreeItem(rootItem,SWT.NONE);
		item.setText(0, "Amplification time : ");
		item.setText(1, String.valueOf(time.getClassTime(info.name)) + " ms");
		}
		
		item = new TreeItem(rootItem,SWT.NONE);
		item.setText(0, "number of mutants killed originally : ");
		item.setText(1,String.valueOf(info.nbMutantKilledOriginally));
		
		item = new TreeItem(rootItem,SWT.NONE);
		item.setText(0, "number of original test cases : ");
		item.setText(1, String.valueOf(info.nbOriginalTestCases));
		
		item = new TreeItem(rootItem,SWT.NONE);
		item.setText(0, "test cases");
		List<TestCase> testCases = info.testCases;
		int i = 0;
		
		for(TestCase testCase : testCases) {
			
			i++;
			TreeItem testCaseItem = new TreeItem(item,SWT.NONE);
			testCaseItem.setText(0," test case : " + String.valueOf(i));
			
		    TreeItem subItem = new TreeItem(testCaseItem,SWT.NONE);
			subItem.setText(0,"name");
			subItem.setText(1,testCase.name);
			
			subItem = new TreeItem(testCaseItem,SWT.NONE);
			subItem.setText(0, "nbAssertionAdded");
			subItem.setText(1, String.valueOf(testCase.nbAssertionAdded));
			
			subItem = new TreeItem(testCaseItem,SWT.NONE);
			subItem.setText(0, "nbInputAdded");
			subItem.setText(1, String.valueOf(testCase.nbInputAdded));
			
			subItem = new TreeItem(testCaseItem,SWT.NONE);
			subItem.setText(0, "nbMutantKilled");
			subItem.setText(1, String.valueOf(testCase.nbMutantKilled));
			
			subItem = new TreeItem(testCaseItem,SWT.NONE);
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
		
	}
}
