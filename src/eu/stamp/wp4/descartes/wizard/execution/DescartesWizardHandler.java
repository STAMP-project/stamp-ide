package eu.stamp.wp4.descartes.wizard.execution;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.stamp.wp4.descartes.wizard.DescartesWizard;

public class DescartesWizardHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		WizardDialog wizDiag = new WizardDialog(HandlerUtil.getActiveShell(event),new DescartesWizard());
		wizDiag.open();
		return null;
	}

}
