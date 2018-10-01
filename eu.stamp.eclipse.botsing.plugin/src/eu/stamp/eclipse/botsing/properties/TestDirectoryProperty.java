package eu.stamp.eclipse.botsing.properties;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.PlatformUI;

public class TestDirectoryProperty extends BotsingExplorerField {

	public TestDirectoryProperty(String defaultValue, String key, String name) {
		super(defaultValue, key, name);
	}

	@Override
	protected String openExplorer() {
		
		DirectoryDialog dialog = 
				new DirectoryDialog(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell());
		
		dialog.setMessage(" Select test directory ");
		dialog.setText(" Test directory selection ");
		
		String result = dialog.open();
		return result;
	}

}
