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

item = new TreeItem(rootItem,SWT.NONE);
item.setText(0, "Number of original test cases : ");
item.setText(1,String.valueOf(info.nbOriginalTestCases));

if(time != null) {
    item = new TreeItem(rootItem,SWT.NONE);
item.setText(0, "Amplification time : ");
item.setText(1, String.valueOf(time.getClassTime(info.name)) + " ms");
}

item = new TreeItem(rootItem,SWT.NONE);
item.setText(0,"The original test suite kills : ");
item.setText(1,String.valueOf(info.nbMutantKilledOriginally)
+ " mutants");

List<TestCase> testCases = info.testCases; // Test cases
item = new TreeItem(rootItem,SWT.NONE);
item.setText(0, "The amplification results with : ");
item.setText(1, String.valueOf(testCases.size()) + " new tests");

if(testCases.size() == 0) return;  // in this case there are not cases

item = new TreeItem(rootItem,SWT.NONE);
item.setText(0, "New test cases");

for(TestCase testCase : testCases) {

TreeItem testCaseItem = new TreeItem(item,SWT.NONE);
testCaseItem.setText(0,"Test case : ");
testCaseItem.setText(1,testCase.name);

TreeItem subItem = new TreeItem(testCaseItem,SWT.NONE);
subItem.setText(0, "Number of added assertions : ");
subItem.setText(1, String.valueOf(testCase.nbAssertionAdded));

subItem = new TreeItem(testCaseItem,SWT.NONE);
subItem.setText(0, "Number of added inputs : ");
subItem.setText(1, String.valueOf(testCase.nbInputAdded));

subItem = new TreeItem(testCaseItem,SWT.NONE);
subItem.setText(0, "Number of mutants killed : ");
subItem.setText(1, String.valueOf(testCase.nbMutantKilled));

subItem = new TreeItem(testCaseItem,SWT.NONE);
subItem.setText(0, "Mutants killed");
List<MutantKilled> mutantsKilled = testCase.mutantsKilled;

for(MutantKilled mutant : mutantsKilled) {
TreeItem subSubItem = new TreeItem(subItem,SWT.NONE);
subSubItem.setText(0,"Id");
subSubItem.setText(1,mutant.ID);

subSubItem = new TreeItem(subItem,SWT.NONE);
subSubItem.setText(0,"Line number : ");
subSubItem.setText(1,String.valueOf(mutant.lineNumber));

subSubItem = new TreeItem(subItem,SWT.NONE);
subSubItem.setText(0,"Location method : ");
subSubItem.setText(1,mutant.locationMethod);
}

}

}
}
