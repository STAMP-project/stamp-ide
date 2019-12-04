package eu.stamp.eclipse.botsing.model.generation.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.stamp.eclipse.botsing.model.generation.launch.SequentialJob;
import eu.stamp.eclipse.botsing.model.generation.wizard.ModelGenerationWizard;

public class ModelGenerationHandler extends StampHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ModelGenerationWizard wizard = new ModelGenerationWizard(getProject());
		WizardDialog wizardDialog = new WizardDialog(HandlerUtil.getActiveShell(event),wizard);
		wizardDialog.setBlockOnOpen(true);
		wizardDialog.open();
		SequentialJob job = wizard.getJob();
		if(job != null) {
			job.schedule();
			while(job.isWorking()) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			job.delete();
		}
		
		return null;
	}
}
