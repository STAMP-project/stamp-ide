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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.widgets.Display;

import eu.stamp.eclipse.dspot.controls.impl.SimpleControllerProxy;

public abstract class SimpleController extends Controller {
	/**
	 * 
	 */
	protected final boolean projectDependent;
	
	protected SimpleControllerProxy proxy;
/**
 * 
 * @param projectDependent
 */
	public SimpleController(String key, String labelText, boolean checkButton,boolean projectDependent,int place,String tooltip) {
		super(key, labelText, checkButton,place,tooltip);
		this.projectDependent = projectDependent;
	}
	
	public void setProxy(SimpleControllerProxy proxy) {
		this.proxy = proxy;
	}

	@Override
	public void loadConfiguration(ILaunchConfiguration configuration) {
		try {
			String data = configuration.getAttribute(key,"");
			if(data == null || data.isEmpty()) return;
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
				   setText(data);
				}
			});
			setData(data);
			if(proxy != null) {
				proxy.setTemporalData(data);
				proxy.save();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		firstTime = false;
	}
    /**
     * 
     * @param text
     */
	public abstract void setText(String text);
}
