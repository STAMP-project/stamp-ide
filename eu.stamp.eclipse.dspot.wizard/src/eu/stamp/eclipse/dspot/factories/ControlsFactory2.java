package eu.stamp.eclipse.dspot.factories;

import java.awt.Point;

import eu.stamp.eclipse.dspot.controls.impl.CheckController;
import eu.stamp.eclipse.dspot.controls.impl.ComboController;
import eu.stamp.eclipse.dspot.controls.impl.ExplorerController;
import eu.stamp.eclipse.dspot.controls.impl.ListController;
import eu.stamp.eclipse.dspot.controls.impl.SimpleControllerProxy;
import eu.stamp.eclipse.dspot.controls.impl.SpinnerController;
import eu.stamp.eclipse.dspot.controls.impl.TextController;
import eu.stamp.eclipse.plugin.dspot.controls.CheckProxy;
import eu.stamp.eclipse.plugin.dspot.controls.Controller;
import eu.stamp.eclipse.plugin.dspot.controls.MultiController;
import eu.stamp.eclipse.plugin.dspot.controls.MultiControllerProxy;
import eu.stamp.eclipse.plugin.dspot.controls.SimpleController;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;
import eu.stamp.eclipse.plugin.dspot.properties.DSpotProperties;

public class ControlsFactory2 {
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
public void createController() {
			
	if(direction == null || type == null || key == null) return;
	Controller controller = null;
			
	switch(type) {
	case "text" : controller = new TextController(
			key,labelText,checkButton,(project != null),place,tooltip);
	break;
	case "spinner" : controller = new SpinnerController(
		key,labelText,checkButton,initialSelection,step,interval,place,tooltip);
	break;
	case "combo" : controller = new ComboController(key,project,labelText,checkButton,
		activationDirection,condition,place,tooltip,content);
	break;
	case "explorer" : controller = new ExplorerController(
		 key,project,labelText,checkButton,place,tooltip,content,fileDialog,extensions);
	break;
	case "list" : controller = new ListController(
		key,project,labelText,checkButton,place,tooltip,content);
	break;
	case "check" :  key = key + DSpotProperties.CHECK_EXTRA_KEY;
	controller = new CheckController(key,labelText,place,tooltip,activationDirection,condition);
			}
			
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
}
