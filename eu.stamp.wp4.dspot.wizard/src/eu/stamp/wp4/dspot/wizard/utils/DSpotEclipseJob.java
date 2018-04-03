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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
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
	
private String[] parameters;            // execution information given by the user
private String[] advParameters;
private boolean verbose;
private boolean clean;
private WizardConfiguration conf;
	
public DSpotEclipseJob(String[] parameters,String[] advParameters,boolean verbose,boolean clean,WizardConfiguration conf) {
   super("DSpot working");
	this.parameters = parameters;
	this.advParameters = advParameters;
	this.verbose = verbose;
	this.clean = clean;
	this.conf = conf;
} // end of the constructor

@Override
protected IStatus run(IProgressMonitor monitor) {
	
    String Orders = " -p " +parameters[1]+" -i "+parameters[2]+" -t "+parameters[3];
    if(parameters[4] != null && !parameters[4].isEmpty()) {
    	Orders = Orders + " -a "+parameters[4] ;
    }
    if(parameters[5]!= null && !parameters[5].isEmpty()) {
    	Orders = Orders +" -s "+parameters[5];
    }
    if(parameters[6] != null && !parameters[6].isEmpty()) {
    	Orders = Orders +" -g "+parameters[6];
    }
       
    for(String s : advParameters) {
    	Orders = Orders + s;   // put the extra options in the string
    }
    if(verbose) {
    	Orders = Orders + " --verbose";
    }
    if(clean) {
    	Orders = Orders + " --clean";
    }
     
     DSpotExecutionHandler executor = new DSpotExecutionHandler(conf,Orders);
 	try {	
 		executor.execute(new ExecutionEvent());
 	 while(!executor.isFinished());  // wait until DSpod finish
 	} catch (ExecutionException e) {
 		e.printStackTrace();
 	}
     
	return Status.OK_STATUS;
} // end of run
}
