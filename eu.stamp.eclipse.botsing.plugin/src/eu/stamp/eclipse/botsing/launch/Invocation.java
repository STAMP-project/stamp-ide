package eu.stamp.eclipse.botsing.launch;

import eu.stamp.botsing.Botsing;

public class Invocation {

	public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
		String[] command = {
				"-Dcrash_log=/home/ricardo/eclipse-workspace/HelloException/logs/HelloException.log",
				"-Dtarget_frame=1",
				"-projectCP",
				"/home/ricardo/eclipse-workspace/HelloException/dep/HelloException.jar"
		};
         Botsing botsing = new Botsing();
         botsing.parseCommandLine(command);

	}

}
