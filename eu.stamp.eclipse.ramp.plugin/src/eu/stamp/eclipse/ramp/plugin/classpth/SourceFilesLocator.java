/*******************************************************************************
 * Copyright (c) 2019 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * 	Ricardo Jose Tejada Garcia (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.eclipse.ramp.plugin.classpth;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;

/**
 *  This class is responsible to get the list of qualified names of the no test classes
 *  in a project, the project is supposed to follow the maven structure.
 */
public class SourceFilesLocator {
	/**
	 * @param project maven project to look for the no test classes
	 * @return a string containing the list of qualified names in the format name1:name2:... (; in windows)
	 */
	public String getFullNamesCompactList(IJavaProject project) {
		List<String> fullNamesList = getFullNamesList(project);
		if(fullNamesList.size() == 0) return "";
		else if(fullNamesList.size() == 1) return fullNamesList.get(0);
        StringBuilder builder = new StringBuilder();
        builder.append(fullNamesList.get(0));
        for(int i = 1; i < fullNamesList.size(); i++) 
        	builder.append(System.getProperty("path.separator")).append(fullNamesList.get(i));
        return builder.toString();
	}
	/**
	 * @param project a maven project to look for the no test java source files
	 * @return the list of qualified names of the no test classes in the project
	 */
	private List<String> getFullNamesList(IJavaProject project){
		List<File> javaFiles = getJavaSourceFiles(
				new File(project.getProject().getLocation().toString()),0);
		List<String> fullNames = new ArrayList<String>(javaFiles.size());
		for(File javaFile : javaFiles) {
			String fullName = getFullName(javaFile);
			if(fullName != null) {
				if(fullName.startsWith(".")) {
					fullName = fullName.replaceFirst(".","");
				}
				fullNames.add(fullName);
			}
		}
		return fullNames;
	}
	/**
	 * @param javaFile a java file in src/main/java
	 * @return the full qualified name of the java class corresponding to the given java file
	 */
	private String getFullName(File javaFile) {
	    String result = javaFile.getAbsolutePath();
	    if(result.contains("src/main/java")) {
	    	result = result.split("src/main/java")[1];
	    	return result.replaceAll(".java","").replaceAll("\\\\",".").replaceAll("/",".");
	    } else return null;
	}
    /**
     * @param folder the folder to look for java files
     * @param level the level of recursion
     * @return the list of the java files inside the given folder (including files into sub-folders)
     */
	private List<File> getJavaSourceFiles(File folder,int level){
		List<File> javaSourceFiles = new LinkedList<File>();
		if(level > 100) return javaSourceFiles;
		File[] files = folder.listFiles();
		for(File file : files) {
			if(file.isDirectory()) javaSourceFiles.addAll(getJavaSourceFiles(file,level+1)); // recursion
			else if(file.getPath().endsWith(".java")) javaSourceFiles.add(file);
		}
		return javaSourceFiles;
	}
}
