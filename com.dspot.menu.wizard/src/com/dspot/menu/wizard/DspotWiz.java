package com.dspot.menu.wizard;


import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;


public class DspotWiz extends Wizard{
	
	protected DsPa1 one;
	protected DsPa2 two;
	// [0] Dspot jar path, [1] proyect path [2] number of iterations i, [3] -t testclass, [4] -a Method
	private String[] parameters = new String[5];   // this will be the execution parameters
	
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
		writeTheFile(one);    // writing the properties file
        MessageConsole MyConsole = createConsole("My Console");  // obtaining the console of the eclipse application
        MyConsole.activate();
        MessageConsoleStream out = MyConsole.newMessageStream();
        String[] MyS = two.getMyStrings();
        parameters[2] = MyS[0]; parameters[3] = MyS[1]; parameters[4] = MyS[2];
		ExecutionClass doIt = new ExecutionClass(out,parameters);  // the thread class to execute Dspot
		doIt.start();
		return true;
	}
	
	
	private void writeTheFile(DsPa1 Pa) {   
		
		
		
		// this is the method to write the dspot.properties                        
    // it uses the information in page 1 and it is called by performFinish
		String[] Values = Pa.getTheProperties();
		String[] Keys = {"project","src","testSrc","javaVersion","outputDirectory","filter"};
		
		
			String p = MyHan.getPro().getProject().getLocation().toString();
			parameters[1] = p;  // this will be set when perform finish will use it
			File file = new File(p+"\\dspot.properties");
			try {
			file.createNewFile();
			BufferedWriter fw = new BufferedWriter(new FileWriter(file));
			fw.write("# Properties File #");
			fw.newLine();
			if(Values[4] == null || Values[4] == "") {   // use the default output directory
				Values[4] = "dspot-out/";
			}
			Values[4] = p+Values[4];
			if(Values[5] == null) { Values[5] = ""; } // if there is no filter
			for(int i = 0; i < Values.length; i++){
				fw.write(Keys[i]+"="+Values[i]);
				fw.newLine();
			}  // end of the for
			fw.close();
			} catch(IOException ioe) {ioe.printStackTrace();}		
	}    // end of writTheFile
	
	
	private MessageConsole createConsole(String name) {  // this will show the console information in the eclipse application console

	      ConsolePlugin plugin = ConsolePlugin.getDefault();
	      IConsoleManager conMan = plugin.getConsoleManager();
	       MessageConsole myConsole = new MessageConsole(name,null);
	      conMan.addConsoles(new IConsole[] {myConsole});
	      return myConsole;
		
	}  // end of the method create console
}
	

