package eu.stamp.eclipse.dspot.wizard.page.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

public class DSpotRowSizeCalculator {

	private int x;
	private int y;
	
	public DSpotRowSizeCalculator() {
		x = 0; y = 0;
	}
	public void addWidget(Control control) {
		Point point = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		x = x + point.x;
		if(point.y > y) y = point.y;
	}
	public int getX() { return x; }
	
	public int getY() { return y; }
}
