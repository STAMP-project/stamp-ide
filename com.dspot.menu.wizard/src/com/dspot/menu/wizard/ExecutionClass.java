package com.dspot.menu.wizard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.ui.console.MessageConsoleStream;

public class ExecutionClass extends Thread {

	private MessageConsoleStream out;
	private String[] parameters;
	
	public ExecutionClass(MessageConsoleStream out, String[] parameters) {
		super();
		this.out = out;
		this.parameters = parameters;
	} // end of the constructor
	
	@Override
	public void run() {
		
		out.println(" DSpot is beeing executed ");
		out.println();
	    String[] Orders = {"cmd","/C","java -jar C:\\Users\\A683946\\workspace\\com.dspot.menu.wizard\\lib\\dspot-1.0.5-jar-with-dependencies.jar -p"+parameters[1]+"\\dspot.properties -i 1 -t "+parameters[3]+" -a MethodAdd"};

		try {
			Process pro = Runtime.getRuntime().exec(Orders);
			InputStream inputStream = pro.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while((line = bufferedReader.readLine()) != null) {
				out.println(line);
			}
			System.out.println(pro.exitValue());
			inputStreamReader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}  // end of the method run
	
}
