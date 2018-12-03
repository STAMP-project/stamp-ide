/*******************************************************************************
 * Copyright (c) 2018 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ricardo Jose Tejada Garcia (Atos) - main developer
 * Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.wp4.dspot.wizard.utils;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import eu.stamp.wp4.dspot.dialogs.DSpotExecutionErrorDialog;
import eu.stamp.wp4.dspot.execution.handlers.DSpotExecutionHandler;
import eu.stamp.wp4.dspot.view.DSpotView;

/**
 *  This class describes the background invocation of Dspot 
 *  that starts when the user click the finish button of the wizard 
 */
public class DSpotEclipseJob extends Job {

private WizardConfiguration conf;
private String Orders;
private boolean error;

public DSpotEclipseJob(String path,
WizardConfiguration conf) {
   super("DSpot working");
if(conf == null) error = true;
else {
	error = false;
	this.Orders = " -p " + path +" "+ conf.getDSpotMemory().getAsString(); 
    this.conf = conf;
}
} // end of the constructor

@Override
protected IStatus run(IProgressMonitor monitor) {
 
	if(error) return Status.OK_STATUS;
	
     DSpotExecutionHandler executor = new DSpotExecutionHandler(conf,Orders);
     String outputDirectory;
     try {
 //long start = System.currentTimeMillis();
outputDirectory = (String)executor.execute(new ExecutionEvent());
while(!executor.isFinished());  // wait until DSpod finish
conf.getPro().getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
if(executor.getExitCode() != 0) {
	openDSpotErrorMessage(true);
	return Status.OK_STATUS;
}
 //long time = System.currentTimeMillis() - start;
 //System.out.println(time);
 } catch (Exception e) {
 e.printStackTrace();
 openDSpotErrorMessage(executor.getExitCode() != 0);
 return Status.OK_STATUS;
 }
 
 Display.getDefault().asyncExec(new Runnable() {
	@Override
	public void run() {
 try { if(outputDirectory != null)if(!outputDirectory.isEmpty()) {	
       DSpotView view = (DSpotView)PlatformUI.getWorkbench()
    	 .getActiveWorkbenchWindow().getActivePage().showView(DSpotView.ID);
	   view.parseJSON(outputDirectory);
 }   
} catch (PartInitException | IOException e) {
	e.printStackTrace();
	openDSpotErrorMessage(false);
}
		}	 
	 });

return Status.OK_STATUS;
} // end of run

private void openDSpotErrorMessage(boolean DSpotError) {
	
	Display.getDefault().asyncExec(new Runnable() {
		@Override
		public void run() {
			Shell shell = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell();
		    DSpotExecutionErrorDialog dialog = 
		    		new DSpotExecutionErrorDialog(shell,DSpotError);
		    dialog.open();
		}
	});
}
	 
}
