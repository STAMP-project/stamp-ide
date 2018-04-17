package eu.stamp.wp4.descartes.wizard.execution;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class DescartesEclipseJob extends Job {
	
	private String projectPath;

	public DescartesEclipseJob(String projectPath) {
		super("Descartes working");
		this.projectPath = projectPath;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		String[] Orders = {"cmd","/C","mvn clean package"};
		try {
			Process process = Runtime.getRuntime().exec(Orders,null,new File(projectPath));
			InputStream inputStream = process.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			MessageConsole console = createConsole("Descartes console");
			MessageConsoleStream out = console.newMessageStream();
			while((line = bufferedReader.readLine()) != null) {
				out.println(line);   
			}
			inputStreamReader.close();
			if(!process.isAlive()) {
			 Orders[2] = "mvn org.pitest:pitest-maven:mutationCoverage -DmutationEngine=descartes";
			 process = Runtime.getRuntime().exec(Orders,null,new File(projectPath));
				inputStream = process.getInputStream();
				inputStreamReader = new InputStreamReader(inputStream);
				bufferedReader = new BufferedReader(inputStreamReader);
				while((line = bufferedReader.readLine()) != null) {
					  out.println(line);
				}
				inputStreamReader.close();}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Status.OK_STATUS;
	}
	private MessageConsole createConsole(String name) {
		IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		MessageConsole myConsole = new MessageConsole(name,null);
		consoleManager.addConsoles(new IConsole[] {myConsole});
		return myConsole;
	}
}
