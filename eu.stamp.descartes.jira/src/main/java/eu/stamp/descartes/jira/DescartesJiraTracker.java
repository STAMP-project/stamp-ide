package eu.stamp.descartes.jira;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.renderer.wysiwyg.DefaultWysiwygConverter;

public class DescartesJiraTracker {

	private String title, description, projectKey;
	
	private long issueType;
	
	private final Map<String,String> projects;

	private final Map<String,Long> issueTypes;
	
	private JiraRestClient client;
	
	public DescartesJiraTracker() {
		projects = new HashMap<String,String>();
		// TODO
		issueTypes = new HashMap<String,Long>();
	}
	
	public void parseDescription(String description) {
		DefaultWysiwygConverter converter = new DefaultWysiwygConverter();
		this.description = converter.convertXHtmlToWikiMarkup(description);
	}
	
	/*
	 *   Getters
	 */
	public String getProjectkey() { return projectKey; }
	
	public String getTitle() { return title; }
	
	public String getDescription() { return description; }
	
	/*
	 *   Setters
	 */
	public void setProjectkey(String projectKey) {
		this.projectKey = projectKey;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}