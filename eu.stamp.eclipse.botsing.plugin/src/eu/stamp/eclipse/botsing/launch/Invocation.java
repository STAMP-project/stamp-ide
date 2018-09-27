package eu.stamp.eclipse.botsing.launch;

import eu.stamp.botsing.Botsing;

public class Invocation {

	public static void main(String[] args) {
		
         Botsing botsing = new Botsing();
         botsing.parseCommandLine(args);
	}
}
