package models;

public class ReleaseNote {
	private String ticketId;
	private String ticketDate;
	private String packVersion;
	private String releaseNotes;

	public ReleaseNote() {

	}

	public ReleaseNote(String ticketId, String ticketDate, String packVersion,
			String releaseNotes) {
		this.ticketId = ticketId;
		this.ticketDate = ticketDate;
		this.packVersion = packVersion;
		this.releaseNotes = releaseNotes;
	}

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public String getTicketDate() {
		return ticketDate;
	}

	public void setTicketDate(String ticketDate) {
		this.ticketDate = ticketDate;
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
