package eu.stamp.eclipse.descartes.wizard.dialogs;

import org.eclipse.swt.widgets.Shell;

import eu.stamp.eclipse.wizard.dialogs.ExecutionErrorDialog;

public class DescartesExecutionErrorDialog extends ExecutionErrorDialog {

	public DescartesExecutionErrorDialog(Shell parentShell, boolean toolError) {
		super(parentShell, toolError);
	}

	@Override
	protected String getPluginErrorDirection() {
		return "https://github.com/STAMP-project/stamp-ide/issues";
	}

	@Override
	protected String getPluginErrorMessage() {
		return "Error in Descartes plugin, please check the .log file, "
				+ "you can report it in : ";
	}

	@Override
	protected String getToolErrorDirection() {
		return "https://github.com/STAMP-project/pitest-descartes/issues";
	}

	@Override
	protected String getToolErrorMessage() {
		return "Error during Descartes execution, please check the log,"
				+ " you can report it in : ";
	}

	@Override
	protected String getToolName() {
		return "Descartes";
	}

}
