package rncontroller;

import gglconnector.GGLConnector;
import gglconnector.GGLFileManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import sfconnector.DemoREST;
import sfconnector.SFConnector;

/**
 * Servlet implementation class MainController
 */
public class MainController extends HttpServlet {
	private static final long serialVersionUID = 7546372886300391908L;
	
	private String content;
	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String INSTANCE_URL = "INSTANCE_URL";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SFConnector sfConnector = new SFConnector();
		sfConnector.getAccessToSalesforce(request, response);
		
		DemoREST demoREST = new DemoREST();
		
		String accessToken = (String) request.getSession().getAttribute(
				ACCESS_TOKEN);

		String instanceUrl = (String) request.getSession().getAttribute(
				INSTANCE_URL);
		
		PrintWriter writer = response.getWriter();

		if (accessToken == null) {
			response.getWriter().print("Error - no access token");
			return;
		}

		content = demoREST.showAccounts(instanceUrl, accessToken, writer);
		writer.print(content);
		request.setAttribute("content", "content");
		getServletContext().getRequestDispatcher("index.jsp").forward(request, response);
	}
	
}
