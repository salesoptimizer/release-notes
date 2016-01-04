package gglconnector;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class GGLFileManager {
	private Logger log = LogManager.getLogManager().getLogger("rnotes");
	/**
	 * Insert new file.
	 *
	 * @param service
	 * Drive API service instance.
	 * @param title
	 * Title of the file to insert, including the extension.
	 * @param description
	 * Description of the file to insert.
	 * @param parentId
	 * Optional parent folder's ID.
	 * @param mimeType
	 * MIME type of the file to insert.
	 * @param filename
	 * Filename of the file to insert.
	 * @return Inserted file metadata if successful, {@code null} otherwise.
	 */
	public void insertFile(Drive service, String title, String description,
			String parentId, String mimeType, String filename) {
		// File's metadata.
		File body = createFileBody(title, description, parentId, mimeType);

		// File's content.
		FileContent mediaContent = null;
		if (filename != null && !filename.isEmpty()) {
			java.io.File fileContent = new java.io.File(filename);
			mediaContent = new FileContent(mimeType, fileContent);
		}
		if (body != null && mediaContent != null) {
			try {
				File file = service.files().insert(body, mediaContent).execute();
				System.out.println("File ID: " + file.getId());
				// return file;
			} catch (IOException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private File createFileBody(String title, String description, String parentId, String mimeType) {
		File body = null;
		if (title != null && description != null) {
			body = new File();
			body.setTitle(title);
			body.setDescription(description);
			body.setMimeType("application/vnd.google-apps.document");
		}
		
		// Set the parent folder.
		if (parentId != null && parentId.length() > 0) {
			body.setParents(Arrays.asList(new ParentReference().setId(parentId)));
		}
		return body;
	}
	
	public static List<File> retrieveAllFiles(Drive service) throws IOException {
		List<File> result = new ArrayList<File>();
		Files.List request = service.files().list();

		do {
			try {
				FileList files = request.execute();
				result.addAll(files.getItems());
				request.setPageToken(files.getNextPageToken());
			} catch (IOException e) {
				System.out.println("An error occurred: " + e);
				request.setPageToken(null);
			}
		} while (request.getPageToken() != null
				&& request.getPageToken().length() > 0);

		return result;
	}

}
