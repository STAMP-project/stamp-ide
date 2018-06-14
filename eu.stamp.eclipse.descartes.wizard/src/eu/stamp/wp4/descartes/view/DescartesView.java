package eu.stamp.wp4.descartes.view;

import java.util.ArrayList;

import javax.inject.Inject;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;
/**
 *  This class describes the Descartes Eclipse view to display 
 *  the content of the html summaries, the view is formed by a tab,
 *  each item of the tab contains a browser to display one of the documents
 *
 */
public class DescartesView extends ViewPart {
	
	@Inject IWorkbench workbench;

	public static final String ID = "eu.stamp.wp4.descartes.view.DescartesView";
	
	private Composite parent;
	
	@Override
	public void createPartControl(Composite parent) { this.parent = parent; }
	

	@Override
	public void setFocus() {}
    
	/**
	 * updates the view to show a new html list
	 * @param urls : a string array with the urls of the documents
	 */
	public void setUrls(String[] urls) { 
		
		String url = null;
		for(String sr : urls)if(sr.contains("index")) { url = sr; break; }
		
		if(url != null) {		
			Control[] children = parent.getChildren();
			for(Control child : children) child.dispose();
			GridLayoutFactory.fillDefaults().numColumns(3).applyTo(parent);
			
			Button backButton = new Button(parent,SWT.PUSH);
			backButton.setText("Back");	
			GridDataFactory.swtDefaults().applyTo(backButton);
			
			Button forwardButton = new Button(parent,SWT.PUSH);
			forwardButton.setText("Forward");
			GridDataFactory.swtDefaults().applyTo(forwardButton);
			
			Browser browser = new Browser(parent,SWT.NONE);
			browser.setUrl(url);
			GridDataFactory.fillDefaults().span(3,1).grab(true, true).minSize(200,150)
			.applyTo(browser);
			
			backButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) { browser.back(); }
			});
			
			forwardButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) { browser.forward(); }
			});
			/*
			TabFolder tabFolder = new TabFolder(parent,SWT.NONE);
			GridDataFactory.fillDefaults().grab(true, true).minSize(200,150).applyTo(tabFolder);
            tabFolder.setLayout(new GridLayout());
			
			MyFileList fileList = new MyFileList();
			for(String url : urls) fileList.addMyFile(new MyFile(getFileName(url),url));
			ArrayList<MyFile> list = fileList.getList();
			for(MyFile file : list) {
				TabItem tab = new TabItem(tabFolder,SWT.HORIZONTAL | SWT.BORDER);
				tab.setText(file.name);

				Browser browser = new Browser(tabFolder,SWT.NONE);
				browser.setUrl(file.url);
				GridDataFactory.fillDefaults().grab(true, true).minSize(200,150)
				.applyTo(browser);
				tab.setControl(browser);
				parent.layout();
				}*/
			}
		}
	/**
	 * get the name of a file from its url, this method is called to set the name of the tab items
	 * @param url : a string representing an url
	 * @return the name of the file (without extension)
	 */
	private String getFileName(String url) {
		if(url.contains("/") && url.contains("."))   // / or \\ depending of the OS
			return url.substring(url.lastIndexOf('/')+1,url.lastIndexOf('.'));
		if(url.contains("\\") && url.contains("."))
            return url.substring(url.lastIndexOf('\\')+1,url.lastIndexOf('.'));
		return url;
	}
	/**
	 * inner class to store the MyFile objects without name repetition 
	 */
	private class MyFileList{
		private ArrayList<MyFile> list;
		public MyFileList() {
			list = new ArrayList<MyFile>(1);
		}
		public void addMyFile(MyFile myFile) {
			if(!list.isEmpty())for(MyFile file : list)
				if(file.name.equalsIgnoreCase(myFile.name)) return; // no repetition
			list.add(myFile);
		}
		public ArrayList<MyFile> getList(){
			return list;
		}
	}
	/**
	 * inner class to get together the name and url of a file
	 */
	private class MyFile{
		public String name;
		public String url;
		public MyFile(String name,String url) {
			this.name = name; this.url = url;
		}
	}
}
