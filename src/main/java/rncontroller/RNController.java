package rncontroller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
		
//		get logs page
		if (requestURI != null) { if (requestURI.endsWith("_logs")) {
			/*BufferedReader in = new BufferedReader(new FileReader("logs.txt"));
			String line;
			while ((line = in.readLine()) != null) {
				response.getWriter().println(line);
			}*/actionDisplayLog(request, response);
			return;
		} 
//		wake up heroku application by external request from SF app
		if (requestURI.endsWith("_ping")) {
			/*response.setStatus(200);*/actionPing(request, response);return;
		} else {
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
				if (accessToken == null) {
					request.setAttribute("errorMsg", "Fatal Error: unable to connect to Salesforce. Access token not available");
					isError = true;
//					request.getRequestDispatcher("/main.jsp").forward(request, response);
					return;
				}
	
				if (!isError) {
					SFQuery sfQuery = new SFQuery(accessToken, instanceUrl);
					File logo = sfQuery.getLogo(this.projectId);
					if (logo == null) {
						request.setAttribute("errorMsg", "Project must has the logo.png image in attachments for successful operation");
						request.getRequestDispatcher("/main.jsp").forward(request, response);
						return;
					}
					List<ReleaseNote> tickets = sfQuery.getTickets(this.minVer, this.maxVer, this.projectId);
					if (tickets.isEmpty()) {
						request.setAttribute("errorMsg", "There are no any appropriate tickets");
						isError = true;
//						request.getRequestDispatcher("/main.jsp").forward(request, response);
						return;
					}
					if (!isError) {
						RTFConverter.convertToRTF(tickets, logo, true);
//						GGLService.docName = sfQuery.getProjectName(this.projectId);
						
			//			bug-fix (3 docs were created instead of 1 after first calling during day)	***********************************************************  
						Long time = (Long) request.getSession().getAttribute("docTime");
						Long docTime = time == null ? 0 : time; 
						if ((System.currentTimeMillis() - docTime) > 60000) {
							if (GGLService.createGoogleDoc(this.projectName)) {
								request.setAttribute("gglResult", "Release Notes document was successfully created on Google Drive");
							} else {
								request.setAttribute("gglResult", "Error during document creating. Please, check app logs for getting more info");
							}
							
							RTFConverter.convertToRTF(tickets, logo);
							if (sfQuery.addAttachmentToProject(this.projectId, this.projectName)) {
								request.setAttribute("attResult", "Release Notes document was successfully added to the Project's attachments");
							} else {
								request.setAttribute("attResult", "Error during document creating. Please, check app logs for getting more info");
							}
							request.getSession().setAttribute("docTime", System.currentTimeMillis());
	//						request.getRequestDispatcher("/main.jsp").forward(request, response);
						}
					}
				}
				request.getRequestDispatcher("/main.jsp").forward(request, response);
			}
		}}
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
		File logo = sfQuery.getLogo(this.projectId);
		if (logo == null) {
			request.setAttribute("errorMsg", "Project must has the logo.png image in attachments for successful operation");
			request.getRequestDispatcher("/main.jsp").forward(request, response);
			return;
		}
		List<ReleaseNote> tickets = sfQuery.getTickets(this.minVer, this.maxVer, this.projectId);
		if (tickets == null || tickets.isEmpty()) {
			request.setAttribute("errorMsg", "There are no any appropriate tickets");
			request.getRequestDispatcher("/main.jsp").forward(request, response);
			return;
		}
		RTFConverter.convertToRTF(tickets, logo, true);
		GGLService.docName = sfQuery.getProjectName(this.projectId);
		
//		bug-fix (3 docs were created instead of 1 after first calling during day)	***********************************************************  
		Long time = (Long) request.getSession().getAttribute("docTime");
		Long docTime = time == null ? 0 : time; 
		if ((System.currentTimeMillis() - docTime) > 60000) {
			if (GGLService.createGoogleDoc()) {
				request.setAttribute("gglResult", "Release Notes document was successfully created on Google Drive");
			} else {
				request.setAttribute("gglResult", "Error during document creating. Please, check app logs for getting more info");
			}
			
			RTFConverter.convertToRTF(tickets, logo);
			if (sfQuery.addAttachmentToProject(this.projectId)) {
				request.setAttribute("attResult", "Release Notes document was successfully added to the Project's attachments");
			} else {
				request.setAttribute("attResult", "Error during document creating. Please, check app logs for getting more info");
			}
			request.getSession().setAttribute("docTime", System.currentTimeMillis());
			request.getRequestDispatcher("/main.jsp").forward(request, response);
		}
	}
}
