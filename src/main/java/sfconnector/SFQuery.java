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
	private static final String LOGO_NAME = "logo.png";
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
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("SELECT Id, Name, Fixed_in_Ver__c, Release_Notes__c, Est_Due_Date__c ")
						 .append("FROM Ticket__c ")
						 .append("WHERE (Fixed_in_Ver__c >= '").append(ver1)
						 .append("' AND Fixed_in_Ver__c <= '").append(ver2).append("')")
						 .append("AND Project__c = '").append(projectId).append("'")
						 .append("LIMIT 100");
			NameValuePair[] params = { new NameValuePair("q", stringBuilder.toString()) };
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
						log.severe(e.getMessage());
						throw new ServletException(e);
					}
				} else {
					log.warning("getTickets() => STATUS CODE " + statusCode);
				}
			} finally {
				getMethod.releaseConnection();
			}
		}
		return releaseNotes;
	}

	public File getLogo(String projectId) {
		File logo = null;
		if (projectId != null && !projectId.isEmpty()) {
			HttpClient httpclient = new HttpClient();
			
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("SELECT Id FROM Attachment WHERE ParentId = '").append(projectId).append("' AND Name = 'logo.png' LIMIT 1");
			NameValuePair[] params = { new NameValuePair("q", stringBuilder.toString()) };
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
						String logoId = results != null ? results.getJSONObject(0).getString("Id") : "";
						
						if (!logoId.isEmpty()) {
							logo = getLogoFromBytes(logoId);
						}
					} catch (JSONException e) {
						log.severe(e.getMessage());
						e.printStackTrace();
					}
				} else {
					log.warning("getLogo() => STATUS CODE " + statusCode);
				}
			} catch (HttpException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
			} finally {
				getMethod.releaseConnection();
			}
		}
		return logo;
	}

//	create new logo.ong file from bytes array
	private File getLogoFromBytes(String logoId) {
		File logo = null;
		if (logoId != null && !logoId.isEmpty()) {
			GetMethod getMethod = createGetAttachmentMethod(logoId);
			HttpClient httpclient = new HttpClient();
			try {
				httpclient.executeMethod(getMethod);
				int statusCode = getMethod.getStatusCode(); 
				if (statusCode == HttpStatus.SC_OK) {
					byte[] responseBody = getMethod.getResponseBody();
					BufferedImage image = readImage(responseBody);
					logo = new File(LOGO_NAME);
					ImageIO.write(image, "png", logo);
				}
			} catch (HttpException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
			} finally {
				getMethod.releaseConnection();
			}
		}
		return logo;
	}
	
	public boolean addAttachmentToProject(String projectId, String projectName) {
		boolean result = false;
		log.info("addAttachmentToProject called");
		if (projectName != null && !projectName.isEmpty() &&
			projectId != null && !projectId.isEmpty()) {
			log.info(projectName);
			HttpClient httpclient = new HttpClient();
			
			JSONObject attachment = new JSONObject();
			attachment.put("Name", projectName + " " + GGLService.FILENAME);
			attachment.put("Body", encodeFileToBase64Binary(GGLService.FILENAME));
			attachment.put("ParentId", projectId);
			
			PostMethod postMethod = createPostMethod();
			log.info("Post method created");
			try {
				postMethod.setRequestEntity(new StringRequestEntity(attachment.toString(), "application/json", null));
				httpclient.executeMethod(postMethod);
				int status = postMethod.getStatusCode();
				if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
					result = true;
				}
				log.info("REQUEST STATUS CODE => " + status);
			} catch (UnsupportedEncodingException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
			} catch (HttpException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
			}
		}
		return result;
	}
	
	private String encodeFileToBase64Binary(String fileName) {
		String encodedString = "";
		if (fileName != null && !fileName.isEmpty()){
			File file = new File(fileName);
			byte[] bytes = null;
			try {
				bytes = loadFile(file);
				encodedString = Base64.encodeBase64String(bytes);
			} catch (IOException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
			}
		}
		return encodedString;
	}

	private byte[] loadFile(File file) throws IOException {
		byte[] bytes = null;
		if (file != null) {
			InputStream is = null;
			try {
			    is = new FileInputStream(file);
			    long length = file.length();
			    if (length > Integer.MAX_VALUE) {
			        throw new IOException("File is too large: " + file.length() / 1000 + "KB");
			    }
			    bytes = new byte[(int)length];
			    int offset = 0;
			    int numRead = 0;
			    while (offset < bytes.length 
			    		&& (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			        offset += numRead;
			    }
			    if (offset < bytes.length) {
			        throw new IOException("Could not completely read file " + file.getName());
			    }
			} catch(IOException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
			} finally {
				if (is != null) {
					is.close();
				}
			}
		}
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
				/*if (ticket.get("RN_Rich__c") instanceof String) {
					ticketReleaseNotes = ticket.getString("RN_Rich__c");
				}*/
				releaseNotes.add(new ReleaseNote(ticketId, ticketDate, ticketFixedVersion, ticketReleaseNotes));
			}
		}
		return releaseNotes;
	}
	
	private BufferedImage readImage(byte[] responseBody) throws IOException {
		BufferedImage image = null;
		if (responseBody != null) {
			ByteArrayInputStream bis = null;
			try {
				bis = new ByteArrayInputStream(responseBody);
				image = ImageIO.read(bis);
			} catch (IOException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
			} finally {
				if (bis != null) {
					bis.close();
				}
			}
		}
		return image;
	}
}
