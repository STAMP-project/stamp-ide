
package eu.stamp.wp4.dspot.view;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;

import eu.stamp.wp4.dspot.view.tree.DSpotCompleteReportTree;


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
		
		if(tabFolder.getItemCount() > 0)if( !tabFolder.getItem(0).isDisposed()) {
			TabItem[] items = tabFolder.getItems();
			for(TabItem item : items) item.dispose();
		}
		
		if(trees == null) trees = new LinkedList<Tree>();
		if(!trees.isEmpty())if(!trees.get(0).isDisposed())
			for(Tree tree : trees) tree.removeAll();
		DSpotCompleteReportTree completeReport = new DSpotCompleteReportTree(
				tabFolder,jsonFolderPath,trees);
		completeReport.createTree();
		trees = completeReport.getTrees();

	}
	
}
