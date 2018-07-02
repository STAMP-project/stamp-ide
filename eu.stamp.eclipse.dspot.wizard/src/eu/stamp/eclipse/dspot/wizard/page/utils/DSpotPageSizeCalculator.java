package eu.stamp.eclipse.dspot.wizard.page.utils;

public final class DSpotPageSizeCalculator {
	
	private int x;
	private int y;
	
	public DSpotPageSizeCalculator() {
		x = 0; y = 0;
	}
	
	public void addRow(DSpotRowSizeCalculator row) {
		if(row.getX() > x) x = row.getX();
        y = y + row.getY();
	}
	
	public int getX() { return x; }
	
	public int getY() { return y; }

}
