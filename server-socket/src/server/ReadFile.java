package server;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class ReadFile {
	private boolean fileExist;
	private String requestedFile;
	private String content;
	private String dir;
	
	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getContent() {
		return content;
	}
	
	public void setContent(String file) {
		this.content = file;
	}
	
	public boolean isFileExist() {
		return fileExist;
	}
	
	
	public void setFileExist(boolean fileExist) {
		this.fileExist = fileExist;
	}
	
	// Ref: w3schools.com
	public void retrieveFile(){
		try {
		      File myObj = new File(dir + requestedFile);
		      Scanner myReader = new Scanner(myObj);
		      String contentBuf = "";
		      while (myReader.hasNextLine()) {
		        contentBuf += myReader.nextLine() + "\r\n";
		      }
		      myReader.close();
		      setContent(contentBuf);
		      setFileExist(true);
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      setFileExist(false);
		      e.printStackTrace();
		    }
	}
	
	public ReadFile() {
		fileExist = false;
		content = null;
		dir = "";
	}

	public String getRequestedFile() {
		return requestedFile;
	}

	public void setRequestedFile(String requestedFile) {
		this.requestedFile = requestedFile;
	}
}
