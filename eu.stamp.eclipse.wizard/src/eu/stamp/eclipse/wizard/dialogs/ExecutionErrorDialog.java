package eu.stamp.eclipse.wizard.dialogs;

import java.io.IOException;
import java.net.URL;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public abstract class ExecutionErrorDialog extends TitleAreaDialog {

private final boolean toolError;
	
	public ExecutionErrorDialog(Shell parentShell,boolean toolError) {
		super(parentShell);
		this.toolError = toolError;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("error");
	}
	
    @Override
    protected Point getInitialSize() {
    	return new Point(647,400);
    }
	
    @Override
    public void create() {
    	super.create();
    	if(toolError) {
    	setTitle(getToolName() + " error");
    	setMessage("Error in " + getToolName() + "execution");
    	} else {
    	setTitle(getToolName() + " plugin error");
    	setMessage("Error in" + getToolName() + "plugin");
    	  }
    	}
    @Override
    protected boolean isResizable() { return true; }
    @Override
    public Control createDialogArea(Composite parent) {
    	
    	Composite container = (Composite) super.createDialogArea(parent);
    	container.setLayout(new GridLayout());
    	
    	Label infoLabel = new Label(container,SWT.NONE);
    	Link link = new Link(container,SWT.NONE);
    	
    	String direction;
    	if(toolError) {
    		direction = getToolErrorDirection();
    		infoLabel.setText(getToolErrorMessage());
    		
    	} else {
    		 direction = getPluginErrorDirection();
    	     infoLabel.setText(getPluginErrorMessage());	
    	}
    	
        link.setText("<A>"+direction+"</A>");
      	  link.addSelectionListener(new SelectionAdapter() {
      		  @Override
      		  public void widgetSelected(SelectionEvent e) {
      			  try {
						PlatformUI.getWorkbench().getBrowserSupport()
						.getExternalBrowser().openURL(new URL(direction)); 
					} catch (IOException | PartInitException e1) {
						e1.printStackTrace();
					}
      		  }
      	  });
      	  GridDataFactory.fillDefaults().indent(5,8).applyTo(infoLabel);
      	  GridDataFactory.fillDefaults().indent(5,8).applyTo(link);
    	return parent;
    }
    
    protected abstract String getToolName();

    protected abstract String getToolErrorDirection();
    
    protected abstract String getToolErrorMessage();
    
    protected abstract String getPluginErrorDirection();
    
    protected abstract String getPluginErrorMessage();
	
}
