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
package eu.stamp.eclipse.botsing.model.generation.load;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.swt.widgets.Display;

public class GenerationConfigurationLoader {
	
	private static final String ITEM_SEPARATOR = "%";
	
	private static final String KEY_VALUE_SEPARATOR = "@";
	
	private boolean noLoad;

	private Map<String,String> map;
	
	private final LinkedList<LoadableElement> loadElements;
	
	public GenerationConfigurationLoader() {
		reset(); 
		loadElements = new LinkedList<LoadableElement>();
		noLoad = true;
		}
	
	public void reset() { map = new HashMap<String,String>(); }
	
	public boolean getNoLoad() { return noLoad; }
	
	public String toString() { 
		StringBuilder builder = new StringBuilder();
        boolean first = true;
        for(String key : map.keySet()) {
        	if(first) first = false;
        	else builder.append(ITEM_SEPARATOR);
        	builder.append(key).append(KEY_VALUE_SEPARATOR).append(map.get(key));
        }
        return builder.toString();
	}
	
	public void fromString(String loadString) {
		reset();
		try {
		String[] items = loadString.split(ITEM_SEPARATOR);
		for(String item : items) {
			String[] keyValue = item.split(KEY_VALUE_SEPARATOR);
			map.put(keyValue[0],keyValue[1]);
			noLoad = false;
		}
		} catch(ArrayIndexOutOfBoundsException e) { 
			System.err.println("ERROR : Wrong load string in GenerationConfigurationLoader"); 
			e.printStackTrace();
			}
	}
	
	public void load() {
		if(noLoad) return;
		Display display = Display.getDefault();
		for(LoadableElement loadElement : loadElements) display.syncExec(new Runnable() {
			@Override
			public void run() {
				loadElement.load(map);
			}
		});
	}
	
	public void update(Map<String,String> map2) { 
	  reset();
	  noLoad = false;
      for(String key : map2.keySet()) map.put(key,map2.get(key));
	}
	
	public void addLoadablePart(LoadableElement loadElement) { loadElements.add(loadElement); }
}
