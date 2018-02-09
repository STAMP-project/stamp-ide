package com.dspot.menu.wizard;


import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.lang.Runtime;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;


public class DspotWiz extends Wizard{
	
	protected DsPa1 one;
	protected DsPa2 two;
	
	public DspotWiz() {
		super();
		setNeedsProgressMonitor(true);
		setHelpAvailable(true);
	} // end of the constructor
	
	@Override
	public String getWindowTitle() { 
		return "Dspot Wizard";
	}
	
	
	@Override
	public void addPages() {
		one = new DsPa1();
		addPage(one);
		two = new DsPa2();
		addPage(two);
	}
	
	@Override
	public boolean performFinish() {
		writeTheFile(one);
        MessageConsole MyConsole = createConsole("My Console");
        MyConsole.activate();
        MessageConsoleStream out = MyConsole.newMessageStream();
		//executeOrders(out);
		return true;
	}
	
	
	private void writeTheFile(DsPa1 Pa) {   
		
		
		
		// this is the method to write the dspot.properties                        
    // it uses the information in page 1 and it is called by performFinish
		String[] Values = Pa.getTheProperties();
		String[] Keys = {"project","src","testSrc","javaVersion","outputDirectory","filter"};
		
		
			Path p = Paths.get(MyHan.getPro().getProject().getLocation()+"\\dspot.properties");
			File file = new File(p.toString());
			try {
			file.createNewFile();
			BufferedWriter fw = new BufferedWriter(new FileWriter(file));
			fw.write("# Properties File #");
			fw.newLine();
			if(Values[4] == null || Values[4] == "") {   // use the default output directory
				Values[4] = "dspot-out/";
			}
			if(Values[5] == null) { Values[5] = ""; } // if there is no filter
			for(int i = 0; i < Values.length; i++){
				fw.write(Keys[i]+"="+Values[i]);
				fw.newLine();
			}  // end of the for
			fw.close();
			} catch(IOException ioe) {ioe.printStackTrace();}		
	}    // end of writTheFile
	
	private void executeOrders(MessageConsoleStream out) {  // this method executes dspot
		
	    String[] Orders = {"cmd","/C","java -jar C:\\Users\\A683946\\PROJECT\\dspot\\dspot\\target\\dspot-1.0.5-jar-with-dependencies.jar -p C:\\Users\\A683946\\PROJECT\\dhell\\dspot.properties -i 1 -t fr.inria.stamp.examples.dhell.HelloAppTest -a MethodAdd"};

		try {
			Process pro = Runtime.getRuntime().exec(Orders);
			InputStream inputStream = pro.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
			}
			System.out.println(pro.exitValue());
			inputStreamReader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	} // end of executeOrders
	
	private MessageConsole createConsole(String name) {  // this will show the console information in the eclipse application console

	      ConsolePlugin plugin = ConsolePlugin.getDefault();
	      IConsoleManager conMan = plugin.getConsoleManager();
	       MessageConsole myConsole = new MessageConsole(name,null);
	      conMan.addConsoles(new IConsole[] {myConsole});
	      return myConsole;
		
	}  // end of the method create console
}
	

