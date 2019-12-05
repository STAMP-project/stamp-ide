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

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.widgets.Composite;

import eu.stamp.eclipse.dspot.controls.impl.CheckController;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;

public class CheckProxy extends CheckController implements IControllerProxy {

	protected final CheckController innerController;
	
	protected boolean data;
	
	protected boolean temporalData;
	
	public CheckProxy(CheckController innerController) {
		super(null,null,0,null,null,null);
		this.innerController = innerController;
	
		this.activationDirection = innerController.activationDirection;
		this.place = innerController.place;
		innerController.setProxy(this);
	}
	@Override
	public void save() {
		data = temporalData;
		DSpotMapping.getInstance().setValue(innerController.key,String.valueOf(data));
	}
	
	public void setTemporalData(boolean temporalData) {
		this.temporalData = temporalData;
	}
	
	@Override
	public void createControl(Composite parent) {
		DSpotMapping.getInstance().setValue(innerController.key,String.valueOf(data));
		innerController.createControl(parent);
	} 
	@Override
	public void setEnabled(boolean enabled) {
		innerController.setEnabled(enabled);
	}
	@Override
	public void loadConfiguration(ILaunchConfiguration configuration) {
		innerController.loadConfiguration(configuration);
	}
	@Override
	public int checkActivation(String condition) {
		return innerController.checkActivation(condition);
	}
}