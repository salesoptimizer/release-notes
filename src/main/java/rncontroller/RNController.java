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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
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
	
	private String minVer;
	private String maxVer;
	private String projectId;
	
	private static Logger log = Logger.getLogger("rnotes");
	private static int docCounter = 0;

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
			String newMinVer = request.getParameter("minVer");
			String newMaxVer = request.getParameter("maxVer");
			String newProjectId = request.getParameter("projectId");
			this.minVer = newMinVer == null ? this.minVer : newMinVer;
			this.maxVer = newMaxVer == null ? this.maxVer : newMaxVer;
			this.projectId = newProjectId == null ? this.projectId : newProjectId;
			log.info("minVer => " + minVer + " maxVer => " + maxVer + " projectId => " + projectId);
			
			SFConnector sfConnector = new SFConnector();
			sfConnector.getAccessToSalesforce(request, response);
			
			accessToken = (String) request.getSession().getAttribute(ACCESS_TOKEN);
			instanceUrl = (String) request.getSession().getAttribute(INSTANCE_URL);

			log.info("accessToken => " + accessToken);
			log.info("instanceUrl => " + instanceUrl);
			
			if (accessToken == null) {
				response.getWriter().print("Error - no access token");
				return;
			}

			SFQuery sfQuery = new SFQuery(accessToken, instanceUrl);
			RTFConverter.convertToRTF(sfQuery.getTickets(minVer, maxVer, projectId));
			GGLService.docName = sfQuery.getProjectName(projectId);
			
//			we need to check docCounter, because there is a strange bug. If we call app first time a day then we will get three docs on GoogleDoc **********************
			if (GGLService.createGoogleDoc() && docCounter < 1) {
				response.getWriter().print("<b>Google doc successfully created on Google Drive</b>");
				docCounter++;
			} else {
				response.getWriter().print("<b>Error during document creating. Please, check app logs for getting more info</b>");
			}
		}
	}
	
}
