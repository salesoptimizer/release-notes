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

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import rnservices.RTFConverter;
import sfconnector.SFQuery;
import sfconnector.SFConnector;

/**
 * Servlet implementation class MainController
 */
public class MainController extends HttpServlet {
	private static final long serialVersionUID = 7546372886300391908L;
	
	private String content;
	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String INSTANCE_URL = "INSTANCE_URL";
	private static String accessToken;
	private static String instanceUrl;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SFConnector sfConnector = new SFConnector();
		sfConnector.getAccessToSalesforce(request, response);
		
		accessToken = (String) request.getSession().getAttribute(
				ACCESS_TOKEN);

		instanceUrl = (String) request.getSession().getAttribute(
				INSTANCE_URL);
		
		SFQuery demoREST = new SFQuery();
		
		PrintWriter writer = response.getWriter();

		if (accessToken == null) {
			response.getWriter().print("Error - no access token");
			return;
		}

		content = demoREST.showAccounts(instanceUrl, accessToken, writer);
		request.setAttribute("accounts", content);
//		content = demoREST.showProjects(instanceUrl, accessToken, writer);
		request.setAttribute("projects", demoREST.showProjects(instanceUrl, accessToken, writer));
		getServletContext().getRequestDispatcher("/main.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter writer = resp.getWriter();
		String minVer = req.getParameter("minVer");
		String maxVer = req.getParameter("maxVer");
		SFQuery demoREST = new SFQuery();
		RTFConverter.convertToRTF(demoREST.getTickets(instanceUrl, accessToken, minVer, maxVer, writer));
		req.setAttribute("tickets", true);
		getServletContext().getRequestDispatcher("/main.jsp").forward(req, resp);
	}
	
	
	
}
