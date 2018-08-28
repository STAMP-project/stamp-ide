package eu.stamp.eclipse.dspot.wizard.page.utils;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

public class DSpotSizeManager {

private static DSpotSizeManager sizeManager;

private int x;
private int y; 

private DSpotSizeManager() {
x = 0; y = 0;
}

public void addPage(DSpotPageSizeCalculator page) {
if(page.getX() > x) x = page.getX();
if(page.getY() > y) y = page.getY();
}

public void configureWizardSize(Wizard wizard) {
WizardDialog dialog = new WizardDialog(
PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),wizard);
dialog.setPageSize(x + 50, y + 100);
}

public static DSpotSizeManager getInstance() {
if(sizeManager == null) sizeManager = new DSpotSizeManager();
return sizeManager;
}

}
