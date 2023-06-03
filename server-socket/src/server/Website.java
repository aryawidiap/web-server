package server;

public class Website {
	private String url;
	private String rootDir;
	public Website(String url, String rootDir) {
		super();
		this.url = url;
		this.rootDir = rootDir;
	}
	public String getUrl() {
		return url;
	}
	public String getRootDir() {
		return rootDir;
	}
	
	
}
