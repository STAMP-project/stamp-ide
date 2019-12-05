/*******************************************************************************
 * Copyright (c) 2019 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * 	Ricardo Jose Tejada Garcia (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.eclipse.dspot.controls.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import eu.stamp.eclipse.plugin.dspot.controls.MultiController;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;

public class TreeController extends MultiController {
	
	private Tree tree;
	
	private String[] selection;
	
	private List<TreeItem> allItems;

	public TreeController(String key, String project, String labelText, boolean checkButton, int place, String tooltip,
			String[] content,String separator) {
		super(key, project, labelText, checkButton, place, tooltip, content,separator);
		if(content == null && project != null && !project.isEmpty()) super.loadProject();
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		tree = new Tree(parent,SWT.CHECK | SWT.BORDER | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().grab(true,true).minSize(200,200).span(3,5).applyTo(tree);
		getTreeItems();
	    tree.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
               updateTreeSelection();
	    	}
	    });
        String myData = DSpotMapping.getInstance().getValue(key);
        if(myData != null && !myData.isEmpty()) updateController(myData);
		tree.pack();
	}
	@Override
	protected void setContent(String[] content) {
		this.content = content;
		if(tree == null || tree.isDisposed()) return;
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				tree.removeAll();
				getTreeItems();
			}	
		});
	}

	@Override
	protected void setSelection(String[] selection) {
		this.selection = selection;
		if(tree == null || tree.isDisposed()) return;
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				tree.deselectAll();
				for(TreeItem item : allItems)for(String sr : selection){
					if(item.getData().toString().equalsIgnoreCase(sr)) {
						tree.select(item);
						item.setChecked(true);
						break;
					} else {
						item.setChecked(false);
					}
				}
				tree.notifyListeners(SWT.Selection,new Event());
			}
		});
	}

	@Override
	public void notifyListener() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				tree.notifyListeners(SWT.Selection,new Event());
			}	
		});
	}

	@Override
	public void setEnabled(boolean enabled) {}

	@Override
	public void updateController(String data) {
		if(tree == null || tree.isDisposed() || data == null) return;
		tree.deselectAll();
		for(TreeItem item : allItems) {
			if(data.contains(item.getData().toString())) {
				tree.select(item);
				item.setChecked(true);
			} else item.setChecked(false);
		}
	}

	@Override
	public int checkActivation(String condition) { return 0; }
	
	private void updateTreeSelection() {
		 List<String> newSelection = new ArrayList<String>(allItems.size());
		 for(TreeItem item : allItems)if(item.getChecked())
			      newSelection.add(item.getData().toString());
		 selection = new String[newSelection.size()];
		 for(int i = 0; i < newSelection.size(); i++)
			 selection[i] = newSelection.get(i);
		 if(selection.length < 1) return;
		 StringBuilder dataBuilder = new StringBuilder();
		 dataBuilder.append(selection[0]);
		 for(int i = 1; i < selection.length; i++) {
			 dataBuilder.append(separator);
			 dataBuilder.append(selection[i]);
		 }
		 DSpotMapping.getInstance().setValue(key,dataBuilder.toString());
	}
	
	private void getTreeItems(){
		Set<String> packages = new HashSet<String>();
		allItems = new LinkedList<TreeItem>();
        for(String sr : content)if(sr.contains("."))
        	packages.add(sr.substring(0,sr.lastIndexOf(".")));
        for(String myPackage : packages) {
        	TreeItem item = new TreeItem(tree,0);
        	allItems.add(item);
        	item.setText(myPackage);
        	item.setData(myPackage + ".*");
        	for(String clazz : content)if(clazz.contains(myPackage)){
        		TreeItem subItem = new TreeItem(item,0);
        		allItems.add(subItem);
        		subItem.setText(clazz);
        		subItem.setData(clazz);
        	}
        }
	}
	
}
