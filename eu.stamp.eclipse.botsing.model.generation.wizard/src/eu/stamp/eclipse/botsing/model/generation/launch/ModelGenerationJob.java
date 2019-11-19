package eu.stamp.eclipse.botsing.model.generation.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import eu.stamp.eclipse.botsing.model.generation.constants.ModelgenerationPluginConstants;

public class ModelGenerationJob extends Job {
	
	private final String arguments;
	
	private IProcess[] processes;

	public ModelGenerationJob(String arguments) {
		super("Botsing models generation");
		this.arguments = arguments;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		try {
			ILaunchConfigurationWorkingCopy launchCopy = 
					DebugPlugin.getDefault().getLaunchManager()
			.getLaunchConfigurationType(ModelgenerationPluginConstants.LAUNCH_TYPE_ID)
			.newInstance(null,"Model generation");
			
			launchCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
					"-Xmx4000m");
			
			launchCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
					ModelgenerationPluginConstants.GENERATION_MAIN);
			
			launchCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
					arguments);
			
			ILaunchConfiguration launchConfiguration = launchCopy.doSave();
			ILaunch launch = launchConfiguration.launch(ILaunchManager.RUN_MODE,null);
			processes = launch.getProcesses();
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return Status.OK_STATUS;
	}
	
	public boolean isWorking() {
		if(processes == null) return false;
		for(IProcess process : processes)if(!process.isTerminated())return true;
		return false;
	}
   
	
}
