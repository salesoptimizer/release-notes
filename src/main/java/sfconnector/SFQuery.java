package sfconnector;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.ReleaseNote;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import rnservices.GGLService;

/**
 * Servlet implementation class DemoREST
 */
public class SFQuery {
	private static final long serialVersionUID = 1L;
	private String accessToken;
	private String instanceUrl;
	
	private Logger log2 = LogManager.getLogManager().getLogger("rnotes");
	
	public SFQuery(String accessToken, String instanceUrl) {
		this.accessToken = accessToken;
		this.instanceUrl = instanceUrl;
	}

	private GetMethod createGetMethod() {
		GetMethod get = new GetMethod(this.instanceUrl + "/services/data/v20.0/query");
		get.setRequestHeader("Authorization", "OAuth " + this.accessToken);
		return get;
	}
	
	private GetMethod createGetAttachmentMethod(String id) {
		GetMethod get = new GetMethod(this.instanceUrl + "/services/data/v20.0/sobjects/Attachment/" + id + "/body");
		get.setRequestHeader("Authorization", "OAuth " + this.accessToken);
		return get;
	}
	
	private PostMethod createPostMethod() {
		PostMethod post = new PostMethod(this.instanceUrl + "/services/data/v20.0/sobjects/Attachment/");
		post.setRequestHeader("Authorization", "OAuth " + this.accessToken);
		return post;
	}
	
	public List<ReleaseNote> getTickets(String ver1, String ver2, String projectId) throws ServletException, IOException {
		List<ReleaseNote> releaseNotes = new ArrayList<ReleaseNote>();
		HttpClient httpclient = new HttpClient();
		
		// set the SOQL as a query param
		NameValuePair[] params = new NameValuePair[1];
		params[0] = new NameValuePair("q",
				"SELECT Id, Name, Fixed_in_Ver__c, Release_Notes__c, Est_Due_Date__c "
			  + "FROM Ticket__c "
			  + "WHERE (Fixed_in_Ver__c >= '" + ver1 + "' AND Fixed_in_Ver__c <= '" + ver2 + "')"
			  + "AND Project__c = '" + projectId + "'"
	  		  + "LIMIT 100");
		GetMethod getMethod = createGetMethod();
		getMethod.setQueryString(params);
		
		try {
			httpclient.executeMethod(getMethod);
			int statusCode = getMethod.getStatusCode(); 
			if (statusCode == HttpStatus.SC_OK) {
				// Now lets use the standard java json classes to work with the results
				String responseBody = getMethod.getResponseBodyAsString();
				try {
					JSONObject response = new JSONObject(responseBody);
					JSONArray results = response.getJSONArray("records");
					for (int i = 0; i < results.length(); i++) {
						String ticketId = results.getJSONObject(i).getString("Id");
						String ticketFixedVersion = "";
						String ticketDate = "";
						String ticketReleaseNotes = "";
						if (results.getJSONObject(i).get("Fixed_in_Ver__c") instanceof String) {
							ticketFixedVersion = results.getJSONObject(i).getString("Fixed_in_Ver__c");
						}
						if (results.getJSONObject(i).get("Est_Due_Date__c") instanceof String) {
							ticketDate = results.getJSONObject(i).getString("Est_Due_Date__c");
						}
						if (results.getJSONObject(i).get("Release_Notes__c") instanceof String) {
							ticketReleaseNotes = results.getJSONObject(i).getString("Release_Notes__c");
						}
						releaseNotes.add(new ReleaseNote(ticketId, ticketDate, ticketFixedVersion, ticketReleaseNotes));
					}
				} catch (JSONException e) {
					e.printStackTrace();
					throw new ServletException(e);
				}
			} else {
				log2.warning("STATUS CODE " + statusCode);
			}
		} finally {
			getMethod.releaseConnection();
		}
		return releaseNotes;
	}
	
