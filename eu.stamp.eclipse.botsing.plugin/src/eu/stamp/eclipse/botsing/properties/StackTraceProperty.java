package eu.stamp.eclipse.botsing.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.PlatformUI;

public class StackTraceProperty extends BotsingExplorerField {

	public StackTraceProperty(String defaultValue, String key, String name) {
		super(defaultValue, key, name);
	}
	@Override
	protected String openExplorer() {
		
	     final DirectoryDialog dialog = new DirectoryDialog(
	      PlatformUI.getWorkbench().getActiveWorkbenchWindow()
	      .getShell(),SWT.OK);
	     dialog.setText("Select a folder");
	     
	      String result = dialog.open();
	      return result;
	}
}