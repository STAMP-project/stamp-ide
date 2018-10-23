package eu.stamp.wp4.descartes.view;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;
/**
 *  This class describes the Descartes Eclipse view to display 
 *  the content of the html summaries, the view is formed by a tab,
 *  each item of the tab contains a browser to display one of the documents
 */
public class DescartesView extends ViewPart {
	
	@Inject IWorkbench workbench;

	public static final String ID = "eu.stamp.wp4.descartes.view.DescartesView";
	
	private Composite parent;
	
	private Button backButton;
	private Button forwardButton;
	private Browser browser;
	
	private HashMap<String,Visualization> visualizations;
	
	@Override
	public void createPartControl(Composite parent) { this.parent = parent; 
	GridLayoutFactory.fillDefaults().numColumns(3).applyTo(this.parent);
	
	// creating visualizations
    visualizations = new HashMap<String,Visualization>();
    createVisualization(DescartesHTML.ISSUES_NAME);
    createVisualization(DescartesHTML.PIT_NAME);
    Label space = new Label(parent,SWT.NONE);
    space.setText("");
    
	parent.layout();
	}
	
    private void createVisualization(String name) {
    	visualizations.put(name, new Visualization(parent,name));
    }
	@Override
	public void setFocus() {}
    
	/**
	 * updates the view
	 * @param htmls
	 */
	public void setFiles(List<DescartesHTML> htmls) {
		for(DescartesHTML html : htmls) {
			Visualization visualization = visualizations.get(html.getName());
			if(visualization != null) {
				visualization.load(html.getURL());
			}
		}
		// show view
		for(String name : visualizations.keySet())
			   if(visualizations.get(name).show()) break;	
	}
	private void setUrl(String url) { 
		
		if(url != null) {		
			
			if(backButton == null) { backButton = new Button(parent,SWT.PUSH);
			backButton.setText("Back");	
	        GridDataFactory.fillDefaults().applyTo(backButton);
			backButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) { browser.back(); }
			});
			  }
			
			if(forwardButton == null) { forwardButton = new Button(parent,SWT.PUSH);
			forwardButton.setText("Forward");
			GridDataFactory.fillDefaults().applyTo(forwardButton);
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
	
	private class Visualization {
		
		String url;
		final Button button;
		
		Visualization(Composite parent,String name) {
			button = new Button(parent,SWT.PUSH);
			button.setText(name);
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setUrl(url);
				}
			});
		}
		
		void load(String url) {
			if(url == null) {
				button.setEnabled(false);
				return;
			}
			this.url = url;
			button.setEnabled(true);
		}
		boolean show() {
			if(url == null) return false;
			setUrl(url);
			return true;
		}
	}
}
