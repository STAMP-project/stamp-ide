package eu.stamp.wp4.descartes.view;

import javax.inject.Inject;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;

public class DescartesView extends ViewPart {
	
	@Inject IWorkbench workbench;

	public static final String ID = "eu.stamp.wp4.descartes.view.DescartesView";
	
	private Composite parent;
	
	@Override
	public void createPartControl(Composite parent) { this.parent = parent; }
	

	@Override
	public void setFocus() {}
    
	public void setUrls(String[] urls) { 
		if(urls != null) {
			GridLayoutFactory.fillDefaults().applyTo(parent);
	        Browser[] browsers = new Browser[urls.length];
			for(int i = 0; i < urls.length; i++) {
				browsers[i] = new Browser(parent,SWT.NONE);
				browsers[i].setUrl(urls[i]);
				GridDataFactory.fillDefaults().grab(true, true).minSize(200,150)
				.applyTo(browsers[i]);
				parent.layout();}}
		}
}
