package eu.stamp.wp4.dspot.dialogs;

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

public class DSpotExecutionErrorDialog extends TitleAreaDialog{

	private final boolean dspotError;
	
	public DSpotExecutionErrorDialog(Shell parentShell,boolean dspotError) {
		super(parentShell);
		this.dspotError = dspotError;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("ERROR");
	}
	
    @Override
    protected Point getInitialSize() {
    	return new Point(647,400);
    }
	
    @Override
    public void create() {
    	super.create();
    	if(dspotError) {
    	setTitle("DSpot error");
    	setMessage("Error in DSpot execution");
    	} else {
    	setTitle("DSpot plugin error");
    	setMessage("Error in DSpot plugin");
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
    	if(dspotError) {
    		direction = "https://github.com/STAMP-project/dspot/issues";
    		infoLabel.setText("An Error ocurred during DSpot execution, please check DSpot logs, "
     			   +"you can report them in : ");
    		
    	} else {
    		 direction = "https://github.com/STAMP-project/stamp-ide/issues";
    	     infoLabel.setText("An Error ocurred in DSpotPlugin, please check the Eclipse logs"
    			   +"you can report it : ");	
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
}
