package eu.stamp.descartes.jira;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.ProjectRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.CimIssueType;
import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.PropertyInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;


import io.atlassian.util.concurrent.Promise;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;


public class App {
	
    public static void main( String[] args ) {

    	// preferences
    	String user = "atos_user";     // TODO
    	String password = "v7RRsbhpgVBba73M";
    	String url = "https://vmi2.stamp-project.eu/jira/";

    	/*
    	try {
			URL url2 = URI.create(url).toURL();
			InputStream stream = url2.openStream();
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			while((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			reader.close();
			stream.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
    	
    
    	// user selection
    	
    	String projectKey = "FP";  // TODO
    	long issueType = 10006;
    	String summary = "Experimental issue generated with the Java Api 1";
    	
    	System.out.println("\n----- Jira Client Java Api Demo -----\n");
    	System.out.println("   - url : " + url);
    	System.out.println("   - user : " + user);
    	
    	// get the client
    	JiraRestClient client = new AsynchronousJiraRestClientFactory()
    			.createWithBasicHttpAuthentication(URI.create(url),user, password);
    	
    	// get and print projects
    	ProjectRestClient projectClient = client.getProjectClient();
    	Promise<Iterable<BasicProject>> promise = projectClient.getAllProjects();
    	Iterable<BasicProject> projects = promise.claim();
    	System.out.println("\n----- Projects -----\n");
    	for(BasicProject project : projects) {
    		System.out.println("   - " + project.getName() + ", key : " + project.getKey());
    	    projectKey = project.getKey();
    	}
    	
    	// create an issue
    	IssueRestClient issue = client.getIssueClient();
    	
    	/*
    	Issue theIssue = issue.getIssue("FP-5").claim();
    	Iterable<IssueField> fields = theIssue.getFields();
    
    	System.out.println("\n----- Fields -----\n");
    	for(IssueField field : fields) {
    		System.out.println(field.getName() + ", value = " + field.getValue());
    	}*/
    	
    	Iterable<CimProject> cims = issue.getCreateIssueMetadata(null).claim();
    	Iterable<CimIssueType> tips = null;
    	
    	
    	for(CimProject cim : cims) {
    		tips = cim.getIssueTypes();
    		break;
    	}
    	if(tips != null)for(CimIssueType tip : tips) {
    		System.out.println(tip.getName() + " Id : " + tip.getId());
    	}
    	
    	String description = "<html>"
    			+ "<link href=\"../style.css\" rel=\"stylesheet\"><body>"
    			+ "<h1>isEqual(eu.stamp_project.examples.dhell.MyStorage)</h1>"
    			+ "<a href=\"../index.html\">[Back]</a><dl><dt>Class</dt><dd>MyStorage</dd><dt>Package</dt>"
    			+ "<dd>eu/stamp_project/examples/dhell</dd></dl><p>This method is "
    			+ "<strong>partially-tested</strong></p><h2>Transformations</h2>"
    			+ "<p>It seems that this method has been tested to return only the following value(s):"
    			+ " true.</p><p>The following transformations were applied but they were not detected by "
    			+ "the test suite:</p><ul><li>All method body replaced by: return true</li></ul>"
    			+ "<p>The following transformations were detected by the test suite when applied.</p><ul>"
    			+ "<li>All method body replaced by: return false</li></ul><h2>Tests</h2><p>The method is "
    			+ "covered by the following test cases:</p><ul><li>eu.stamp_project.examples"
    			+ ".dhell.MyStorageTest.testSaveReadData(eu.stamp_project.examples.dhell.MyStorageTest)</li>"
    			+ "<li>eu.stamp_project.examples.dhell.HelloAppTest.eu.stamp_project.examples.dhell."
    			+ "HelloAppTest</li></ul><a href=\"../index.html\">[Back]</a></body><html>"; 
       
    	DefaultWysiwygConverter converter = new DefaultWysiwygConverter();
    	description = converter.convertXHtmlToWikiMarkup(description);
    	
    	/*
    	IssueInputBuilder issueInputBuilder = new IssueInputBuilder(projectKey,issueType,
    			"isEqual(eu.stamp_project.examples.dhell.MyStorage)");	
    	issueInputBuilder.setDescription(description);
    	IssueInput issueInput = issueInputBuilder.build();
    	
    	BasicIssue basicIssue = issue.createIssue(issueInput).claim();
    	
    	
    	// get info about the issue
    	String key = basicIssue.getKey();
    	String id = String.valueOf(basicIssue.getId());
    	String self = basicIssue.getSelf().toString();
    	
    	// printing the info about the issue
    	System.out.println("\n----- Created Jira Issue -----\n");
    	System.out.println("   - key : " + key);
    	System.out.println("   - Id : " + id);
    	System.out.println("   - Self : " + self);	*/
    	
    }
}
