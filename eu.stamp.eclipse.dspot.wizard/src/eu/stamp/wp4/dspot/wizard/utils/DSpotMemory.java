package eu.stamp.wp4.dspot.wizard.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class DSpotMemory {

	private HashMap<String,String> DSpotMap = new HashMap<String,String>();
	
	private boolean[] booleanParameters = {false,false}; // [0] verbose [1] clean
	
	public static final String ITERATIONS_KEY = " -i ";
	public static final String RANDOMSEED_KEY = " --randomSeed ";
	public static final String AMPLIFIERS_KEY = " -a ";
	public static final String TIMEOUT_KEY = " --timeOut ";
	public static final String TEST_CASES_KEY = " -c ";
	public static final String MAX_TEST_KEY = " -g ";
	public static final String TEST_CLASSES_KEY = " -t ";
	public static final String CRITERION_KEY = " -s ";
	public static final String PATH_PIT_RESULT_KEY = " -m ";
	public static final String MAVEN_HOME_KEY = " --maven-home ";
	
	public String separator;
	
	public DSpotMemory(String separator){
		this.separator = separator;
		DSpotMap.put(ITERATIONS_KEY, String.valueOf(1));
		DSpotMap.put(RANDOMSEED_KEY, null);
		DSpotMap.put(AMPLIFIERS_KEY, "MethodAdd");
		DSpotMap.put(TIMEOUT_KEY, null);
		DSpotMap.put(TEST_CASES_KEY, null);
		DSpotMap.put(MAX_TEST_KEY, null);
		DSpotMap.put(TEST_CLASSES_KEY, null);	
		DSpotMap.put(CRITERION_KEY, null);
		DSpotMap.put(PATH_PIT_RESULT_KEY, null);
		DSpotMap.put(MAVEN_HOME_KEY, null);
	}
	
	public String getAsString() {
		String information = "";
		String[] keys = DSpotMap.keySet().toArray(new String[DSpotMap.keySet().size()]);
		String value;
		for(String key : keys) {
			value = DSpotMap.get(key);
			if(value != null && !value.isEmpty()) {
		
			information = information + key + value;
             }}
		if(booleanParameters[0])information = information + "--verbose ";
		if(booleanParameters[1])information = information +"--clean ";
		return information;
	}
	
	public DSpotMemory resetFromString (String information) {
		String[] keys = DSpotMap.keySet().toArray(new String[DSpotMap.keySet().size()]);
		String fragment;
		for(String key : keys)if(information.contains(key)) {
			fragment = information.substring(information.indexOf(key)+key.length());
			if(fragment.contains("-"))fragment = fragment.substring(0,fragment.indexOf("-"));
			fragment = fragment.replaceAll(" ", "");
			DSpotMap.put(key, fragment);
		}
		booleanParameters[0] = information.contains("--verbose");
		booleanParameters[1] = information.contains("--clean");
		return this;
	}
	
	public String getDSpotValue(String key){
		return DSpotMap.get(key);
	}
	
	public void setDSpotValue(String key,String value) {
		if(key.contains("verbose")) {
			booleanParameters[0] = value.contains("true");
			return;
		}
		if(key.contains("clean")) {
			booleanParameters[1] = value.contains("true");
			return;
		}
		if(DSpotMap.containsKey(key)) DSpotMap.put(key, value);
	}
	public String[] getSelectedCasesAsArray() {
		ArrayList<String> list = new ArrayList<String>(1);
		String cases = DSpotMap.get(TEST_CASES_KEY);
		
		if(cases != null) while(cases.contains(separator)) {
			list.add(cases.substring(0, cases.indexOf(separator)));
			cases = cases.substring(cases.indexOf(separator)+1);
		}
		if(!list.isEmpty()) return list.toArray(new String[list.size()]);
		return new String[] {""};
	}
}
