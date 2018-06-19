package eu.stamp.wp4.descartes.wizard.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import eu.stamp.wp4.descartes.view.DescartesView;
/**
 *  an instance of this class stores the file objects of the html documents in a folder system
 *  and shows them in an Eclipse view formed by several tabs 
 *  with a browser to display one document in each tab
 */
public class DescartesHtmlManager {
	/**
	 *  the html summaries produced by Descartes
	 */
	private File[] htmls;
	/**
	 * a DescartesHtmlManager object is created from a parent folder finding the htmls inside
	 * @param outputFolder : the parent folder of the file system where the html are stored
	 */
	public DescartesHtmlManager(String outputFolder) {
		htmls = findHtmls(new File(outputFolder));
	}
	/**
	 * opens an Eclipse web browser in a tab item for each html
	 */
	public void openBrowsers() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				String[] urls = new String[htmls.length];
				try {
				for(int i = 0; i < htmls.length; i++) 
					urls[i] = htmls[i].toURI().toURL().toString();
				IWorkbench work = PlatformUI.getWorkbench();
				IWorkbenchWindow wi = work.getActiveWorkbenchWindow();
				IWorkbenchPage page = wi.getActivePage();
				DescartesView viw = (DescartesView) page.showView(DescartesWizardConstants.DESCARTES_VIEW_ID);
				viw.setUrls(urls);
				 }catch (MalformedURLException | PartInitException e) {
					e.printStackTrace();
				}
			}
		});

	}
	/**
	 * finds the Descartes html files
	 * @param folder : the folder that contains the system of folders with the html files
	 * @return an array with all the html files in the system of folders inside the parent folder
	 */
    private File[] findHtmls(File folder) {
    	File[] files = folder.listFiles();
    	ArrayList<File> result = new ArrayList<File>(1);
    	for(File file : files) {
    		if(!file.isDirectory() && file.getPath().endsWith(".html")) 
    			result.add(file);
    		else if(file.isDirectory()) {
    			File[] provisionalList = findHtmls(file);
    			for(File provisionalFile : provisionalList) result.add(provisionalFile);
    		}
    	}
    	return result.toArray(new File[result.size()]);
    }
}
