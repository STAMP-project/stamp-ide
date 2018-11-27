/*******************************************************************************
 * Copyright (c) 2018 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Ricardo José Tejada García (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.wp4.descartes.view;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
/**
 * An instance of this class is responsible for finding 
 * the Descartes Issues and Pit Coverage reports indexes after
 * load and show the views,after the Descartes execution, if a report is
 * not produced (usually because of the user's output formats selection)
 * only the non empty view will be shown
 */
public class DescartesViewsActivator {

	private URL[] urls;
	
	private boolean ok;
	
	public DescartesViewsActivator(File folder) {
		
		setOutputFolder(folder);
		if(!ok)	System.out.println(
		">> ERROR loading index.html files in DescartesViewsActivator");
	}
	
	public void activate() {
		
		if(!ok) {
			System.out.println(
		">> ERROR can not activate views because the index files are not correctly loaded");
		return;
		}
		
        loadView(true);
        loadView(false);
	}
	
	public void setOutputFolder(File folder) {
		
		// checking
		if(folder == null) { 
			ok = false; 
			return; 
			}
		if(!folder.exists()) { 
			ok = false; 
			return; 
			}
		if(!folder.isDirectory()) { 
			ok = false; 
			return; 
			}
		
		List<File> list = findFiles(folder);
		if(list.isEmpty()) { 
			ok = false; 
			return; 
			}
		
		urls = new URL[list.size()];
		int i = 0;
		for(File file : list) {
			try {
				urls[i] = file.toURI().toURL();
				i++;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				ok = false;
				return;
			}
		}
		ok = true;
	}
	
	/**
	 * @param folder : the root folder to start the recursive search
	 * @return a List with all index.html files in the subsystem of folder
	 */
	private List<File> findFiles(File folder) {
		
		File[] files = folder.listFiles();
		
		List<File> result = new LinkedList<File>();
		
		for(File file : files) {
			if(file.exists()) {
			if(file.isDirectory()) {
				List<File> provisionalList = findFiles(file); // recursive
				for(File f : provisionalList)
					result.add(f);
			}
			else if(file.getPath().endsWith("index.html"))
				result.add(file);
		}
		}
		return result;
	}
	
	private void loadView(boolean issues) {
		
		URL url = getURL(issues);
		if(url == null) return;
		
		String id;
		if(issues) id = DescartesIssuesView.ID;
		else id = PitCoverageView.ID;		
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				
				IWorkbench work = PlatformUI.getWorkbench();
				IWorkbenchWindow wi = work.getActiveWorkbenchWindow();
				IWorkbenchPage page = wi.getActivePage();
				
				try {
					DescartesAbstractView issues = 
							(DescartesAbstractView)page.showView(id);
				    issues.setURL(url);
				    issues.load();
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}	
		});
	
	}
	
	private URL getURL(boolean issues) {
			for(URL url : urls)if(url != null)if(url.getPath() != null)
				if(url.getPath().contains("issues") == issues) {
					return url;
				}
			return null;
		}
	
}
