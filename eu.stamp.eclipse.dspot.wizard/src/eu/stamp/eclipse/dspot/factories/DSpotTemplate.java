package eu.stamp.eclipse.dspot.factories;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.google.gson.Gson;

import eu.stamp.eclipse.plugin.dspot.properties.DSpotProperties;
import eu.stamp.eclipse.plugin.dspot.wizard.DSpotDialog;
import eu.stamp.eclipse.plugin.dspot.wizard.DSpotPage;

public class DSpotTemplate {
	
	public List<DSpotPageTemplate> pages;

	public List<ControlsFactory2> factories;
	
	public static List<DSpotPage> parseTemplate(){
		try {
			DSpotTemplate template = parse();
			template.activateControllers();
			return template.generatePages();
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<DSpotPage>(1);
		}
	}
	
	private static DSpotTemplate parse() throws IOException {
		
     URL url = FileLocator.find(Platform.getBundle(
	 DSpotProperties.PLUGIN_ID),new Path("files/dspot_template.json"),null);
				
       String line;
	   StringBuilder builder = new StringBuilder();
	   BufferedReader reader = new BufferedReader(
	   new InputStreamReader(url.openStream()));
	   while((line = reader.readLine()) != null) builder.append(line);
	   reader.close();
				
	   Gson gson = new Gson();
	   return gson.fromJson(builder.toString(),DSpotTemplate.class);
			}
	
	public List<DSpotPage> generatePages(){
		List<DSpotPage> result = new ArrayList<DSpotPage>(pages.size());
		for(DSpotPageTemplate template : pages) result.add(template.createPage());
		return result;
	}
	
    private void activateControllers() {
    	for(ControlsFactory2 factory : factories) factory.createController();
    }
	
	// Nested classes
	public class DSpotPageTemplate {
		public String ID;
		public String name;
		public List<DialogTemplate> dialogs;
		
		public DSpotPage createPage() {
			DSpotPage result = new DSpotPage(name,ID);
			for(DialogTemplate dialogTemplate : dialogs) 
				result.addDialog(dialogTemplate.createDialog());
			return result;
		}
	}
	
	public class DialogTemplate implements Runnable {
		
		public String ID;
		public String title;
		public String message;
		
		private DSpotDialog dialog;
		
		public DSpotDialog createDialog() {
          Display.getDefault().syncExec(this);
          return dialog;
		}
		
		@Override
		public void run() {
			dialog = new DSpotDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getShell(),title,message,ID);
		}
	}
}
