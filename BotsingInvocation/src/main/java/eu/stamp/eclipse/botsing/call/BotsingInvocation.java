
package eu.stamp.eclipse.botsing.call;

import java.io.File;
import java.io.IOException;

import eu.stamp.botsing.Botsing;

public class BotsingInvocation {

	public static void main(String[] args) {

		if(args.length != 1) {
			System.out.println("Error in Botsing invocation args.length != 1");
			return;
		}
		
           InputManager input = new InputManager();
           input.loadFromString(args[0]);
           
           String[] command = input.getCommand();
           
           if(!input.outputFileSet()) {
        	   Botsing.main(command);
        	   return;
           }
           
           String path = input.getoutputFilePath();
		
		if(path != null)if(!path.isEmpty()){
        File file = new File(path);
		if(file.getParentFile() != null)
			if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
			
			try {
		    if(!file.exists())file.createNewFile();
			
			System.out.println("\n----- COMMAND -----\n");
			for(String sr : command) System.out.println(sr);
			System.out.println("\n---END---\n");
			
			Botsing.main(command);
			
			return;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
	}	
}
