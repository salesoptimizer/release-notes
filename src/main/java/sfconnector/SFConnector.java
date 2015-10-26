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
	private String clientId;
	private String clientSecret;
	private String redirectURL;
	private String environment;
	
	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String INSTANCE_URL = "INSTANCE_URL";
	private static final String FILENAME = "src/main/resources/params.properties";
	
	private String authUrl = null;
	private String tokenUrl = null;
	
	private Properties properties;
	private Logger log = LogManager.getLogManager().getLogger("rnotes");
	
	public SFConnector() throws ServletException {
		init();
	}
	
	public void init() throws ServletException {
		try {
			this.properties = new Properties();
			this.properties.load(new FileInputStream(new File(FILENAME)));
			this.clientId = properties.getProperty("CLIENT_ID");
			this.clientSecret = properties.getProperty("CLIENT_SECRET");
			this.redirectURL = properties.getProperty("REDIRECT_URL");
			this.environment = properties.getProperty("ENVIRONMENT");
			authUrl = environment
					+ "/services/oauth2/authorize?response_type=code&client_id="
					+ clientId + "&redirect_uri="
					+ URLEncoder.encode(redirectURL, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new ServletException(e);
		} catch (FileNotFoundException e) {
			log.severe(e.getMessage());
		} catch (IOException e) {
			log.severe(e.getMessage());
		} 

		tokenUrl = environment + "/services/oauth2/token";
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
				post.addParameter("client_id", clientId);
				post.addParameter("client_secret", clientSecret);
				post.addParameter("redirect_uri", redirectURL);

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
