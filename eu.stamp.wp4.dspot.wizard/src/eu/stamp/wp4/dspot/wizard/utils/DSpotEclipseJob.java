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
	
	// String with the parameters
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
