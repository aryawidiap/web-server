package server;

public class ServerFile {
	public ServerFile(String fileName, String lastModifiedDate, long size) {
		super();
		this.fileName = fileName;
		this.lastModifiedDate = lastModifiedDate;
		this.size = size;
	}
	private final String fileName;
	private final String lastModifiedDate; 
	private final long size;
	public String getFileName() {
		return fileName;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public long getSize() {
		return size;
	}
}
