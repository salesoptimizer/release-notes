package gglconnector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Properties;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;


public class GGLConnector {
	private static final String PARAMS_FILE = "src/main/resources/params.properties";
	private static final String GOOGLE_ACCOUNT = "rnotes.soptimizer@gmail.com";
//	private static final String EMAIL_ADDRESS = "183282861003-2v0spvht82l2pr9fdvb0a7t9p8g936fo@developer.gserviceaccount.com";
	private static String emailAddress;
//	private static final String FILENAME_P12 = "release-notes-5c2b2bc466ef.p12";
	private static String filenameP12;
	
	private static GoogleCredential getCredential() throws GeneralSecurityException, IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(new File(PARAMS_FILE)));
		emailAddress = properties.getProperty("EMAIL_ADDRESS");
		filenameP12 = properties.getProperty("FILENAME_P12");
		JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		return new GoogleCredential.Builder()
			    .setTransport(httpTransport)
			    .setJsonFactory(JSON_FACTORY)
			    .setServiceAccountId(emailAddress)
			    .setServiceAccountPrivateKeyFromP12File(new File(filenameP12))
			    .setServiceAccountScopes(Collections.singleton(DriveScopes.DRIVE))
			    .setServiceAccountUser(GOOGLE_ACCOUNT)
			    .build();
	}
	
	public static Drive getDrive() throws GeneralSecurityException, IOException{
		JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		return new Drive.Builder(httpTransport, JSON_FACTORY, getCredential()).build();
	}
	
}