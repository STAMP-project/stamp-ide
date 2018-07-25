/*******************************************************************************
 * Copyright (c) 2018 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Ricardo Jose Tejada Garcia (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.wp4.dspot.wizard.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import eu.stamp.wp4.dspot.execution.handlers.DSpotExecutionHandler;

/**
 *  This class describes the background invocation of Dspot 
 *  that starts when the user click the finish button of the wizard 
 */
public class DSpotEclipseJob extends Job {

private WizardConfiguration conf;
private String Orders;
	
public DSpotEclipseJob(String path,
		WizardConfiguration conf) {
   super("DSpot working");

    this.Orders = " -p " + path +" "+ conf.getDSpotMemory().getAsString();
	this.conf = conf;
} // end of the constructor

@Override
protected IStatus run(IProgressMonitor monitor) {
	 
     DSpotExecutionHandler executor = new DSpotExecutionHandler(conf,Orders);
 	try {	
 		//long start = System.currentTimeMillis();
 		executor.execute(new ExecutionEvent());
	while(!executor.isFinished());  // wait until DSpod finish
	conf.getPro().getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
 	//long time = System.currentTimeMillis() - start;
 	//System.out.println(time);
 	} catch (ExecutionException | CoreException e) {
 		e.printStackTrace();
 	}
	return Status.OK_STATUS;
} // end of run

}
