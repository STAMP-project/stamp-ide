package eu.stamp.wp4.descartes.view;

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
/**
 *  This class describes the Descartes Eclipse view to display 
 *  the content of the html summaries, the view is formed by a tab,
 *  each item of the tab contains a browser to display one of the documents
 *
 */
public class DescartesView extends ViewPart {
	
	@Inject IWorkbench workbench;

	public static final String ID = "eu.stamp.wp4.descartes.view.DescartesView";
	
	private Composite parent;
	
	private Button backButton;
	private Button forwardButton;
	private Browser browser;
	
	
	@Override
	public void createPartControl(Composite parent) { this.parent = parent; 
	GridLayoutFactory.fillDefaults().numColumns(3).applyTo(this.parent);}
	

	@Override
	public void setFocus() {}
    
	/**
	 * updates the view to show a new html list
	 * @param urls : a string array with the urls of the documents
	 */
	public void setUrls(String[] urls) { 
		
		String url = null;
		for(String sr : urls)if(sr.contains("index")) { url = sr; break; }
		
		if(url != null) {		
			
			if(backButton == null) { backButton = new Button(parent,SWT.PUSH);
			backButton.setText("Back");	
			GridDataFactory.swtDefaults().applyTo(backButton);
			backButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) { browser.back(); }
			});
			  }
			
			if(forwardButton == null) { forwardButton = new Button(parent,SWT.PUSH);
			forwardButton.setText("Forward");
			GridDataFactory.swtDefaults().applyTo(forwardButton);
			forwardButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) { browser.forward(); }
			});
			  }
			
			if(browser == null) { browser = new Browser(parent,SWT.NONE);
			GridDataFactory.fillDefaults().span(3,1).grab(true, true).minSize(200,150)
			.applyTo(browser); }
			
			browser.setUrl(url);
			}	
		parent.layout();
		}
}
