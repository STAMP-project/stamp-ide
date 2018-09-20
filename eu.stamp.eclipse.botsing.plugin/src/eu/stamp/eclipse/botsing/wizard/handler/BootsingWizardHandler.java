package eu.stamp.eclipse.botsing.wizard.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.stamp.eclipse.botsing.wizard.BotsingWizard;

public class BootsingWizardHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		WizardDialog wizardDialog = 
				new WizardDialog(HandlerUtil.getActiveShell(event),
						new BotsingWizard());
		wizardDialog.open();
		return null;
	}

}
