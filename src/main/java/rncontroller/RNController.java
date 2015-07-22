package rncontroller;

import gglconnector.GGLConnector;
import gglconnector.GGLFileManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import rnservices.GGLService;
import rnservices.RTFConverter;
import sfconnector.SFQuery;
import sfconnector.SFConnector;

/**
 * Servlet implementation class MainController
 */
public class RNController extends HttpServlet {
	private static final long serialVersionUID = 7546372886300391908L;
	
	private String content;
	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String INSTANCE_URL = "INSTANCE_URL";
	private static String accessToken;
	private static String instanceUrl;
	
	private static Logger log = Logger.getLogger("rnotes");

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LogManager.getLogManager().readConfiguration(RNController.class.getResourceAsStream("/logging.properties"));
		
		if (request.getRequestURI().endsWith("_logs")) {
			BufferedReader in = new BufferedReader(new FileReader("logs.txt"));
			String line;
			while ((line = in.readLine()) != null) {
				response.getWriter().println(line);
			}
			return;
		} else {
			log.info("REQUEST REMOTE ADDR => " + request.getRemoteAddr());
			log.info("REQUEST REMOTE HOST => " + request.getRemoteHost());
			log.info("REQUEST SESSION ID => " + request.getSession().getId());
			log.info("REQUEST QUERY STRING => " + request.getQueryString());
			log.info("REQUEST AUTH TYPE => " + request.getAuthType());
			
			/*String minVer = request.getParameter("minVer");
			String maxVer = request.getParameter("maxVer");*/
			String projectId = request.getParameter("projectId");
			
			SFConnector sfConnector = new SFConnector();
			
			sfConnector.getAccessToSalesforce(request, response);
			
			accessToken = (String) request.getSession().getAttribute(ACCESS_TOKEN);
			instanceUrl = (String) request.getSession().getAttribute(INSTANCE_URL);
			
			response.getWriter().println("accessToken => " + accessToken);
			response.getWriter().println("\ninstanceUrl => " + instanceUrl);
			log.info("accessToken => " + accessToken);
			log.info("instanceUrl => " + instanceUrl);
			
			SFQuery sfQuery = new SFQuery(accessToken, instanceUrl);
			
			if (accessToken == null) {
				response.getWriter().print("Error - no access token");
				return;
			}
			
//			response.getWriter().println("minVer => " + minVer + " maxVer => " + maxVer + " projectId => " + projectId);
//			log.info("minVer => " + minVer + " maxVer => " + maxVer + " projectId => " + projectId);
			
	//		RTFConverter.convertToRTF(sfQuery.getTickets(minVer, maxVer, projectId));
			RTFConverter.convertToRTF(null);
			request.setAttribute("tickets", true);
			GGLService.docName = sfQuery.getProjectName(projectId);
			GGLService.createGoogleDoc();
	
	//		request.setAttribute("projects", sfQuery.showProjects());
	//		
	//	    Map<String, String> projects = sfQuery.showProjects(); 
	//		for (String projectKey: projects.keySet()) {
	//			response.getWriter().println(projectKey + " => " + projects.get(projectKey));	
	//		}
	//		getServletContext().getRequestDispatcher("/main.jsp").forward(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String minVer = req.getParameter("minVer");
		String maxVer = req.getParameter("maxVer");
		String projectId = req.getParameter("projectId");
		SFQuery sfQuery = new SFQuery(accessToken, instanceUrl);
		RTFConverter.convertToRTF(sfQuery.getTickets(minVer, maxVer, projectId));
		req.setAttribute("tickets", true);
		GGLService.docName = sfQuery.getProjectName(projectId);
		getServletContext().getRequestDispatcher("/main.jsp").forward(req, resp);
	}
	
	
	
}
