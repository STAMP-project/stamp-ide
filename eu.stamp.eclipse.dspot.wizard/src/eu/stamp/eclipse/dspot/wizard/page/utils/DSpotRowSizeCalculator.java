package eu.stamp.eclipse.dspot.wizard.page.utils;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

public class DSpotRowSizeCalculator {

private int x;
private int y;

private int y0;
private int x0; 

public DSpotRowSizeCalculator() {
x = 0; y = 0; x0 = 80; y0 = 80;
}
public void reStart() {
x = 0; y = 0;
}
public void addWidget(Control control) {

if(control instanceof Label) {
y = 10; x = ((Label)control).getText().length()*10;
return;
}
if(control instanceof List) {
List list = (List)control;
String[] strings = list.getItems();
String sr = "";
for(String s : strings)if(s.length() > sr.length()) sr = s;
x = sr.length()*10;
y = 100;
return;
}
Point point = control.computeSize(x0,y0);
x = x + point.x;
if(x > x0 && x < 200) x0 = x;
if(point.y > y && y < 200) y = point.y;
if(y > y0) y0 = y;
}
public int getX() { return x; }

public int getY() { return y; }
}
