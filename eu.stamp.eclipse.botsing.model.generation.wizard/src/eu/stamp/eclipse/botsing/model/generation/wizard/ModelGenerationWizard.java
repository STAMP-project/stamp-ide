package eu.stamp.eclipse.botsing.model.generation.wizard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.wizard.Wizard;

import eu.stamp.eclipse.botsing.model.generation.constants.ModelGenerationLaunchConstants;

public class ModelGenerationWizard extends Wizard {
	
	private final Map<String,String> map;
	
	public ModelGenerationWizard() {
		
	super();
		
	map = new HashMap<String,String>();
	
	map.put(ModelGenerationLaunchConstants.PROJECT_CLASS_PATH,"");
	map.put(ModelGenerationLaunchConstants.PROJECT_PREFIX,"");
	map.put(ModelGenerationLaunchConstants.OUT_DIR,ModelGenerationLaunchConstants.OUT_DIR_DEFAULT);
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
	
	@Override
	public boolean performFinish() {
		// TODO
		return true;
	}

}
