package eu.stamp.eclipse.botsing.model.generation.launch;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;

public abstract class SequentialJob extends Job {
	
	protected IProcess[] processes;
	
	protected ILaunchConfiguration launchConfiguration;

	public SequentialJob(String name) { super(name); }

	public boolean isWorking() {
		if(processes == null) return false;
		for(IProcess process : processes)if(!process.isTerminated()) return true;
		return false;
	}
	
	public void delete() {
		/*if(launchConfiguration == null) return;
		try {
			launchConfiguration.delete();
		} catch (CoreException e) {
			e.printStackTrace();
		}*/
	}
}
