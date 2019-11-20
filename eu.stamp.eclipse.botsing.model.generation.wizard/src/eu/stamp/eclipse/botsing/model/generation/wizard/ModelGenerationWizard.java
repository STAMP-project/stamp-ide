package eu.stamp.eclipse.botsing.model.generation.wizard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.wizard.Wizard;

import eu.stamp.eclipse.botsing.model.generation.constants.ModelGenerationLaunchConstants;
import eu.stamp.eclipse.botsing.model.generation.launch.ModelGenerationJob;

public class ModelGenerationWizard extends Wizard {
	
	private final Map<String,String> map;
	
	private ModelGenerationJob job;
	
	private final IJavaProject project;
	
	public ModelGenerationWizard(IJavaProject project) {
		
	super();
		
	map = new HashMap<String,String>();
	
	map.put(ModelGenerationLaunchConstants.PROJECT_CLASS_PATH,"");
	map.put(ModelGenerationLaunchConstants.PROJECT_PREFIX,"");
	map.put(ModelGenerationLaunchConstants.OUT_DIR,ModelGenerationLaunchConstants.OUT_DIR_DEFAULT);
	
	this.project = project;
	}
	
	@Override
	public void addPages() {
		ModelGenerationWizardPage page = new ModelGenerationWizardPage(map);
		addPage(page);
	}
	
	@Override
	public String getWindowTitle() {
		return "Botsing model generation";
	}
	/**
	 *  this wizard provide the job to other plugins in order to execute it in the
	 *  right moment, it dosn't execute the job
	 */
	@Override
	public boolean performFinish() {
		StringBuilder builder = new StringBuilder();
		for(String key : map.keySet())if(!map.get(key).isEmpty())
			builder.append(key).append(' ').append(map.get(key)).append(' ');
		job = new ModelGenerationJob(builder.toString(),project);
		return true;
	}
	
	public ModelGenerationJob getJob() { return job; }

}
