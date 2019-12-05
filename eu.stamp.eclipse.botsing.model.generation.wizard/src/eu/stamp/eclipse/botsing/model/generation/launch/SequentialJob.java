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
package eu.stamp.eclipse.botsing.model.generation.launch;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;

public abstract class SequentialJob extends Job {
	
	protected IProcess[] processes;
	
	protected ILaunchConfiguration launchConfiguration;

	public SequentialJob(String name) { super(name); }

	public boolean isWorking() {
		if(processes == null) return false;
		for(IProcess process : processes)if(!process.isTerminated()) return true;
		return false;
	}
	
	public void delete() {
		/*if(launchConfiguration == null) return;
		try {
			launchConfiguration.delete();
		} catch (CoreException e) {
			e.printStackTrace();
		}*/
	}
}
