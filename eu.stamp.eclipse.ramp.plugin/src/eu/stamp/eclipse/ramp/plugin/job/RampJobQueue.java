package eu.stamp.eclipse.ramp.plugin.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import eu.stamp.eclipse.botsing.model.generation.launch.ModelGenerationJob;
import eu.stamp.eclipse.botsing.model.generation.launch.SequentialJob;

public class  RampJobQueue extends Job {

	private List<SequentialJob> jobs;
	
	public RampJobQueue(ModelGenerationJob modelGenerationJob,Map<String,String> launchMap,String projectName) {
		this(launchMap,projectName,true);
		if(modelGenerationJob != null) jobs.add(0,modelGenerationJob); // the model generation job must be the first in the list
	}
	
	public RampJobQueue(Map<String,String> launchMap,String projectName) {
		this(launchMap,projectName,false);
	}
	
	private RampJobQueue(Map<String,String> launchMap, String projectName,boolean modelGeneration) {
		super("Evosuite working");
		int n = launchMap.size();
		if(modelGeneration) n++;
		jobs = new ArrayList<SequentialJob>(n);
		// create jobs
        for(String className : launchMap.keySet()) {
          jobs.add(new RampJob(className,launchMap.get(className),projectName,0));
        }
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		for(SequentialJob job : jobs) {
			try {
			     job.schedule();
			     System.out.println(job.toString());
			     job.join();
			} catch(ClassCastException | InterruptedException e) { 
				e.printStackTrace(); // Model generation may throws this exception
				}
			while(job.isWorking()) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		for(SequentialJob job : jobs)job.delete();
		return Status.OK_STATUS;
	}

}
