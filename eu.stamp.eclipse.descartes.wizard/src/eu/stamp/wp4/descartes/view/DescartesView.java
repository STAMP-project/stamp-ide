package eu.stamp.wp4.descartes.view;

import java.net.URL;
import java.util.ArrayList;

import javax.inject.Inject;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;

public class DescartesView extends ViewPart {
	
	@Inject IWorkbench workbench;

	public static final String ID = "eu.stamp.wp4.descartes.view.DescartesView";
	
	private Composite parent;
	
	@Override
	public void createPartControl(Composite parent) { this.parent = parent; }
	

	@Override
	public void setFocus() {}
    
	public void setUrls(String[] urls) { 
		if(urls != null) {
			GridLayoutFactory.fillDefaults().applyTo(parent);
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
				}
			}
		}
	private String getFileName(String url) {
		if(url.contains("/") && url.contains(".")) 
			return url.substring(url.lastIndexOf('/')+1,url.lastIndexOf('.'));
		if(url.contains("\\") && url.contains("."))
            return url.substring(url.lastIndexOf('\\')+1,url.lastIndexOf('.'));
		return url;
	}
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
	private class MyFile{
		public String name;
		public String url;
		public MyFile(String name,String url) {
			this.name = name; this.url = url;
		}
	}
}
