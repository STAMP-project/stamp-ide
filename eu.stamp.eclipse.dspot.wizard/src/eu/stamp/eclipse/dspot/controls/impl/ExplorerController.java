
package eu.stamp.eclipse.dspot.controls.impl;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SegmentEvent;
import org.eclipse.swt.events.SegmentListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import eu.stamp.eclipse.plugin.dspot.context.DSpotContext;
import eu.stamp.eclipse.plugin.dspot.controls.MultiController;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;
import eu.stamp.eclipse.plugin.dspot.properties.DSpotProperties;

/**
 * 
 */
public class ExplorerController extends MultiController {

	private Text text;
	private Button explorerButton;
	/**
	 * 
	 */
	private final boolean file;
	
	private final String[] filterExtensions;
	
	private String[] targets;
	
	public ExplorerController(String key, String project, String labelText, boolean checkButton,
			int place,String tooltip,String[] content,boolean file,String[] filterExtensions,String separator) {
		super(key, project, labelText, checkButton,place,tooltip,content,separator);
		this.filterExtensions = filterExtensions;
		this.file = file;
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		text = new Text(parent,SWT.BORDER | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().indent(DSpotProperties.INDENT)
		.grab(true,false).applyTo(text);
		text.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
		        if(!listenerOn) return;
				DSpotMapping.getInstance().setValue(key,text.getText());
			}
		});
		explorerButton = new Button(parent,SWT.PUSH);
		explorerButton.setText("Select");
		GridDataFactory.swtDefaults().indent(DSpotProperties.INDENT).applyTo(explorerButton);
		explorerButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				String result;
				if(file) result = showFileDialog();
				else result = showDirectoryDialog();
				if(project != null) result = match(result);
				String sr = text.getText();
				if(sr == null || sr.isEmpty()) text.setText("");
				else text.setText(sr + DSpotProperties.getPathSeparator() + result);
			}
		});
		explorerButton.setEnabled(isEnabled);
		text.setEnabled(isEnabled);
	}

	@Override
	protected void setContent(String[] content) {
	DSpotMapping.getInstance().setValue(key, null);
       if(text == null) return;
       if(text.isDisposed())return;
       text.setText("");
       this.targets = content;
	}
	
	private String match(String  path) {
		if(path.contains("\\")) path = path.replaceAll("\\",".");
		if(path.contains("/")) path = path.replaceAll("/",".");
		for(String target : targets)if(path.contains(target))return target;
	    return "";
	}

	@Override
	protected void setSelection(String[] selection) {
		if(text == null) {
			DSpotMapping.getInstance().setValue(key,processList(selection));
		    return;
		}
		if(text.isDisposed()) {
			DSpotMapping.getInstance().setValue(key,processList(selection));
		    return;	
		}
		String sr = processList(selection);
		if(sr != null)text.setText(sr);
		
	}

	@Override
	public void setEnabled(boolean enabled) {
		isEnabled = enabled;
		if(text == null || text.isDisposed()) return;
		explorerButton.setEnabled(enabled);
		text.setEnabled(enabled);
		if(!enabled)if(text != null && !text.isDisposed())
			text.setText("");
	}
	private String processList(String[] selection) {
		if(selection == null) return null;
		if(selection.length < 1) return null;
		String result = selection[0];
		for(int i = 1; i < selection.length; i++)
			result += DSpotProperties.SEPARATOR + selection[i];
		return result;
	}

	@Override
	public void notifyListener() { text.notifyListeners(SWT.Segments,new Event()); }

	@Override
	public void updateController(String data) {
	if(data == null || text == null || text.isDisposed()) return;
	text.setText(data);
	}
	
	private String showDirectoryDialog() {
		
		DirectoryDialog dialog = new DirectoryDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		
		return dialog.open();
	}
	
	private String showFileDialog() {
		 FileDialog dialog = new FileDialog(
				 PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				 .getShell(),SWT.OK | SWT.MULTI);
		 dialog.setFilterExtensions(new String[] {"*.log"});
		 dialog.setText("Select log file");
         dialog.setFilterExtensions(filterExtensions);
		 String path = DSpotContext.getInstance().getProject().getProject().getLocation().toString();
		 dialog.setFilterPath(path);
	     
	      return dialog.open();
	}

	@Override
	public int checkActivation(String condition) {
		// TODO Auto-generated method stub
		return 0;
	}
}