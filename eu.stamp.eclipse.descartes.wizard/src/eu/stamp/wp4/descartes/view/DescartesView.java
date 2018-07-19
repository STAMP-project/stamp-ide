package eu.stamp.wp4.descartes.view;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;

import javax.inject.Inject;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
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
	private TabFolder tabFolder;
	/*
	 *   Objects for Pit report
	 */
	private TabItem pitItem; // TODO look for repetitions in objects storage
	private Button pitBackButton;
	private Button pitForwardButton;
	private Browser pitBrowser;
	/*
	 *  Objects for Descartes report
	 */
	private TabItem descartesItem;
	private Button descartesBackButton;
	private Button descartesForwardButton;
	private Browser descartesBrowser;
	
	@Override
	public void createPartControl(Composite parent) { this.parent = parent; 
	GridLayoutFactory.fillDefaults().numColumns(3).applyTo(this.parent);}
	

	@Override
	public void setFocus() {}
    
	/**
	 * updates the view to show a new html list
	 * @param urls : a string array with the urls of the documents
	 */
	public void setUrls(HashMap<String,File> htmls) { 
		
		String pitUrl = null;
		String descartesUrl = null;	
		
			try {
				if(htmls.containsKey("pit")) 
					pitUrl = htmls.get("pit").toURI().toURL().toString();
				if(htmls.containsKey("descartes"))
					descartesUrl = htmls.get("descartes").toURI().toURL().toString();
			} catch (MalformedURLException e1) { e1.printStackTrace(); }
		
		if(tabFolder == null) {
			tabFolder = new TabFolder(parent,SWT.BORDER);
			GridDataFactory.fillDefaults().indent(0, 5)
			.grab(true, true).applyTo(tabFolder);
		}
		
		if(pitUrl != null) {
			
			if(pitItem == null) { 
				
			pitItem = new TabItem(tabFolder,SWT.NONE);
			pitItem.setText("PITest");
			Composite pitComposite = new Composite(tabFolder,SWT.NONE);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(pitComposite);
			GridLayoutFactory.fillDefaults().numColumns(3).applyTo(pitComposite);
			
			pitBackButton = new Button(pitComposite,SWT.PUSH);
			pitBackButton.setText("back");
			GridDataFactory.swtDefaults().indent(5, 10).applyTo(pitBackButton);
			
			pitForwardButton = new Button(pitComposite,SWT.PUSH);
			pitForwardButton.setText("forward");
			GridDataFactory.swtDefaults().indent(0, 10).applyTo(pitForwardButton);
			
			pitBrowser = new Browser(pitComposite,SWT.NONE);
			GridDataFactory.fillDefaults().span(3,1).grab(true, true).minSize(200,150)
			.indent(0,5).applyTo(pitBrowser);
			
			pitBackButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) { pitBrowser.back(); }
			});
			
			pitForwardButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) { pitBrowser.forward(); }
			});
			
			pitItem.setControl(pitComposite);
			}
			pitBrowser.setUrl(pitUrl);
		}
		
		if(descartesUrl != null) {
		    
			if(descartesItem == null) {
				
				descartesItem = new TabItem(tabFolder,SWT.NONE);
				descartesItem.setText("Descartes");
				Composite descartesComposite = new Composite(tabFolder,SWT.NONE);
				GridDataFactory.fillDefaults().grab(true, true).applyTo(descartesComposite);
				GridLayoutFactory.fillDefaults().numColumns(3).applyTo(descartesComposite);
                
				descartesBackButton = new Button(descartesComposite,SWT.PUSH);
				descartesBackButton.setText("back");
				GridDataFactory.swtDefaults().indent(5,10).applyTo(descartesBackButton);
				
				descartesForwardButton = new Button(descartesComposite,SWT.PUSH);
				descartesForwardButton.setText("forward");
				GridDataFactory.swtDefaults().indent(0,10).applyTo(descartesForwardButton);
				
				descartesBrowser = new Browser(descartesComposite,SWT.NONE);
				GridDataFactory.fillDefaults().span(3,1).grab(true, true).minSize(200,150)
				.indent(0,5).applyTo(descartesBrowser);
				
				descartesBackButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) { descartesBrowser.back(); }
				});
				
				descartesForwardButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) { descartesBrowser.forward(); }
				});
				descartesItem.setControl(descartesComposite);
			}
			descartesBrowser.setUrl(descartesUrl);
		}
		parent.layout();
	}		
 }