	public String getProjectName(String projectId) throws ServletException, IOException {
		String projectName = null;
		HttpClient httpclient = new HttpClient();
		
		// set the SOQL as a query param
		NameValuePair[] params = new NameValuePair[1];
		params[0] = new NameValuePair("q",
				"SELECT Name from SFDC_Project__c WHERE Id = '" + projectId + "' LIMIT 1");
		GetMethod getMethod = createGetMethod();
		getMethod.setQueryString(params);
		
		try {
			httpclient.executeMethod(getMethod);
			int statusCode = getMethod.getStatusCode(); 
			if (statusCode == HttpStatus.SC_OK) {
				// Now lets use the standard java json classes to work with the results
				String responseBody = getMethod.getResponseBodyAsString();
				try {
					JSONObject response = new JSONObject(responseBody);
					JSONArray results = response.getJSONArray("records");
					for (int i = 0; i < results.length(); i++) {
						projectName = results.getJSONObject(i).getString("Name"); 
					}
				} catch (JSONException e) {
					e.printStackTrace();
					throw new ServletException(e);
				}
			}
		} finally {
			getMethod.releaseConnection();
		}
		return projectName;
	}

	public File getLogo(String projectId) {
		File logo = null;
		HttpClient httpclient = new HttpClient();
		
		NameValuePair[] params = new NameValuePair[1];
		params[0] = new NameValuePair("q",
				"SELECT Id FROM Attachment WHERE ParentId = '" + projectId + "' AND Name = 'logo.png' LIMIT 1");
		GetMethod getMethod = createGetMethod();
		getMethod.setQueryString(params);
		
		try {
			httpclient.executeMethod(getMethod);
			int statusCode = getMethod.getStatusCode(); 
			if (statusCode == HttpStatus.SC_OK) {
				String responseBody = getMethod.getResponseBodyAsString();
				try {
					JSONObject response = new JSONObject(responseBody);
					JSONArray results = response.getJSONArray("records");
					String logoId = results.getJSONObject(0).getString("Id");
					
					if (!logoId.isEmpty()) {
						getMethod = createGetAttachmentMethod(logoId);
						logo = getLogoFromBytes(getMethod);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			getMethod.releaseConnection();
		}
		return logo;
	}
	
	private File getLogoFromBytes(GetMethod getMethod) {
		File logo = null;
		HttpClient httpclient = new HttpClient();
		try {
			httpclient.executeMethod(getMethod);
			int statusCode = getMethod.getStatusCode(); 
			if (statusCode == HttpStatus.SC_OK) {
				byte[] responseBody = getMethod.getResponseBody();
//				String imageString = Base64.encodeBase64String(responseBody);
				BufferedImage image = null;
				ByteArrayInputStream bis = new ByteArrayInputStream(responseBody);
				image = ImageIO.read(bis);
				bis.close();
				logo = new File("logo.png");
				ImageIO.write(image, "png", logo);
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			getMethod.releaseConnection();
		}
		return logo;
	}
	
	public boolean addAttachmentToProject(String projectId) {
		boolean result = false;
		log2.info("addAttachmentToProject called");
		if (!GGLService.docName.equals(null)) {
			log2.info(GGLService.docName);
			HttpClient httpclient = new HttpClient();
			
			JSONObject attachment = new JSONObject();
			attachment.put("Name", GGLService.docName + " Release Notes.rtf");
			attachment.put("Body", encodeFileToBase64Binary("ReleaseNotes.rtf"));
			attachment.put("ParentId", projectId);
			
			PostMethod postMethod = createPostMethod();
			log2.info("Post method created");
			try {
				postMethod.setRequestEntity(new StringRequestEntity(attachment.toString(), "application/json", null));
				httpclient.executeMethod(postMethod);
				int status = postMethod.getStatusCode();
				if (status >= 200 && status < 300) {
					result = true;
				}
				log2.info("REQUEST STATUS CODE => " + status);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	private String encodeFileToBase64Binary(String fileName) {

		File file = new File(fileName);
		byte[] bytes;
		String encodedString = null;
		try {
			bytes = loadFile(file);
			encodedString = Base64.encodeBase64String(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return encodedString;
	}

	private static byte[] loadFile(File file) throws IOException {
	    InputStream is = new FileInputStream(file);

	    long length = file.length();
	    if (length > Integer.MAX_VALUE) {
	        // File is too large
	    }
	    byte[] bytes = new byte[(int)length];
	    
	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length
	           && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	        offset += numRead;
	    }

	    if (offset < bytes.length) {
	        throw new IOException("Could not completely read file "+file.getName());
	    }

	    is.close();
	    return bytes;
	}
	
}
