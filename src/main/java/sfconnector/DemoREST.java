package sfconnector;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
public class DemoREST extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String INSTANCE_URL = "INSTANCE_URL";
       
	private void showAccounts(String instanceUrl, String accessToken,
			PrintWriter writer) throws ServletException, IOException {
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
			writer.print("\n HttpStatus => " + statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				// Now lets use the standard java json classes to work with the
				// results
				String responseBody = get.getResponseBodyAsString();
				writer.print("RESPONSE => " + responseBody + "\n");
				try {
					JSONObject response = new JSONObject(responseBody);
//					writer.print("line 56 \n");
//					writer.print("Query response => "
//							+ response.toString(2) + "\n");
//
//					writer.print(response.getString("totalSize")
//							+ " record(s) returned\n\n");

					JSONArray results = response.getJSONArray("records");
//					writer.print("JSONArray of results => " + results.toString() + "\n");

					for (int i = 0; i < results.length(); i++) {
						writer.print(results.getJSONObject(i).getString("Id")
								+ ", "
								+ results.getJSONObject(i).getString("Name")
								+ "\n");
					}
					writer.print("\n");
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
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String accessToken = (String) request.getSession().getAttribute(
				ACCESS_TOKEN);

		String instanceUrl = (String) request.getSession().getAttribute(
				INSTANCE_URL);
		
		PrintWriter writer = response.getWriter();

		if (accessToken == null) {
			response.getWriter().print("Error - no access token");
			return;
		}

		writer.print("We have an access token: " + accessToken + "\n"
				+ "Using instance " + instanceUrl + "\n\n");
		
		showAccounts(instanceUrl, accessToken, writer);
	}

}
