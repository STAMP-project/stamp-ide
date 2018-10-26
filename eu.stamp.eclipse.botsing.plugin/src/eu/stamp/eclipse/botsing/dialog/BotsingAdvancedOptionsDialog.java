/*******************************************************************************
 * Copyright (c) 2018 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ricardo José Tejada García (Atos) - main developer
 * Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.eclipse.botsing.dialog;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import eu.stamp.eclipse.botsing.interfaces.IBotsingConfigurablePart;
import eu.stamp.eclipse.botsing.interfaces.IBotsingInfoSource;
import eu.stamp.eclipse.botsing.launch.BotsingPartialInfo;
import eu.stamp.eclipse.botsing.properties.AbstractBotsingProperty;
import eu.stamp.eclipse.botsing.properties.BotsingDialogProperty;
import eu.stamp.eclipse.botsing.properties.BotsingSpinnerProperty;

public class BotsingAdvancedOptionsDialog extends TitleAreaDialog 
              implements IBotsingConfigurablePart, IBotsingInfoSource{

	public static Image image;
	
	private final List<BotsingDialogProperty> properties;
	
	public BotsingAdvancedOptionsDialog(Shell parentShell) {
		super(parentShell);
		properties = new LinkedList<BotsingDialogProperty>();
		
		 addSpinner("100","-Dpopulation","Population : ",10,20,4000);
         addSpinner("1800","-Dsearch_budget","Search Budget : ",100,800,80000);
         addSpinner("30","-Dmax_recursion","Max recursion : ",5,5,1000);
	}
	
	@Override
	public void create() {
		super.create();
		setTitle("Botsing configuration");
		setMessage("Botsing configuration options");
		if(image != null) setTitleImage(image);
	}
    @Override
    protected Control createDialogArea(Composite parent) {
    	
  	     Composite composite = (Composite)super.createDialogArea(parent);
		 GridLayout layout = new GridLayout(3,true);
		 composite.setLayout(layout);
		 
		Label space = new Label(composite,SWT.NONE);
		space.setText("");
		GridDataFactory.fillDefaults().span(2,1).applyTo(space);
		 
		 for(BotsingDialogProperty property : properties) {
			 property.setData(property.getData());
			 property.getCoreProperty().createControl(composite);
		 }
    	
    	return composite;
    }
    
    @Override
    public void configureShell(Shell shell) {
    	super.configureShell(shell);
    	shell.setText("Botsing configuration");
    }
    
    private void addSpinner(String defaultValue,String key,
    		String name,int step,int minimun,int maximun) {
    	
    	BotsingSpinnerProperty spinnerProperty = 
    			new BotsingSpinnerProperty(defaultValue,
                          key,name,step,minimun,maximun,false);
    	
    	properties.add(new BotsingDialogProperty(spinnerProperty));
    }
    
	@Override
	public void okPressed() {
		super.okPressed();
		for(BotsingDialogProperty property : properties)
			property.save();
	}

	@Override
	public BotsingPartialInfo getInfo() {
		List<AbstractBotsingProperty> result = 
				new LinkedList<AbstractBotsingProperty>();
		for(BotsingDialogProperty property : properties)
			result.add(property.getCoreProperty());
		return new BotsingPartialInfo(result);
	}

	@Override
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy configuration){
		for(AbstractBotsingProperty property : properties)
			property.appendToConfiguration(configuration);
	}

	@Override
	public void load(ILaunchConfigurationWorkingCopy configuration) {
		for(AbstractBotsingProperty property : properties)
			property.load(configuration);
	}
	
	
}
