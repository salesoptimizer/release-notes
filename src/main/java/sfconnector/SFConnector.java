package sfconnector;

import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;



@WebServlet(name = "oauth", urlPatterns = { "/oauth/*", "/oauth" }, initParams = {
	        // clientId is 'Consumer Key' in the Remote Access UI
	        @WebInitParam(name = "clientId", value = "3MVG9Rd3qC6oMalUQmRJ9gVbbcd7fa9vmARC5CJe7WzzPgD9Dy_dQaYYBCBr7Z0B8vLFZ71bhnGMrghvHDDYu"),
	        // clientSecret is 'Consumer Secret' in the Remote Access UI
	        @WebInitParam(name = "clientSecret", value = "8922169740292202489"),
	        // This must be identical to 'Callback URL' in the Remote Access UI
	        @WebInitParam(name = "redirectUri", value = "https://tranquil-taiga-6535.herokuapp.com/getsf/"),
	        @WebInitParam(name = "environment", value = "https://login.salesforce.com"), })

public class SFConnector extends HttpServlet {
	private static final String CLIENT_ID = "3MVG9Rd3qC6oMalUQmRJ9gVbbcd7fa9vmARC5CJe7WzzPgD9Dy_dQaYYBCBr7Z0B8vLFZ71bhnGMrghvHDDYu";
	private static final String CLIENT_SECRET = "8922169740292202489";
	private static final String REDIRECT_URL = "https://tranquil-taiga-6535.herokuapp.com/getsf/";
	private static final String ENVIRONMENT = "https://login.salesforce.com";
	
}
