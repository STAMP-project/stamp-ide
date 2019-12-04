package eu.stamp.eclipse.botsing.model.generation.load;

import java.util.Map;

public abstract class LoadableElement {
	
  protected final String key;
  
  public LoadableElement(String key) {
	  this.key = key;
  }
  
  public void load(Map<String,String> map) { loadValue(map.get(key)); }
      
  protected abstract void loadValue(String value);
}
