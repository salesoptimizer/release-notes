package rncontroller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;

import models.ReleaseNote;
import rnservices.GGLService;
import rnservices.RTFConverter;
import sfconnector.SFQuery;
import sfconnector.SFConnector;

/**
 * Servlet implementation class MainController
 */
public class RNController extends HttpServlet {
	private static final long serialVersionUID = 7546372886300391908L;
	
	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String INSTANCE_URL = "INSTANCE_URL";
	private static String accessToken;
	private static String instanceUrl;
	
	private String minVer;
	private String maxVer;
	private String projectId;
	private String projectName;
	
	private static Logger log = Logger.getLogger("rnotes");

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LogManager.getLogManager().readConfiguration(RNController.class.getResourceAsStream("/logging.properties"));
		String requestURI = request.getRequestURI(); 

		if (requestURI != null) {
//			get logs page
			if (requestURI.endsWith("_logs")) {
				actionDisplayLog(request, response);
				return;
			} 
	//		wake up heroku application by request from SF app
			if (requestURI.endsWith("_ping")) {
				actionPing(request, response);
				return;
			} else {
//				create GoogleDoc and Project attachment
				actionCreateVersionsFile(request, response);
			}
		}
	}
	
	private void actionDisplayLog(HttpServletRequest request, HttpServletResponse response) throws IOException {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader("logs.txt"));
			String line;
			while ((line = in.readLine()) != null) {
				response.getWriter().println(line);
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
	
	private void actionPing(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(HttpStatus.SC_OK);
	}
	
	private void actionCreateVersionsFile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String newMinVer = request.getParameter("minVer");
		String newMaxVer = request.getParameter("maxVer");
		String newProjectId = request.getParameter("projectId");
		String newProjectName = request.getParameter("projectName");
		this.minVer = newMinVer == null ? this.minVer : newMinVer;
		this.maxVer = newMaxVer == null ? this.maxVer : newMaxVer;
		this.projectId = newProjectId == null ? this.projectId : newProjectId;
		this.projectName = newProjectName == null ? this.projectName : newProjectName;
		if (this.minVer != null && this.maxVer != null && this.projectId != null && this.projectName != null) {
			log.info("minVer => " + this.minVer + 
					 " maxVer => " + this.maxVer + 
					 " projectId => " + this.projectId + 
					 " projectName => " + this.projectName);
			
			
			SFConnector sfConnector = new SFConnector();
			sfConnector.getAccessToSalesforce(request, response);

			accessToken = (String) request.getSession().getAttribute(ACCESS_TOKEN);
			instanceUrl = (String) request.getSession().getAttribute(INSTANCE_URL);
			
			log.info("accessToken => " + accessToken);
			log.info("instanceUrl => " + instanceUrl);
			
			boolean isError = false;
			boolean showPage = true;
			if (accessToken == null) {
				request.setAttribute("errorMsg", "Fatal Error: unable to connect to Salesforce. Access token not available");
				isError = true;
				return;
			}

			if (!isError) {
				SFQuery sfQuery = new SFQuery(accessToken, instanceUrl);
				File logo = sfQuery.getLogo(this.projectId);
				List<ReleaseNote> tickets = sfQuery.getTickets(this.minVer, this.maxVer, this.projectId);
				log.info("tickets.isEmpty() => " + tickets.isEmpty());
				if (tickets.isEmpty()) {
					request.setAttribute("errorMsg", "There are no any appropriate tickets. Release Notes were not able to generate.");
					isError = true;
				}
				if (!isError) {
					RTFConverter.convertToRTF(tickets, logo, true);
					
//					There was a bug about creating 3 docs instead of one. It happens once a day, when we use an app first time a day.
//					So we have to check if we already created (or tried to create) documents last 5 sec. If we did than we will do nothing.
					Long time = (Long) request.getSession().getAttribute("docTime");
					Long docTime = time == null ? 0 : time; 
					
					if ((System.currentTimeMillis() - docTime) > 5000) {
						if (GGLService.createGoogleDoc(this.projectName)) {
							request.setAttribute("gglResult", "Release Notes document was successfully created on Google Drive");
						} else {
							request.setAttribute("gglResult", "Error during document creating. Please, check app logs for getting more info");
						}
						
						RTFConverter.convertToRTF(tickets, logo, false);
						if (sfQuery.addAttachmentToProject(this.projectId, this.projectName)) {
							request.setAttribute("attResult", "Release Notes document was successfully added to the Project's attachments");
						} else {
							request.setAttribute("attResult", "Error during document creating. Please, check app logs for getting more info");
						}
						request.getSession().setAttribute("docTime", System.currentTimeMillis());
					} else {
						showPage = false;
					}
				}
			}
			if (showPage) {
				request.getRequestDispatcher("/main.jsp").forward(request, response);
			}
		}
	}
}
