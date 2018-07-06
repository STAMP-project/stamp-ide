package eu.stamp.eclipse.dspot.wizard.page.utils;

public class DSpotPageSizeCalculator {
	
	private int x;
	private int y;
	private boolean finish;
	
	public DSpotPageSizeCalculator() {
		x = 0; y = 0; finish = false;
	}
	
	public void addRow(DSpotRowSizeCalculator row) {
		if(finish) return;
		if(row.getX() > x) x = row.getX();
        y = y + row.getY();
        //System.out.println(String.valueOf(x) + "  " + String.valueOf(y));
	}
	
	public int getX() { finish = true; return x; }
	
	public int getY() { return y; }

}
