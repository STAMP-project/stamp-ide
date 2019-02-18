
package eu.stamp.eclipse.plugin.dspot.controls;

import java.awt.Point;

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
		if(controller != null) {
			if(isFileParameter) DSpotMapping.getInstance().setFileParameter(key);
			else DSpotMapping.getInstance().setCommandParameter(key);
			DSpotMapping.getInstance().setController(controller,direction);
		}
		}
}