package rnservices;

import gglconnector.GGLConnector;
import gglconnector.GGLFileManager;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class GGLService {
	public static final String FILENAME = "ReleaseNotes.rtf";
	
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
