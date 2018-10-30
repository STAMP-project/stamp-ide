package eu.stamp.eclipse.dspot.plugin.interfaces;

import eu.stamp.eclipse.dspot.plugin.launch.info.DSpotLaunchInfo;

public interface IDSpotInfoSource extends IDSpotElement {

	public void appendToInfoObject(DSpotLaunchInfo info);
}
