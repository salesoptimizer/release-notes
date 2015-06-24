package sfconnector;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

//@WebServlet(name = "SFConnector", urlPatterns = { "/oauth/*", "/oauth" })

public class SFConnector extends HttpServlet {
	private static final long serialVersionUID = 6637048634977987707L;
	/* Eugene test org */ 
	private static final String CLIENT_ID = "3MVG9Rd3qC6oMalUQmRJ9gVbbcd7fa9vmARC5CJe7WzzPgD9Dy_dQaYYBCBr7Z0B8vLFZ71bhnGMrghvHDDYu";
	private static final String CLIENT_SECRET = "8922169740292202489";
	
	/* Salesoptimizer sandbox */ 
//	private static final String CLIENT_ID = "3MVG9Iu66FKeHhIPlMlaDshNV605eVYIpb8RFv08Ln2iC1qIwMIDZ8jLXRNM4twGgmfj4o6g9gGciWw1TjERQ";
//	private static final String CLIENT_SECRET = "4861802443923999686";
	
	private static final String REDIRECT_URL = "https://tranquil-taiga-6535.herokuapp.com/RestTest/oauth/_callback";
	private static final String ENVIRONMENT = "https://login.salesforce.com";
//	private static final String ENVIRONMENT = "https://test.salesforce.com";
	
	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String INSTANCE_URL = "INSTANCE_URL";
	
	private String authUrl = null;
	private String tokenUrl = null;
	
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
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String accessToken = (String) request.getSession().getAttribute(
				ACCESS_TOKEN);
		
		if (accessToken == null) {
			String instanceUrl = null;
			
			if (request.getRequestURI().endsWith("oauth")) {
				response.getWriter().print("oauth authUrl =>"+authUrl);
				// we need to send the user to authorize
				response.sendRedirect(authUrl);
				return;
			} else {
				System.out.println("Auth successful - got callback");
				response.getWriter().print("Auth successful - got callback\n");

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
					response.getWriter().print("responseBody => "+responseBody+"\n");
					response.getWriter().print("line 81 \n");
					try {
//						JSONObject json = new JSONObject(); 
						JSONObject authResponse = new JSONObject(responseBody);
						response.getWriter().print("line 85 \n");
//						JSONObject authResponse = new JSONObject(
//								new JSONTokener(new InputStreamReader(
//										post.getResponseBodyAsStream())));
						System.out.println("Auth response: "
								+ authResponse.toString(2));

						accessToken = authResponse.getString("access_token");
						instanceUrl = authResponse.getString("instance_url");

						System.out.println("Got access token: " + accessToken + "\n");
					} catch (JSONException e) {
						e.printStackTrace();
						throw new ServletException("JSONException => " + e);
					}
				} finally {
					post.releaseConnection();
				}
			}
			response.getWriter().print("line 104 \n");
			// Set a session attribute so that other servlets can get the access
			// token
			request.getSession().setAttribute(ACCESS_TOKEN, accessToken);
			response.getWriter().print("line 108 \n");
			// We also get the instance URL from the OAuth response, so set it
			// in the session too
			request.getSession().setAttribute(INSTANCE_URL, instanceUrl);
		}
		response.getWriter().print("line 113 \n");
		response.sendRedirect(request.getContextPath() + "/DemoREST");
	}
	
	

}
