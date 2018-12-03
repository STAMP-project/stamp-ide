package eu.stamp.eclipse.botsing.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LogReader {

	private File logFile;
	
	public void setFile(File logFile) { this.logFile = logFile; }
	
	public void setFile(String path) { logFile = new File(path); }
	
	public int getFrameLevel() {
		
		if(logFile == null) return 0;
		if(!logFile.exists()) return 0;
		
		int n = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(logFile));
			String line;
			while((line = reader.readLine()) != null) {
				line = line.replaceAll(" ","");
				if(!line.isEmpty()) n++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		n--;
		if(n > -1) return n;
		return 0;
	}
	
}
