package sfconnector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;

public class SFConnector {
	/* Salesoptimizer sandbox */ 
//	private static final String CLIENT_ID = "3MVG9snqYUvtJB1MWfzh1Ehkk24Hanyly3hFbq9YTZnjgwkXBDQ5YoXmqQDF9F6yfwY5hzAW3U6d5ORMILt4x";
	private static String CLIENT_ID;
//	private static final String CLIENT_SECRET = "6328597736698327307";
	private static String CLIENT_SECRET;
	
//	private static final String REDIRECT_URL = "https://tranquil-taiga-6535.herokuapp.com/RestTest/oauth/_callback";
	private static String REDIRECT_URL;
//	private static final String ENVIRONMENT = "https://login.salesforce.com";
//	private static final String ENVIRONMENT = "https://test.salesforce.com";
	private static String ENVIRONMENT;
	
	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String INSTANCE_URL = "INSTANCE_URL";

	private static final String FILENAME = "src/main/resources/params.properties";
	
	private String authUrl = null;
	private String tokenUrl = null;
	
	private static Properties properties;
	private Logger log = LogManager.getLogManager().getLogger("rnotes");
	
	public SFConnector() throws ServletException {
		init();
	}
	
	public void init() throws ServletException {
		try {
			properties = new Properties();
			properties.load(new FileInputStream(new File(FILENAME)));
			CLIENT_ID = properties.getProperty("CLIENT_ID");
			CLIENT_SECRET = properties.getProperty("CLIENT_SECRET");
			REDIRECT_URL = properties.getProperty("REDIRECT_URL");
			ENVIRONMENT = properties.getProperty("ENVIRONMENT");
			authUrl = ENVIRONMENT
					+ "/services/oauth2/authorize?response_type=code&client_id="
					+ CLIENT_ID + "&redirect_uri="
					+ URLEncoder.encode(REDIRECT_URL, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new ServletException(e);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 

		tokenUrl = ENVIRONMENT + "/services/oauth2/token";
	}
	
	public void getAccessToSalesforce(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String accessToken = (String) request.getSession().getAttribute(ACCESS_TOKEN);
		
		if (accessToken == null) {
			String instanceUrl = null;
			if (!request.getRequestURI().endsWith("_callback")) {
				log.info("REQ DOESN'T END WITH _callback");
				log.info("oauth authUrl =>"+authUrl);
				// we need to send the user to authorize
				response.sendRedirect(authUrl);
				return;
			} else {
				log.info("REQ ENDS WITH _callback");
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
						log.severe(e.getMessage());
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

}
