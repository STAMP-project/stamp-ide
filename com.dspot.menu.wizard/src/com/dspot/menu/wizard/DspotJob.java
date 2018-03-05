package com.dspot.menu.wizard;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.console.MessageConsoleStream;

import eu.stamp.wp4.dspot.execution.handlers.DSpotExecutionHandler;


public class DspotJob extends Job {
	
	/*
	 *  This class describes the background invocation of Dspot 
	 *  that starts when the user click the finish button of the wizard
	 */
	
private	MessageConsoleStream out;       // this will be the Dspot console in the eclipse application
private String[] parameters;            // execution information given by the user
private String[] advParameters;
private boolean verbose;
private boolean clean;
private WizardConf conf;
	
public DspotJob(String name,MessageConsoleStream out,String[] parameters,String[] advParameters,boolean verbose,boolean clean,WizardConf conf) {
	super(name);
	this.out = out;
	this.parameters = parameters;
	this.advParameters = advParameters;
	this.verbose = verbose;
	this.clean = clean;
	this.conf = conf;
} // end of the constructor

@Override
protected IStatus run(IProgressMonitor monitor) {

	out.println(" DSpot is beeing executed ");  // console
	out.println();
	
	// the String to give the order like a command line order using the exec() method of the java's Runtime class
    String Orders = " -p " +parameters[1]+"/dspot.properties -i "+parameters[2]+" -t "+parameters[3]+
    		" -a "+parameters[4]+" -s "+parameters[5]+" -g "+parameters[6];
       
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
	} catch (ExecutionException e) {
		e.printStackTrace();
	}
	
	return Status.OK_STATUS;
} // end of run

}
