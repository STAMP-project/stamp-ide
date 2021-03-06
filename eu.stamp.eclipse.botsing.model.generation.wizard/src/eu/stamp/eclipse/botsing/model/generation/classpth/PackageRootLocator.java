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
package eu.stamp.eclipse.botsing.model.generation.classpth;

import java.io.File;

import org.eclipse.jdt.core.IJavaProject;
/**
 * This class is responsible to find the root package name, the project is supposed
 * to have a maven structure
 */
public class PackageRootLocator {

	public String findpackageRoot(IJavaProject project) {
		try {
		File mainFolder = new File(project.getProject().getLocation().toString() + "/src/main/java");
		File packageRoot = getLastNoMultipleFolder(mainFolder,0);
		String result = packageRoot.getAbsolutePath().split("/src/main/java")[1];
		result = result.replaceAll("\\\\",".").replaceAll("/",".");
		if(result.startsWith(".")) result = result.replaceFirst(".","");
		if(result.endsWith(".")) result = result.substring(0,result.length()-1);
		return result;
		} catch(Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private File getLastNoMultipleFolder(File folder,int level) {
		if(level > 200) return folder;
		File[] files = folder.listFiles();
		int subFolders = 0;
		File subFolder = null;
		for(File file : files)if(file.isDirectory()) {
			subFolders++;
			if(subFolders > 1) break;
			subFolder = file;
		}
		if(subFolders  != 1) return folder;
		return getLastNoMultipleFolder(subFolder,level+1); // recursion
	}
}
