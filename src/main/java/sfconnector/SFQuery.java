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
	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String INSTANCE_URL = "INSTANCE_URL";
       
	public String showAccounts(String instanceUrl, String accessToken,
			PrintWriter writer) throws ServletException, IOException {
		
		StringBuilder resultString = new StringBuilder();
		
		HttpClient httpclient = new HttpClient();
		GetMethod get = new GetMethod(instanceUrl
				+ "/services/data/v20.0/query");

		// set the token in the header
		get.setRequestHeader("Authorization", "OAuth " + accessToken);

		// set the SOQL as a query param
		NameValuePair[] params = new NameValuePair[1];

		params[0] = new NameValuePair("q",
				"SELECT Name, Id from Account LIMIT 100");
		get.setQueryString(params);

		try {
			httpclient.executeMethod(get);
			int statusCode = get.getStatusCode(); 
			if (statusCode == HttpStatus.SC_OK) {
				// Now lets use the standard java json classes to work with the results
				String responseBody = get.getResponseBodyAsString();
				try {
					JSONObject response = new JSONObject(responseBody);
					JSONArray results = response.getJSONArray("records");
					resultString.append("ACCOUNTS:\n");
					for (int i = 0; i < results.length(); i++) {
						resultString.append("ID[")
									.append(i)
									.append("], NAME[")
									.append(i)
									.append("] => ")
									.append(results.getJSONObject(i).getString("Id"))
									.append(", ")
									.append(results.getJSONObject(i).getString("Name"))
									.append("\n");
					}
					resultString.append("\n");
				} catch (JSONException e) {
					e.printStackTrace();
					throw new ServletException(e);
				}
			} else {
				writer.print("\n HttpStatus => " + statusCode + " but OK is " + HttpStatus.SC_OK);
			}
		} finally {
			get.releaseConnection();
		}
		return resultString.toString();
	}
	
	public HashMap<String, String> showProjects(String instanceUrl, String accessToken,
			PrintWriter writer) throws ServletException, IOException {
		
		HashMap<String, String> resultMap = new HashMap<String, String>();
		
		HttpClient httpclient = new HttpClient();
		GetMethod get = new GetMethod(instanceUrl
				+ "/services/data/v20.0/query");
		
		// set the token in the header
		get.setRequestHeader("Authorization", "OAuth " + accessToken);
		
		// set the SOQL as a query param
		NameValuePair[] params = new NameValuePair[1];
		
		params[0] = new NameValuePair("q",
				"SELECT Name, Id from SFDC_Project__c LIMIT 100");
		get.setQueryString(params);
		
		try {
			httpclient.executeMethod(get);
			int statusCode = get.getStatusCode(); 
			if (statusCode == HttpStatus.SC_OK) {
				// Now lets use the standard java json classes to work with the results
				String responseBody = get.getResponseBodyAsString();
				try {
					JSONObject response = new JSONObject(responseBody);
					JSONArray results = response.getJSONArray("records");
					for (int i = 0; i < results.length(); i++) {
						String projectId = results.getJSONObject(i).getString("Id"); 
						String projectName = results.getJSONObject(i).getString("Name"); 
						resultMap.put(projectId, projectName);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					throw new ServletException(e);
				}
			} else {
				writer.print("\n HttpStatus => " + statusCode + " but OK is " + HttpStatus.SC_OK);
			}
		} finally {
			get.releaseConnection();
		}
		return resultMap;
	}
	
	public List<ReleaseNote> getTickets(String instanceUrl, String accessToken, String ver1, String ver2, String projectId, PrintWriter writer) throws ServletException, IOException {
		
		List<ReleaseNote> releaseNotes = new ArrayList<ReleaseNote>();
		
		HttpClient httpclient = new HttpClient();
		GetMethod get = new GetMethod(instanceUrl
				+ "/services/data/v20.0/query");
		
		// set the token in the header
		get.setRequestHeader("Authorization", "OAuth " + accessToken);
		
		// set the SOQL as a query param
		NameValuePair[] params = new NameValuePair[1];
		
		params[0] = new NameValuePair("q",
				"SELECT Name, Id, Fixed_in_Ver__c, Release_Notes__c "
			  + "FROM Ticket__c "
			  + "WHERE (Fixed_in_Ver__c >= '" + ver1 + "' AND Fixed_in_Ver__c <= '" + ver2 + "')"
			  + "AND Project__c = '" + projectId + "'"
	  		  + "LIMIT 100");
		get.setQueryString(params);
		
		try {
			httpclient.executeMethod(get);
			int statusCode = get.getStatusCode(); 
			if (statusCode == HttpStatus.SC_OK) {
				// Now lets use the standard java json classes to work with the results
				String responseBody = get.getResponseBodyAsString();
				try {
					JSONObject response = new JSONObject(responseBody);
					JSONArray results = response.getJSONArray("records");
					for (int i = 0; i < results.length(); i++) {
						String ticketId = results.getJSONObject(i).getString("Id"); 
						String ticketName = results.getJSONObject(i).getString("Name"); 
						String ticketFixedVersion = results.getJSONObject(i).getString("Fixed_in_Ver__c");
						if (results.getJSONObject(i).getString("Release_Notes__c") != null) {
							String ticketReleaseNotes = results.getJSONObject(i).getString("Release_Notes__c");
						}
						releaseNotes.add(new ReleaseNote(ticketId, ticketName, ticketFixedVersion, ticketReleaseNotes));
					}
				} catch (JSONException e) {
					e.printStackTrace();
					throw new ServletException(e);
				}
			} else {
				writer.print("\n HttpStatus => " + statusCode + " but OK is " + HttpStatus.SC_OK);
			}
		} finally {
			get.releaseConnection();
		}
		return releaseNotes;
	}

}
