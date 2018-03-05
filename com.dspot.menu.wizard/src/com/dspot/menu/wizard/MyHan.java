package com.dspot.menu.wizard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;


public class MyHan extends AbstractHandler {
	
	private WizardConf wConf;  // instance of the class with the project's information

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
    	wConf = new WizardConf();   // creating the object with the project's information
        WizardDialog wizDiag = new WizardDialog(HandlerUtil.getActiveShell(event),new DspotWiz(wConf));
        wizDiag.open();   // open the wizard
        return null;
    }

}
          
      
      