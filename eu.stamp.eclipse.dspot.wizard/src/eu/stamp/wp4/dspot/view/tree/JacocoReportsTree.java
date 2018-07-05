package eu.stamp.wp4.dspot.view.tree;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import eu.stamp.wp4.dspot.wizard.json.DSpotTestClassJSON;
import eu.stamp.wp4.dspot.wizard.json.DSpotTimeJSON;

public class JacocoReportsTree extends DSpotReportsTree {

	public JacocoReportsTree(DSpotTestClassJSON info) { super(info); }

	@Override
	public void createTree(Tree tree,DSpotTimeJSON time) {
			
		TreeItem rootItem = new TreeItem(tree,SWT.NONE);
		rootItem.setText(0,"Test Class name : ");
		rootItem.setText(1,info.name);
		
		TreeItem item = new TreeItem(rootItem,SWT.NONE);
		item.setText(0, "Amplification time : ");
		item.setText(1, String.valueOf(time.getClassTime(info.name)) + " ms");
		
		item = new TreeItem(rootItem,SWT.NONE);
		item.setText(0,"Initial instruction coverage : "
				+ "\n initialInstructionCovered / initialInstructionTotal ");
		item.setText(1,info.percentageinitialInstructionCovered + " %");
		
		item = new TreeItem(rootItem,SWT.NONE);
		item.setText(0,"Amplified instruction coverage: "
				+ "\n amplifiedInstructionCovered / amplifiedInstructionTotal");
		item.setText(1, info.percentageamplifiedInstructionCovered + " %");
		
		item = new TreeItem(rootItem,SWT.NONE);
		item.setText("Amplified tests/original tests : "); //TODO 
	}	
}
