package sfconnector;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;

import models.ReleaseNote;

import org.apache.commons.codec.binary.Base64;
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

import rnservices.GGLService;

/**
 * Servlet implementation class DemoREST
 */
public class SFQuery {
	private String accessToken;
	private String instanceUrl;
	
	private Logger log = LogManager.getLogManager().getLogger("rnotes");
	
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
		if (ver1 != null && !ver1.isEmpty() &&
			ver2 != null && !ver2.isEmpty() &&
			projectId != null && !projectId.isEmpty()) {
			
			HttpClient httpclient = new HttpClient();
			
			// set the SOQL as a query param
			NameValuePair[] params = new NameValuePair[1];
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("SELECT Id, Name, Fixed_in_Ver__c, Release_Notes__c, Est_Due_Date__c ")
						 .append("FROM Ticket__c ")
						 .append("WHERE (Fixed_in_Ver__c >= '").append(ver1)
						 .append("' AND Fixed_in_Ver__c <= '").append(ver2).append("')")
						 .append("AND Project__c = '").append(projectId).append("'")
						 .append("LIMIT 100");
			params[0] = new NameValuePair("q", stringBuilder.toString());
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
						if (results != null) {
							releaseNotes = convertToReleaseNotes(results);
						}
					} catch (JSONException e) {
						e.printStackTrace();
						throw new ServletException(e);
					}
				} else {
					log.warning("STATUS CODE " + statusCode);
				}
			} finally {
				getMethod.releaseConnection();
			}
		}
		return releaseNotes;
	}
	
//	maybe we should delete this method and get SF Project name in the same request from which one we get Project Id *******************************************************
	/*public String getProjectName(String projectId) throws ServletException, IOException {
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
	}*/

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

//	create new logo.ong file from bytes array
	private File getLogoFromBytes(GetMethod getMethod) {
		File logo = null;
		HttpClient httpclient = new HttpClient();
		try {
			httpclient.executeMethod(getMethod);
			int statusCode = getMethod.getStatusCode(); 
			if (statusCode == HttpStatus.SC_OK) {
				byte[] responseBody = getMethod.getResponseBody();
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
	
	public boolean addAttachmentToProject(String projectId, String projectName) {
		boolean result = false;
		log.info("addAttachmentToProject called");
		if (!projectName.equals(null)) {
			log.info(projectName);
			HttpClient httpclient = new HttpClient();
			
			JSONObject attachment = new JSONObject();
			attachment.put("Name", projectName + " Release Notes.rtf");
			attachment.put("Body", encodeFileToBase64Binary("ReleaseNotes.rtf"));
			attachment.put("ParentId", projectId);
			
			PostMethod postMethod = createPostMethod();
			log.info("Post method created");
			try {
				postMethod.setRequestEntity(new StringRequestEntity(attachment.toString(), "application/json", null));
				httpclient.executeMethod(postMethod);
				int status = postMethod.getStatusCode();
				if (status >= 200 && status < 300) {
					result = true;
				}
				log.info("REQUEST STATUS CODE => " + status);
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
	        throw new IOException("Could not completely read file " + file.getName());
	    }

	    is.close();
	    return bytes;
	}
	
	private List<ReleaseNote> convertToReleaseNotes(JSONArray results) {
		List<ReleaseNote> releaseNotes = new ArrayList<ReleaseNote>();
		if (results != null) {
			for (int i = 0; i < results.length(); i++) {
				JSONObject ticket = results.getJSONObject(i);
				String ticketId = ticket.getString("Id");
				String ticketFixedVersion = "";
				String ticketDate = "";
				String ticketReleaseNotes = "";
				if (ticket.get("Fixed_in_Ver__c") instanceof String) {
					ticketFixedVersion = ticket.getString("Fixed_in_Ver__c");
				}
				if (ticket.get("Est_Due_Date__c") instanceof String) {
					ticketDate = ticket.getString("Est_Due_Date__c");
				}
				if (ticket.get("Release_Notes__c") instanceof String) {
					ticketReleaseNotes = ticket.getString("Release_Notes__c");
				}
				releaseNotes.add(new ReleaseNote(ticketId, ticketDate, ticketFixedVersion, ticketReleaseNotes));
			}
		}
		return releaseNotes;
	}
}
