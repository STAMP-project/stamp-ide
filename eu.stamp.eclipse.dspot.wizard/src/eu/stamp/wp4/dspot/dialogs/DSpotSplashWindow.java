package eu.stamp.wp4.dspot.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class DSpotSplashWindow extends TitleAreaDialog {

	private final int max;
	private ProgressBar progressBar;
	
	public DSpotSplashWindow(Shell parentShell,int max) {
		super(parentShell);
		this.max = max;
	}
	
	@Override
	public void create() {
		super.create();
		setTitle("Loading project data");
	}
	
	@Override
	public void configureShell(Shell parent) {
		super.configureShell(parent);
		parent.setText("Loading");
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(647,400);
	}
	
	@Override
	public Control createDialogArea(Composite parent) {
		
		Composite composite = (Composite)super.createDialogArea(parent);
        GridLayoutFactory.fillDefaults().applyTo(composite);
		
		Label label = new Label(composite,SWT.NONE);
		label.setText("Inspecting the project : ");
		
		ProgressBar progressBar = new ProgressBar(composite,SWT.BORDER);
		progressBar.setMaximum(max);
		progressBar.setSelection(0);
		progressBar.setVisible(true);
		GridDataFactory.fillDefaults().grab(true, false).indent(5, 6).applyTo(progressBar);
		
		composite.pack();
		return composite;
	}
    
	public void update(int data) {
		if(progressBar == null) return;
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if(progressBar.isDisposed()) return;
				progressBar.setSelection(data);
			}
		});
	}

}
