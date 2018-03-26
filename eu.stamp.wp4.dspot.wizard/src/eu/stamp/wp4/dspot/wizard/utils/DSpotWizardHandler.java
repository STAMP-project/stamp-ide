package eu.stamp.wp4.dspot.wizard.utils;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.stamp.wp4.dspot.wizard.DSpotWizard;

/**
 * handler of the Eclipse wizard for DSpot
 *
 */
public class DSpotWizardHandler extends AbstractHandler {
	
	private WizardConfiguration wConf;  // instance of the class with the project's information 

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
    	try {
			wConf = new WizardConfiguration();
		} catch (CoreException e) {
			e.printStackTrace();
		}   // creating the object with the project's information
    	if(wConf.projectSelected()) {
        WizardDialog wizDiag = new WizardDialog(HandlerUtil.getActiveShell(event),new DSpotWizard(wConf));
        wizDiag.open();   // open the wizard
    	}
    	return null;
    }

}
          
      
      