package eu.stamp.eclipse.botsing.properties;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.PlatformUI;

import com.richclientgui.toolbox.validation.string.StringValidationToolkit;

public class OutputTraceProperty extends BotsingExplorerField {
	
	public static final String KEY = "Trace path";

	public OutputTraceProperty(String defaultValue, String name, 
			boolean compulsory,StringValidationToolkit kit) {
		super(defaultValue, KEY, name, compulsory,false,true,kit);
	}
	
	@Override
	protected String openExplorer() {
		
		DirectoryDialog dialog = new DirectoryDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell());
		
		String path = dialog.open();
		return path;
	}

}
