/*******************************************************************************
 * Copyright (c) 2019 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * 	Ricardo Jose Tejada Garcia (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
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
	
	public RampJobQueue(ModelGenerationJob modelGenerationJob,Map<String,String> launchMap,String projectName,String projectLocation) {
		this(launchMap,projectName,true,projectLocation);
		if(modelGenerationJob != null) jobs.add(0,modelGenerationJob); // the model generation job must be the first in the list
	}
	
	public RampJobQueue(Map<String,String> launchMap,String projectName,String projectLocation) {
		this(launchMap,projectName,false,projectLocation);
	}
	
	private RampJobQueue(Map<String,String> launchMap, String projectName,boolean modelGeneration,String projectLocation) {
		super("Evosuite working");
		int n = launchMap.size();
		if(modelGeneration) n++;
		jobs = new ArrayList<SequentialJob>(n);
		// create jobs
        for(String className : launchMap.keySet()) {
          jobs.add(new RampJob(className,launchMap.get(className),projectName,projectLocation,0));
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
