package eu.stamp.wp4.descartes.view;

import java.net.URL;

import javax.inject.Inject;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;

public abstract class DescartesAbstractView extends ViewPart {
	
	@Inject IWorkbench workbench;
	
	protected URL url;
	
	protected Browser browser;
	
	@Override
	public void createPartControl(Composite parent) {
          
	GridLayoutFactory.fillDefaults().numColumns(3).applyTo(parent);
	
	Button backButton = new Button(parent,SWT.PUSH);
	backButton.setText(" Back ");
	GridDataFactory.fillDefaults().applyTo(backButton);
	
	Button forwardButton = new Button(parent,SWT.PUSH);
	forwardButton.setText(" Forward ");	
	GridDataFactory.fillDefaults().applyTo(forwardButton);
	
	browser = new Browser(parent,SWT.NONE);
	GridDataFactory.fillDefaults().span(3,1).grab(true, true)
	.minSize(300,400).applyTo(browser);

	backButton.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			browser.back();
		}
	});
	
	forwardButton.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			browser.forward();
		}
	});
	
	parent.pack();
	load();
	}

	@Override
	public void setFocus() {}
	
	public void setURL(URL url) {
         this.url = url;
	}
	
	public void load() {
		if(browser != null)if(!browser.isDisposed())if(url != null)
			browser.setUrl(url.toString());
	}
}
