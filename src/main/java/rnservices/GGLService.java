package rnservices;

import gglconnector.GGLConnector;
import gglconnector.GGLFileManager;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Foo
 */
public class GGLService extends HttpServlet {
	private static final long serialVersionUID = 5599195927405315789L;
	public static String docName;

		/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		GGLFileManager fileManager = new GGLFileManager();
		try {
			if (docName == null) {
				docName = "Test document";
			}
			fileManager.insertFile(GGLConnector.getDrive(), docName, "document description", "", "application/rtf", "ReleaseNotes.rtf");
		} catch (GeneralSecurityException e) {
			response.getWriter().print("Fail");
			e.printStackTrace();
		}
		response.getWriter().print("<a href='https://tranquil-taiga-6535.herokuapp.com'>Back to main page</a><br/>");
		response.getWriter().print("<b>Google doc successfully created on Google Drive</b>");
	}
	
	public static void createGoogleDoc () {
		GGLFileManager fileManager = new GGLFileManager();
		try {
			if (docName == null) {
				docName = "Test document";
			}
			fileManager.insertFile(GGLConnector.getDrive(), docName, "document description", "", "application/rtf", "ReleaseNotes.rtf");
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
