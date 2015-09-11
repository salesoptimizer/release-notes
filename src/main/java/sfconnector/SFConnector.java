package sfconnector;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import rncontroller.RNController;

//@WebServlet(name = "SFConnector", urlPatterns = { "/oauth/*", "/oauth" })

public class SFConnector/* extends HttpServlet*/ {
	private static final long serialVersionUID = 6637048634977987707L;
	/* Eugene test org */ 
	/*private static final String CLIENT_ID = "3MVG9Rd3qC6oMalUQmRJ9gVbbcd7fa9vmARC5CJe7WzzPgD9Dy_dQaYYBCBr7Z0B8vLFZ71bhnGMrghvHDDYu";
	private static final String CLIENT_SECRET = "8922169740292202489";*/
	
	/* Salesoptimizer sandbox old */ 
	/*private static final String CLIENT_ID = "3MVG9Iu66FKeHhIPlMlaDshNV605eVYIpb8RFv08Ln2iC1qIwMIDZ8jLXRNM4twGgmfj4o6g9gGciWw1TjERQ";
	private static final String CLIENT_SECRET = "4861802443923999686";*/
	
	/* Salesoptimizer sandbox */ 
	private static final String CLIENT_ID = "3MVG9snqYUvtJB1MWfzh1Ehkk24Hanyly3hFbq9YTZnjgwkXBDQ5YoXmqQDF9F6yfwY5hzAW3U6d5ORMILt4x";
	private static final String CLIENT_SECRET = "6328597736698327307";
	
	private static final String REDIRECT_URL = "https://tranquil-taiga-6535.herokuapp.com/RestTest/oauth/_callback";
//	private static final String ENVIRONMENT = "https://login.salesforce.com";
	private static final String ENVIRONMENT = "https://test.salesforce.com";
	
	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String INSTANCE_URL = "INSTANCE_URL";
	
	private String authUrl = null;
	private String tokenUrl = null;
	
	private Logger log1 = LogManager.getLogManager().getLogger("rnotes");
	
	public SFConnector() throws ServletException {
		init();
	}
	
	public void init() throws ServletException {
		try {
			authUrl = ENVIRONMENT
					+ "/services/oauth2/authorize?response_type=code&client_id="
					+ CLIENT_ID + "&redirect_uri="
					+ URLEncoder.encode(REDIRECT_URL, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new ServletException(e);
		}

		tokenUrl = ENVIRONMENT + "/services/oauth2/token";
	}
	
	public void getAccessToSalesforce(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String accessToken = (String) request.getSession().getAttribute(ACCESS_TOKEN);
		
		if (accessToken == null) {
			String instanceUrl = null;
			if (!request.getRequestURI().endsWith("_callback")) {
				log1.info("REQ DOESN'T END WITH _callback");
				log1.info("oauth authUrl =>"+authUrl);
				// we need to send the user to authorize
				response.sendRedirect(authUrl);
				return;
			} else {
				log1.info("REQ ENDS WITH _callback");
				String code = request.getParameter("code");
				HttpClient httpclient = new HttpClient();
				PostMethod post = new PostMethod(tokenUrl);
				post.addParameter("code", code);
				post.addParameter("grant_type", "authorization_code");
				post.addParameter("client_id", CLIENT_ID);
				post.addParameter("client_secret", CLIENT_SECRET);
				post.addParameter("redirect_uri", REDIRECT_URL);

				try {
					httpclient.executeMethod(post);
					String responseBody = post.getResponseBodyAsString();
					try {
						JSONObject authResponse = new JSONObject(responseBody);
						accessToken = authResponse.getString("access_token");
						instanceUrl = authResponse.getString("instance_url");
					} catch (JSONException e) {
						e.printStackTrace();
						throw new ServletException("JSONException => " + e);
					}
				} finally {
					post.releaseConnection();
				}
			}
			// Set a session attribute so that other servlets can get the access token and instance URL
			request.getSession().setAttribute(ACCESS_TOKEN, accessToken);
			request.getSession().setAttribute(INSTANCE_URL, instanceUrl);
		}
	}
	
	
	private void sendRequest() throws UnsupportedEncodingException {
		HttpClient client = new HttpClient();

	    GetMethod method  = new GetMethod(ENVIRONMENT + "/services/oauth2/authorize");
	    FileOutputStream fos = null;
	    
	    NameValuePair[] params = new NameValuePair[3];
		params[0] = new NameValuePair("response_type", "code");
		params[1] = new NameValuePair("client_id", CLIENT_ID);
		params[2] = new NameValuePair("redirect_uri", URLEncoder.encode(REDIRECT_URL, "UTF-8"));
		method.setQueryString(params);
	    try {
//	      method.setURI(new URI(authUrl, true));
	      int returnCode = client.executeMethod(method);
	      log1.info("STATUS CODE => " + returnCode);
	      if(returnCode != HttpStatus.SC_OK) {
	        log1.warning("Unable to fetch default page, status code: " + returnCode);
	      }

	      log1.info("RESPONSE BODY => " + method.getResponseBodyAsString());

	      /*HostConfiguration hostConfig = new HostConfiguration();
	      hostConfig.setHost("www.yahoo.com", null, 80, Protocol.getProtocol("http"));

	      method.setURI(new URI("/", true));

	      client.executeMethod(hostConfig, method);

	      System.err.println(method.getResponseBodyAsString());*/

	    } catch (HttpException he) {
	      System.err.println(he);
	    } catch (IOException ie) {
	      System.err.println(ie);
	    } finally {
	      method.releaseConnection();
	      if(fos != null) try { fos.close(); } catch (Exception fe) {}
	    }
	}

}
