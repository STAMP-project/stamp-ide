package eu.stamp.eclipse.descartes.wizard.dialogs;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import eu.stamp.eclipse.descartes.wizard.interfaces.IDescartesConfigurablePart;

public class OutputFormatsDialog extends TitleAreaDialog 
                         implements IDescartesConfigurablePart{
	
	private List<OutputFormat> formats;

	public OutputFormatsDialog(Shell parentShell) {
		super(parentShell);
		formats = new LinkedList<OutputFormat>();
		
	 // Edit this array to add and remove formats
       String[] formatsNames = {"HTML","METHODS","ISSUES"};
       
       for(String name : formatsNames)
      	 createFormat(name);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Output formats");
		setMessage("Select output options");
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
   	     Composite composite = (Composite)super.createDialogArea(parent);
		 GridLayout layout = new GridLayout(2,true);
		 composite.setLayout(layout);
		 
		 Label space = new Label(composite,SWT.NONE);
		 space.setText("");		 
		 for(OutputFormat format : formats)
			 format.createButton(composite);
		 
		 return composite;
	}
	
	@Override
	public void okPressed() {
		super.okPressed();
		for (OutputFormat format : formats)
			format.saveState();
	}
	
	@Override
 	protected void configureShell(Shell shell) {  // set the title
 		super.configureShell(shell);
 		shell.setText(" Add mutator ");
 	}
	
	private void createFormat(String name) {
		OutputFormat format = new OutputFormat(name);
		formats.add(format);
	}
	
	public List<String> getFormatList(){
		List<String> result = new LinkedList<String>();
		for(OutputFormat format : formats)
			if(format.selected) result.add(format.name);
		return result;
	}
	
	private class OutputFormat {
		
		String name;
		Button button;
		boolean selected;
		boolean provisionalSelection;
		
		OutputFormat(String name) { 
			this.name = name; 
			selected = true;
			provisionalSelection = true;
			}
		
		void createButton(Composite composite){
			button = new Button(composite,SWT.CHECK);
			button.setText(" " + name + " : ");
			button.setSelection(selected);
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					provisionalSelection = button.getSelection();
				}
			});
		}
         void saveState() { selected = provisionalSelection; }
	}

	@Override
	public void appendToConfiguration(ILaunchConfigurationWorkingCopy copy) {
		for(OutputFormat format : formats)
			copy.setAttribute(format.name,String.valueOf(format.selected));
	}

	@Override
	public void load(ILaunchConfigurationWorkingCopy copy) {
		boolean boo = true;
		for(OutputFormat format : formats) {
			try {
				String sr = copy.getAttribute(format.name,"true");
				boo = sr.contains("ru");
			} catch (CoreException e) {
				e.printStackTrace();
			}
			format.selected = boo;
		}
	}
}
