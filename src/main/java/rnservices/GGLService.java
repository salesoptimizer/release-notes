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

		/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		GGLFileManager fileManager = new GGLFileManager();
		try {
			fileManager.insertFile(GGLConnector.getDrive(), "TESTTTTT DOCUMENT", "document description", "", "text/plain", "document1.txt");
		} catch (GeneralSecurityException e) {
			response.getWriter().print("Fail");
			e.printStackTrace();
		}
		response.getWriter().print("OK");
	}

}
