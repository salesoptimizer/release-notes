package sfconnector;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.ReleaseNote;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Servlet implementation class DemoREST
 */
public class SFQuery {
	private static final long serialVersionUID = 1L;
	private String accessToken;
	private String instanceUrl;
	
	public SFQuery(String accessToken, String instanceUrl) {
		this.accessToken = accessToken;
		this.instanceUrl = instanceUrl;
	}

	private GetMethod createGetMethod() {
		GetMethod get = new GetMethod(this.instanceUrl + "/services/data/v20.0/query");
		get.setRequestHeader("Authorization", "OAuth " + this.accessToken);
		return get;
	}
	
	public List<ReleaseNote> getTickets(String ver1, String ver2, String projectId) throws ServletException, IOException {
		List<ReleaseNote> releaseNotes = new ArrayList<ReleaseNote>();
		HttpClient httpclient = new HttpClient();
		
		// set the SOQL as a query param
		NameValuePair[] params = new NameValuePair[1];
		params[0] = new NameValuePair("q",
				"SELECT Id, Fixed_in_Ver__c, Release_Notes__c, Est_Due_Date__c"
			  + "FROM Ticket__c "
			  + "WHERE (Fixed_in_Ver__c >= '" + ver1 + "' AND Fixed_in_Ver__c <= '" + ver2 + "')"
			  + "AND Project__c = '" + projectId + "'"
	  		  + "LIMIT 100");
		GetMethod getMethod = createGetMethod();
		getMethod.setQueryString(params);
		
		try {
			httpclient.executeMethod(getMethod);
			int statusCode = getMethod.getStatusCode(); 
			if (statusCode == HttpStatus.SC_OK) {
				// Now lets use the standard java json classes to work with the results
				String responseBody = getMethod.getResponseBodyAsString();
				try {
					JSONObject response = new JSONObject(responseBody);
					JSONArray results = response.getJSONArray("records");
					for (int i = 0; i < results.length(); i++) {
						String ticketId = results.getJSONObject(i).getString("Id");
						String ticketFixedVersion = results.getJSONObject(i).getString("Fixed_in_Ver__c");
						String ticketDate = results.getJSONObject(i).getString("Est_Due_Date__c");
						String ticketReleaseNotes = "";
						if (results.getJSONObject(i).get("Release_Notes__c") instanceof String) {
							ticketReleaseNotes = results.getJSONObject(i).getString("Release_Notes__c");
						}
						releaseNotes.add(new ReleaseNote(ticketId, ticketDate, ticketFixedVersion, ticketReleaseNotes));
					}
				} catch (JSONException e) {
					e.printStackTrace();
					throw new ServletException(e);
				}
			}
		} finally {
			getMethod.releaseConnection();
		}
		return releaseNotes;
	}
	
	public String getProjectName(String projectId) throws ServletException, IOException {
		String projectName = null;
		HttpClient httpclient = new HttpClient();
		
		// set the SOQL as a query param
		NameValuePair[] params = new NameValuePair[1];
		params[0] = new NameValuePair("q",
				"SELECT Name from SFDC_Project__c WHERE Id = '" + projectId + "' LIMIT 1");
		GetMethod getMethod = createGetMethod();
		getMethod.setQueryString(params);
		
		try {
			httpclient.executeMethod(getMethod);
			int statusCode = getMethod.getStatusCode(); 
			if (statusCode == HttpStatus.SC_OK) {
				// Now lets use the standard java json classes to work with the results
				String responseBody = getMethod.getResponseBodyAsString();
				try {
					JSONObject response = new JSONObject(responseBody);
					JSONArray results = response.getJSONArray("records");
					for (int i = 0; i < results.length(); i++) {
						projectName = results.getJSONObject(i).getString("Name"); 
					}
				} catch (JSONException e) {
					e.printStackTrace();
					throw new ServletException(e);
				}
			}
		} finally {
			getMethod.releaseConnection();
		}
		return projectName;
	}

}
