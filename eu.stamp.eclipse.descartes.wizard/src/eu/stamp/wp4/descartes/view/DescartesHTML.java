package eu.stamp.wp4.descartes.view;

public class DescartesHTML {
	
	public static final String ISSUES_NAME = "Descartes Issues";
	public static final String PIT_NAME = "Pit Coverage";
	
    private final String name;
    private final String url;
    
    public DescartesHTML(String url) {
    	this.url = url;
    	if(url.contains("issues"))
    		name = ISSUES_NAME;
    	else if(url.contains("pit-reports"))
    		name = PIT_NAME;
    	else name = "";
    }
    public String getName() { return name; }
    
    public String getURL() { return url; }
}
