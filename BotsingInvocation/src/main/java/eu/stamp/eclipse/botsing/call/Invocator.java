package eu.stamp.eclipse.botsing.call;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import eu.stamp.botsing.Botsing;

public class Invocator {
	
	public static void main(String[] args) {

		String[] myArgs = new String[args.length - 1];
		for(int i = 0; i < args.length - 1; i++) myArgs[i] = args[i];
		
		String[] botsingArg = processArgs(myArgs);
		File outputFile = new File(args[args.length-1]);
		PrintStream out = System.out;
		try {
			DoublePrintStream myDoubleStream = new DoublePrintStream(out,outputFile);
			System.setOut(myDoubleStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Botsing.main(botsingArg);
		System.setOut(out);
	}
	
	private static String[] processArgs(String[] toProcess) {
           String sr = "";
           String[] result = new String[toProcess.length];
           String element;
           for(int i = 0; i < toProcess.length; i++) {
        	   element = toProcess[i];
        	   if(element.contains(".0") && !sr.contains("bject")) result[i] = element.split(".0")[0];
        	   else if(element.contains("-Dp_object_pool=10")) result[i] = element.replaceAll("10","1");
        	   else result[i] = toProcess[i];
        	   sr = element;
           }
           return result;
	}
}
