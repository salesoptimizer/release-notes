package models;

public class ReleaseNote {
	private String ticketId;
	private String ticketName;
	private String packVersion;
	private String releaseNotes;

	public ReleaseNote() {

	}

	public ReleaseNote(String ticketId, String ticketName, String packVersion,
			String releaseNotes) {
		this.ticketId = ticketId;
		this.ticketName = ticketName;
		this.packVersion = packVersion;
		this.releaseNotes = releaseNotes;
	}

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public String getTicketName() {
		return ticketName;
	}

	public void setTicketName(String ticketName) {
		this.ticketName = ticketName;
	}

	public String getPackVersion() {
		return packVersion;
	}

	public void setPackVersion(String packVersion) {
		this.packVersion = packVersion;
	}

	public String getReleaseNotes() {
		return releaseNotes;
	}

	public void setReleaseNotes(String releaseNotes) {
		this.releaseNotes = releaseNotes;
	}
}
