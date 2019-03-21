
package eu.stamp.eclipse.plugin.dspot.properties;

import org.eclipse.swt.graphics.Point;

/**
 *
 */
public abstract class DSpotProperties {
	
	public static final String MAVEN_NATURE = "org.eclipse.m2e.core.maven2Nature";
	public static final String PLUGIN_ID = "eu.stamp.eclipse.dspot.wizard";
	public static final String CONFIGURATION_ID = "eu.stamp.dspot.configuration";
	public static final String PROJECT_KEY = "project";
	public static final String CHECK_EXTRA_KEY = "CHECK";
	public static final Point INDENT = new Point(4,6);
	
	// System dependent constants
	private static String SEPARATOR;
	private static String PATH_SEPARATOR;

	public static String getSeparator() {
		if(SEPARATOR == null) {
			if(System.getProperty("os.name")
					.contains("indow")) SEPARATOR = ";";
			else SEPARATOR = ":";
		}
		return SEPARATOR;
	}
	public static String getPathSeparator() {
		if(PATH_SEPARATOR == null) {
			if(System.getProperty("os.name")
				.contains("indow")) PATH_SEPARATOR = "\\";
			else PATH_SEPARATOR = "/";
		}
		return PATH_SEPARATOR;
	}
}
