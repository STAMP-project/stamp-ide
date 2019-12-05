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
package eu.stamp.descartes.jira;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

public class DescartesJiraTracker {

	private String title, description, projectKey;
	
	private long issueType;
	
	private final Map<String,String> projects;

	private final Map<String,Long> issueTypes;
	
	private JiraRestClient client;
	
	public DescartesJiraTracker(String url,String user,String password) {
		// projects
		projects = new HashMap<String,String>();
		URI uri = URI.create(url);
		client = new AsynchronousJiraRestClientFactory()
				.createWithBasicHttpAuthentication(uri,user, password);
		Iterable<BasicProject> projectsIterable = client.getProjectClient().getAllProjects().claim();
		for(BasicProject basicProject : projectsIterable)
			projects.put(basicProject.getName(),basicProject.getKey());
		
		// issue types
		issueTypes = new HashMap<String,Long>();
        issueTypes.put("Bug",new Long(10006));
        issueTypes.put("Improvement",new Long(10002));
        issueTypes.put("New Feature",new Long(10005));
        issueTypes.put("Task",new Long(10003));
        issueTypes.put("Sub-task",new Long(10004));
        issueTypes.put("Epic",new Long(10000));
        issueType = 10006; // default Bug
	}

	public String createIssue() {
		// prepare Issue Configuration
        IssueRestClient issueClient = client.getIssueClient();
        IssueInputBuilder inputBuilder = new IssueInputBuilder(projectKey,issueType,title);
        inputBuilder.setDescription(description);
        
        // create issue
        BasicIssue basicIssue = issueClient.createIssue(inputBuilder.build()).claim();
        
        // returning ticket creation message
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(" - Key : ");
        resultBuilder.append(basicIssue.getKey());
        resultBuilder.append('\n');
        resultBuilder.append(" - Id : ");
        resultBuilder.append(basicIssue.getId());
        resultBuilder.append('\n');
        resultBuilder.append(" - Self : ");
        resultBuilder.append(basicIssue.getSelf().toString());
        return resultBuilder.toString();
	}
	
	/*
	 *   Getters
	 */
	public Set<String> getIssueTypes() { return issueTypes.keySet(); }
	
	public Set<String> getProjects() { return projects.keySet(); }
	
	public String getTitle() { return title; }
	
	public String getDescription() { return description; }
	
	/*
	 *   Setters
	 */
	public void setIssueType(String type) {
		issueType = issueTypes.get(type).longValue();
	}
	
	public void setProject(String projectName) {
		projectKey = projects.get(projectName);
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}