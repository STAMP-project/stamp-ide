package eu.stamp.eclipse.botsing.invocation;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

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

		/*String[] command = {
				"-Dpopulation=100",
				"-Dsearch_budget=1800",
				"-Dmax_recursion=30",
				"-Dtest_dir=/home/ricardo/eclipse-workspace/ExceptionsForBotsing/tests",
				"-crash_log",
				"/home/ricardo/eclipse-workspace/ExceptionsForBotsing/logs/index_example.log",
				"-target_frame",
				"2",
				"-projectCP",
				"/home/ricardo/eclipse-workspace/ExceptionsForBotsing/target"
		};*/
		//String path = "/home/ricardo/eclipse-workspace/ExceptionsForBotsing/tests/out.txt";
		
		if(path != null)if(!path.isEmpty()){
        File file = new File(path);
		if(file.getParentFile() != null)
			if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
			
			try {
		    if(!file.exists())file.createNewFile();
		    
		    PrintStream original = System.out;
		    PrintStream doubleStream = new DoublePrintStream(original,file);
			
		    System.setOut(doubleStream);
			System.setErr(doubleStream);
			
			Botsing.main(command);
			
			System.setOut(original);
			System.setErr(original);
			
			return;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		Botsing.main(command);
	}	
}
