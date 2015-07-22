package sfconnector;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
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
	private static final String CLIENT_ID = "3MVG982oBBDdwyHjrnsjAnCpNWqm7Uh0aHwcxkLZ9NHZGAF_7CGm1wSWtL5YP74CtD_EitP9vVwjavID4C8xQ";
	private static final String CLIENT_SECRET = "4574498198215889924";
	
	private static final String REDIRECT_URL = "https://tranquil-taiga-6535.herokuapp.com/RestTest/oauth/_callback";
//	private static final String ENVIRONMENT = "https://login.salesforce.com";
	private static final String ENVIRONMENT = "https://test.salesforce.com";
	
	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String INSTANCE_URL = "INSTANCE_URL";
	
	private String authUrl = null;
	private String tokenUrl = null;
	
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
		PrintWriter out = response.getWriter();
		
		HttpClient httpclient = new HttpClient();
		if (accessToken == null) {
			String instanceUrl = null;
			if (!request.getRequestURI().endsWith("_callback")) {
				out.println("REQ DOESN'T END WITH _callback");
				RNController.log.info("REQ DOESN'T END WITH _callback");
				RNController.log.info("oauth authUrl =>"+authUrl);
				out.println("oauth authUrl =>"+authUrl);
				/*response.getWriter().print("oauth authUrl =>"+authUrl);*/
				// we need to send the user to authorize
				response.sendRedirect(authUrl);
				
//				GetMethod get = new GetMethod(authUrl);
//				httpclient.executeMethod(get);
				return;
			} else {
				out.println("REQ ENDS WITH _callback");
				out.println("ACCESS_TOKEN" + (String) request.getSession().getAttribute(ACCESS_TOKEN));
				String code = request.getParameter("code");
				httpclient = new HttpClient();
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

}
