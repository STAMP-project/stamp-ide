
package eu.stamp.eclipse.dspot.controls.impl;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;

import eu.stamp.eclipse.plugin.dspot.controls.MultiController;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;
import eu.stamp.eclipse.plugin.dspot.properties.DSpotProperties;

/**
 * 
 */
public class ListController extends MultiController {

	private List list;
	
	private String[] selection;
	
	public ListController(String key, String project, String labelText, boolean checkButton,int place,String tooltip,String[] content,String separator) {
		super(key, project, labelText, checkButton,place,tooltip,content,separator);
	}
    @Override
    public void createControl(Composite parent) {
    	super.createControl(parent);
    	list = new List(parent,SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        GridData data = new GridData(SWT.FILL,SWT.CENTER,true,true,3,3);
        data.horizontalIndent = DSpotProperties.INDENT.x;
        data.verticalIndent = DSpotProperties.INDENT.y;
        data.heightHint = 200;
        data.minimumHeight = 120;
        list.setLayoutData(data);
        
    	list.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {
                notifyListener();
    		}
    	});
    	
    	Button cleanButton = new Button(parent,SWT.PUSH);
    	cleanButton.setText(" Clean list ");
    	GridDataFactory.swtDefaults().span(3,1).indent(DSpotProperties.INDENT.x,1).applyTo(cleanButton);
    	cleanButton.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {
    			list.deselectAll();
    		    notifyListener();
    		}
    	});
    	if(project == null || project.isEmpty()) {
    		if(content != null && content.length > 0)
    		for(String entry : content) {
    			list.add(entry);
    		}
    	}
    	else {
    		loadProject();
    	}
    	String value = DSpotMapping.getInstance().getValue(key);
    	if(value == null) selection = new String[] {""};
    	else if(value.contains(separator))
    		selection = value.split(separator);
    	else selection = new String[] { value };
        updateController(null);
        if(firstTime) {
        	list.deselectAll();
        	firstTime = false;
        }
   if(selection == null || selection.length < 1 || selection[0].isEmpty()) list.deselectAll();
    }
	@Override
	protected void setContent(String[] content) {
		//DSpotMapping.getInstance().setValue(key,null);
		if(content == null || list == null) return;
		if(content.length < 1 || list.isDisposed()) return;
		list.removeAll();
		for(String entry : content) list.add(entry);
	}

	@Override
	protected void setSelection(String[] selection) {
		this.selection = selection;
		if(selection == null) {
			DSpotMapping.getInstance().setValue(key,null);
			return;
		}
		if(selection.length < 1) {
			DSpotMapping.getInstance().setValue(key,null);
			return;
		}
		String[] processedSelection = new String[selection.length];
		for(int i = 0; i < selection.length; i++) processedSelection[i] = processEntry(selection[i]);
		StringBuilder resultBuilder = new StringBuilder();
		resultBuilder.append(processedSelection[0]);
        for(int i = 1; i < processedSelection.length; i++) {
        	resultBuilder.append(separator);
        	resultBuilder.append(processedSelection[i]);
        }
        DSpotMapping.getInstance().setValue(key,resultBuilder.toString());
		if(list == null) return;
		if(list.isDisposed()) return;
		list.deselectAll();
		String[] items = new String[selection.length];
		for(int i = 0; i < selection.length; i++)items[i] = match(selection[i]);
		if(!items[0].isEmpty())list.setSelection(items);
		notifyListener();
	}

	@Override
	public void setEnabled(boolean enabled) {
		if(list == null) return;
		if(list.isDisposed()) return;
		list.setEnabled(enabled);
	}
	
	/**
	 * 
	 * @param entry
	 * @return
	 */
	private String processEntry(String entry) {
		if(!entry.contains("/")) return entry;
		String[] parts = entry.split("/");
		if(parts.length > 1) return parts[1];
		return entry;
	}
	@Override
	public void notifyListener() {
		String[] sel = list.getSelection();
		String result;
		if(sel == null) result = null;
		if(sel.length < 1) result = null;
		else {
			selection = sel;
			result = processEntry(sel[0]);
		for(int i = 1; i < sel.length; i++)
			result += separator + processEntry(sel[i]);
		}
		if(proxy == null) DSpotMapping.getInstance().setValue(key,result);
		else proxy.setTemporalData(result);
	}
	@Override
	public void updateController(String data) {
        if(selection == null || list == null || list.isDisposed()) return;
        if(selection.length < 1) {
        	Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					list.deselectAll();
				}
        	});
        	return;
        }
        String[] processedSelection = new String[selection.length];
        for(int i = 0; i < selection.length; i++)
        	processedSelection[i] = match(selection[i]);
        Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				list.setSelection(selection);
			}
        	
        });
}
	
	private String match(String sr) {
		String[] items = list.getItems();
		for(String item : items)if(item.contains(sr)) return item;
		return "";
	}
	
	@Override
	public int checkActivation(String condition) { 
		return 0;
	}
}