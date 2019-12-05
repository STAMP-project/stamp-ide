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

package eu.stamp.eclipse.plugin.dspot.controls;

import java.awt.Point;

import eu.stamp.eclipse.dspot.controls.impl.CheckController;
import eu.stamp.eclipse.dspot.controls.impl.ComboController;
import eu.stamp.eclipse.dspot.controls.impl.ExplorerController;
import eu.stamp.eclipse.dspot.controls.impl.ListController;
import eu.stamp.eclipse.dspot.controls.impl.SimpleControllerProxy;
import eu.stamp.eclipse.dspot.controls.impl.SpinnerController;
import eu.stamp.eclipse.dspot.controls.impl.TextController;
import eu.stamp.eclipse.dspot.controls.impl.TreeController;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;
import eu.stamp.eclipse.plugin.dspot.properties.DSpotProperties;
/**
 * 
 */
public class ControllerFactory {
	/**
	 * 
	 */
	private boolean isFileParameter;
	/**
	 * 
	 */
	private String type;
	/**
	 * 
	 */
	private String project;
	/**
	 * 
	 */
	private boolean checkButton;
	/**
	 * 
	 */
	private String direction;
	/**
	 * 
	 */
	private String labelText;
	/**
	 * 
	 */
	private String key;
	/**
	 * 
	 */
	private int initialSelection;
    /**
     * 
     */
	private int step;
	/**
	 * 
	 */
	private Point interval;
	/**
	 * 
	 */
	private String activationDirection;
	/**
	 * 
	 */
	private String condition;
	/*
	 * 
	 */
	private int place;
	/*
	 * 
	 */
	private String tooltip;
	/**
	 * 
	 */
	private String[] content;
	/**
	 * 
	 */
	private boolean fileDialog;
	/**
	 * 
	 */
    private String[] extensions;
    /**
     * 
     */
	private int decimals;
	/**
	 * 
	 */
	private String enable;
	/**
	 * 
	 */
	private String separator;
    
	public void reset() {
		type = null; 
		project = null; 
		checkButton = false; 
		direction = null; 
		labelText = "no label text"; 
		key = null; 
		initialSelection = 1; 
		step = 1; 
		interval = new Point(0,100);
		activationDirection = null; 
		condition = null; 
		isFileParameter = false;
		place = 0;
		tooltip = null;
		content = null;
		fileDialog = true;
		extensions = null;
		decimals = 0;
		separator = null;
	}
    public void setFile(boolean isFileParameter) { 
    	this.isFileParameter = isFileParameter;
    }
	/**
	 * 
	 * @param parameter
	 * @param value
	 */
	public void setParameter(String parameter,String value) {
		switch(parameter) {
		case "type" : type = value;
		break;
		case "key" : key = value;
		break;
		case "direction" : direction = value;
		break;
		case "labelText" : labelText = value;
		break;
		case "tooltip" : tooltip = value;
		break;
		case "project" : project = value;
		break;
		case "activationDirection" : activationDirection = value;
		break;
		case "condition" : condition = value;
		break;
		case "checkButton" : if(value.contains("rue"))
			checkButton = true;
		else checkButton = false;
		break;
		case "step" : step = Integer.parseInt(value);
		break;
		case "initialSelection" : initialSelection = Integer.parseInt(value);
		break;
		case "place" : place = Integer.parseInt(value);
		break;
		case "content" : if(value.contains(",")) content = value.split(",");
		break;
		case "extensions" : if(value.contains(",")) extensions = value.split(",");
		else extensions = new String[] {value};
		break;
		case "explorerType" : if(value.contains("ile")) fileDialog = true;
		else fileDialog = false;
		break;
		case "decimals" : decimals = Integer.parseInt(value);
		break;
		case "separator" : separator = value;
		break;
		case "enable" : enable = value;
		case "interval" : if(value.contains(",")) {
			String[] point = value.split(",");
			interval = new Point(Integer.parseInt(point[0]),
					Integer.parseInt(point[1]));
		}
		  }
		}
	/**
	 * 
	 */
	public void createController() {
		
		if(direction == null || type == null || key == null) return;
		Controller controller = null;
		
		if(separator == null) separator = DSpotProperties.SEPARATOR;
		
		switch(type) {
		case "text" : controller = new TextController(
				key,labelText,checkButton,(project != null),place,tooltip);
		break;
		case "spinner" : controller = new SpinnerController(
				key,labelText,checkButton,initialSelection,step,interval,place,tooltip,decimals);
        break;
		case "combo" : controller = new ComboController(key,project,labelText,checkButton,
				activationDirection,condition,place,tooltip,content,separator);
		break;
		case "explorer" : controller = new ExplorerController(
	     		key,project,labelText,checkButton,place,tooltip,content,fileDialog,extensions,separator);
		break;
		case "list" : controller = new ListController(
				key,project,labelText,checkButton,place,tooltip,content,separator);
		break;
		case "tree" : controller = new TreeController(
				key,project,labelText,checkButton,place,tooltip,content,separator);
		break;
		case "check" :  key = key + DSpotProperties.CHECK_EXTRA_KEY;
			controller = new CheckController(key,labelText,place,tooltip,activationDirection,condition);
		}
		
		if(enable != null && !enable.isEmpty())
			controller.setEnableCondition(getCondition());
		
		if(direction.contains("ialog")) {
			if(controller instanceof CheckController)
				controller = new CheckProxy((CheckController)controller);
			else if(controller instanceof MultiController)
				controller = new MultiControllerProxy((MultiController)controller);
			else if(controller instanceof SimpleController)
				controller = new SimpleControllerProxy((SimpleController)controller);
		}
		
		if(controller != null) {
			if(isFileParameter) DSpotMapping.getInstance().setFileParameter(key);
			else DSpotMapping.getInstance().setCommandParameter(key);
			DSpotMapping.getInstance().setController(controller,direction);
		}
		}
	private EnableCondition getCondition() {
		boolean equal = true;
		String myEnableString = enable;
		if(enable.contains("!")) {
			equal = false;
			myEnableString = enable.replaceAll("!","");
		}
        String[] keyValue = myEnableString.split("/");
        return new EnableCondition(keyValue[0],keyValue[1],equal);
	}
}