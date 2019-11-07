package eu.stamp.eclipse.botsing.model.generation.launch;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class ModelGenerationJob extends Job {
	
	private final String arguments;

	public ModelGenerationJob(String arguments) {
		super("Botsing models generation");
		this.arguments = arguments;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		
		return Status.OK_STATUS;
	}
   
	
}
