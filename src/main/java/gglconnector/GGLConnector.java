package gglconnector;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

public class GGLConnector {
	private static final String EMAIL_ADDRESS = "47363883779-f7u9dgqtnc14eq81rd1hnimrrcmv4b4l@developer.gserviceaccount.com";
	private static final String FILENAME_P12 = "TestProject-76a9dc16b67d.p12";
	
	private static GoogleCredential getCredential() throws GeneralSecurityException, IOException {
		JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		GoogleCredential credential = new GoogleCredential.Builder()
		    .setTransport(httpTransport)
		    .setJsonFactory(JSON_FACTORY)
		    .setServiceAccountId(EMAIL_ADDRESS)
		    .setServiceAccountPrivateKeyFromP12File(new File(FILENAME_P12))
		    .setServiceAccountScopes(Collections.singleton(DriveScopes.DRIVE))
		    .setServiceAccountUser("yevhenii.riabinin@gmail.com")
		    .build();
		return credential;
	}
	
	public static Drive getDrive() throws GeneralSecurityException, IOException{
		JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		Drive drive = new Drive.Builder(httpTransport, JSON_FACTORY, getCredential()).build();
		return drive;
	}
	
}
