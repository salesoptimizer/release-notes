package rnservices;

import gglconnector.GGLConnector;
import gglconnector.GGLFileManager;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class GGLService {
	public static final String FILENAME = "ReleaseNotes.rtf";
	private static Logger log = LogManager.getLogManager().getLogger("rnotes");
	
	public static boolean createGoogleDoc(String docName) {
		GGLFileManager fileManager = new GGLFileManager();
		boolean result = false;
		try {
			if (docName == null) {
				docName = "Test document";
			}
			fileManager.insertFile(GGLConnector.getDrive(), docName, "document description", "", "application/rtf", FILENAME);
			result = true;
		} catch (GeneralSecurityException e) {
			log.severe(e.getMessage());
		} catch (IOException e) {
			log.severe(e.getMessage());
		}
		return result;
	}
}
