package rncontroller;

import gglconnector.GGLConnector;
import gglconnector.GGLFileManager;

import java.io.IOException;
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

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SFConnector sfConnector = new SFConnector();
		sfConnector.getAccessToSalesforce(request, response);
		
		DemoREST demoREST = new DemoREST();
		demoREST.getInfo(request, response);
		//getServletContext().getRequestDispatcher("index.jsp").forward(request, response);
	}
	
}
