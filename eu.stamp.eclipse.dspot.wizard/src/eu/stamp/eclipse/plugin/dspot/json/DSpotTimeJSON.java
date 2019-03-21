package eu.stamp.eclipse.plugin.dspot.json;

import java.util.List;

public class DSpotTimeJSON {
public String projectName;
public List<DSpotClassTime>classTimes;

public class DSpotClassTime {

public String fullQualifiedName;
public int timeInMs;
}

public int getClassTime(String fullQualifiedName) { // TODO
for(DSpotClassTime time : classTimes)if(time.fullQualifiedName
.equalsIgnoreCase(fullQualifiedName)) return time.timeInMs; 
return 0;
}
}
