package eu.stamp.wp4.dspot.execution.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

public class DSpotLaunchConfigurationTableDelegate 
extends DSpotLaunchConfigurationDelegate {

@Override
public void launch(ILaunchConfiguration configuration, String mode, 
		ILaunch launch, IProgressMonitor monitor) {
	try {
		super.launch(configuration, mode, launch, monitor);
		System.out.println("DSpot called from Tab");
	} catch (CoreException e) {
		e.printStackTrace();
	}
}
	
}
