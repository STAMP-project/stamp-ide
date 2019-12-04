package eu.stamp.eclipse.botsing.model.generation.wizard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.wizard.Wizard;

import eu.stamp.eclipse.botsing.model.generation.constants.ModelGenerationLaunchConstants;
import eu.stamp.eclipse.botsing.model.generation.launch.ModelGenerationJob;
import eu.stamp.eclipse.botsing.model.generation.load.GenerationConfigurationLoader;

public class ModelGenerationWizard extends Wizard {
	
	private final Map<String,String> map;
	
	private ModelGenerationJob job;
	
	private final IJavaProject project;
	
	private GenerationConfigurationLoader loader;
	
	public ModelGenerationWizard(IJavaProject project) {
		this(project,"");
	}
	
	public ModelGenerationWizard(IJavaProject project, String projectClassPath) {
		
	super();
	
	if(projectClassPath == null)projectClassPath = "";
		
	map = new HashMap<String,String>();
	
	map.put(ModelGenerationLaunchConstants.PROJECT_CLASS_PATH,projectClassPath);
	map.put(ModelGenerationLaunchConstants.PROJECT_PREFIX,"");
	map.put(ModelGenerationLaunchConstants.OUT_DIR,ModelGenerationLaunchConstants.OUT_DIR_DEFAULT);
	
	this.project = project;
	
	loader = new GenerationConfigurationLoader();
	}
	
	public GenerationConfigurationLoader getLoader() { return loader; }
	
	@Override
	public void addPages() {
		ModelGenerationWizardPage page = new ModelGenerationWizardPage(map,this);
		addPage(page);
	}
	
	@Override
	public String getWindowTitle() {
		return "Behavioral model generation";
	}
	/**
	 *  this wizard provide the job to other plugins in order to execute it in the
	 *  right moment, it dosn't execute the job, the job will be null if the classpath or project prefix is not set
	 */
	@Override
	public boolean performFinish() {
		
		// check
		String target = map.get(ModelGenerationLaunchConstants.PROJECT_CLASS_PATH);
		if(target == null || target.replaceAll(" ","").isEmpty()) {
			job = null;
			return true;
		}
		target = map.get(ModelGenerationLaunchConstants.PROJECT_PREFIX);
		if(target == null || target.replaceAll(" ","").isEmpty()) {
			job = null;
			return true;
		}
		
		// crete job
		StringBuilder builder = new StringBuilder();
		for(String key : map.keySet())if(!map.get(key).isEmpty())
			builder.append(key).append(' ').append(map.get(key)).append(' ');
		job = new ModelGenerationJob(builder.toString(),project);
		loader.update(map);
		return true;
	}
	
	public ModelGenerationJob getJob() { return job; }

	public void setLoader(GenerationConfigurationLoader loader) {
		this.loader = loader;
	}

}
