
package eu.stamp.eclipse.plugin.dspot.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.Workbench;

import eu.stamp.eclipse.plugin.dspot.context.DSpotContext;
import eu.stamp.eclipse.plugin.dspot.controls.ControllerFactory;
import eu.stamp.eclipse.plugin.dspot.processing.DSpotMapping;
import eu.stamp.eclipse.plugin.dspot.properties.DSpotProperties;
import eu.stamp.eclipse.plugin.dspot.wizard.DSpotDialog;
import eu.stamp.eclipse.plugin.dspot.wizard.DSpotPage;
/**
 * 
 */
@SuppressWarnings("restriction")
public abstract class DSpotFileUtils {
	
	public static List<DSpotPage> parseTemplates(InputStream stream){
		List<DSpotPage> result = null;
		try {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String[] list = reader.readLine().split("/"); // TODO checking
		reader.close();
		
		InputStream wizardTemplate = null;
		for(String file : list) {
		final URL url = FileLocator.find(Platform.getBundle(
				DSpotProperties.PLUGIN_ID),new Path("files/" + file),null);
		InputStream toRead = url.openStream();
		if(file.contains("page")) parseTemplate(toRead);
		else if(file.contains("wizard")) wizardTemplate = toRead;
		}
		result = parseWizardTemplate(wizardTemplate);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * @throws IOException 
	 * 
	 */
	protected static void parseTemplate(InputStream stream) throws IOException {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(stream));
			String line;
			ControllerFactory factory = new ControllerFactory();
			while((line = reader.readLine()) != null) {
				if(line.contains("PARAMETER")) {
					factory.createController();
					factory.reset();
					if(line.contains("FILE")) factory.setFile(true);
				}
				else if(line.contains("=")){
					String[] parts = line.split("=");
					factory.setParameter(parts[0],parts[1]);
				}
			}
			reader.close();
	}
	protected static List<DSpotPage> parseWizardTemplate(InputStream stream) throws IOException{
		
		List<DSpotPage> result = new LinkedList<DSpotPage>();
		Map<String,DSpotPage> temporalMap = new HashMap<String,DSpotPage>();
		List<String> temporalDialogList = new LinkedList<String>();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line;
		
		while((line = reader.readLine()) != null) {
			if(line.contains("=") && line.contains("/") && !line.contains("#")) {
				String[] parts0 = line.split("=");
				if(parts0[0].contains("age")) {
					String[] parts = parts0[1].split("/");  // ID/name
					DSpotPage page;
					if(parts.length < 4) page = new DSpotPage(parts[1],parts[0]);
					else page = new DSpotPage(parts[1],parts[0],parts[2],parts[3]);
					temporalMap.put(parts[0],page);
					result.add(page);
				} else if(parts0[0].contains("ialog")) {
					temporalDialogList.add(parts0[1]);
				}
			}
		}
		reader.close();
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				Shell shell = Workbench.getInstance().getActiveWorkbenchWindow().getShell();
				for(String dialogString : temporalDialogList) {
					String[] parts = dialogString.split("/"); // pageID/ID/title/message
					DSpotDialog dialog = new DSpotDialog(shell,parts[2],parts[3],parts[1]);
					temporalMap.get(parts[0]).addDialog(dialog);
				}
			}
		});
		return result;
	}
    /**
     * 
     * @param file
     */
	public static void writePropertiesFile(File folder) {
	     if(!folder.exists()) folder.mkdirs();
	     if(!folder.isDirectory()) {
	    	 System.out.println("ERROR the properties folder is not a folder");
	    	 return;
	     }
	     DateFormat format = new SimpleDateFormat("DD_MM_YY_hh_mm_ss");
	     String name = "dspot" + format.format(new Date()) + ".properties";
	     File file = new File(folder.getAbsolutePath() + "/" + name);
	     try {
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write("project=" + DSpotContext.getInstance().getProject()
					.getProject().getLocation().toString());
			writer.newLine();
			String[] thingsToWrite = DSpotMapping.getInstance().getFileStrings();
			for(String thing : thingsToWrite) {
				writer.write(thing);
				writer.newLine();
			}
			writer.close();
			DSpotMapping.getInstance().setPathToProperties(file.getAbsolutePath().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}