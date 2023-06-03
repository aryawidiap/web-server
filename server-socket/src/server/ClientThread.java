package server;

import java.awt.print.Printable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientThread extends Thread {
	
	private ServerSocket server;
	private String host;
	private  HashMap<String, String> websiteList = new HashMap<String, String>();
	private  HashMap<String, String> extensionTypes= new HashMap<String, String>();
	
	public void setServer(ServerSocket server) {
		this.server = server;
	}


	public ClientThread(ServerSocket server, HashMap<String, String> websiteList) {
		super();
		this.server = server;
		this.host = "";
		this.websiteList = websiteList;
		this.extensionTypes.put("html", "text");
		this.extensionTypes.put("php", "text");
		this.extensionTypes.put("js", "text");
		this.extensionTypes.put("json", "text");
		this.extensionTypes.put("txt", "text");
	}


	@Override
	public void run() {
		try {
			TextExtraction te = new TextExtraction();
			Socket client = server.accept();
			boolean keepAlive = false;
			System.out.println("New Client!");
			do {
				DataOutputStream bos = new DataOutputStream(client.getOutputStream());
				DataInputStream bis = new DataInputStream(client.getInputStream());
				
				ReadFile rf = new ReadFile();
				int line = 0;
				while (true) {
					line++;
					// String buf = new String(bis.readAllBytes());
					@SuppressWarnings("deprecation")
					String buf = bis.readLine(); // Alternative -> BufferedReader
					if (buf.equals("")) {
						break;
					}
					// Ambil alamat requestedFile
					if (line == 1) {
						String reqFile = "";
						reqFile = te.extractReqFileDir(buf); // mungkin perlu di modify			
						if (reqFile.equals("")) {
							rf.setRequestedFile("index.html");
						} else {
							rf.setRequestedFile(reqFile);
						}
						continue;
					}
					if(line == 2) {
						host = te.extractHost(buf);
						if(!this.websiteList.get(host).equals("")) {						
//							rf.setRequestedFile(this.websiteList.get(host)+ "/" + rf.getRequestedFile());
							rf.setDir(this.websiteList.get(host) + "/");
						}
						continue;
					}
					if(buf.contains("Connection: keep-alive")) {
						//keepAlive = true;
					}
					buf += " (from server)";
					System.out.println("Received a message: " + buf);
				}
				
				System.out.println("Keep alive is " + keepAlive);
				
				rf.retrieveFile();
				String response = "";
				// cek rf
				if (rf.isFileExist()) {
					// Response message format
					// HTTP/1.0 status_code msg_status_code
					// Key: value
					// Key: value
					// Key: value
					//
					// CONTENT
					response = "HTTP/1.0 200 OK\r\n";
					if(keepAlive) {
						response += "Connection: Keep-Alive\r\n";						
						response += "Keep-Alive: timeout=50, max=1000\r\n";						
					}
					String ext = te.extractExt( rf.getRequestedFile() );
					if(!(extensionTypes.get(ext).equals("text"))) {
						keepAlive = false;
						response += "Content-Type: application/octet-stream\r\n"
								+ "Content-Disposition: attachment; filename=\""
								+ rf.getRequestedFile() + "\"\r\n";	
						System.out.println("Dir+name: "+ rf.getDir() + rf.getRequestedFile());
						response += "\r\n";
						bos.write(response.getBytes());
						File file = new File(rf.getDir() + rf.getRequestedFile());
						Files.copy(file.toPath(), bos);
						continue;
					} //else {						
					response += "\r\n";
					response += rf.getContent();
//					response += "Date: "+ LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z"))+"\r\n"
//							+ "Last-Modified: "+ LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z"))+"\r\n"
//							+ "Server: Apache";
					//}
				} else {
					if (rf.getRequestedFile().equals("index.html")) {
						// show directory
						ServerDirectory directory = new ServerDirectory();
						directory.setFileSet(ServerDirectory.listFilesUsingDirectoryStream(System.getProperty("user.dir") + "\\" + rf.getDir().replace("/", "\\")));
						
						response = "HTTP/1.0 404 File does not exist\r\n";
						response += "\r\n";
						response += "<html><body><h1>File Directory</h1>"
								+ directory.listDirectoryAsHTML(host)
								+ "</body></html>";
						//System.out.println("Directory listed is  " + System.getProperty("user.dir"));
					} else {
						// CONTENT
						response = "HTTP/1.0 404 File does not exist\r\n";
						response += "\r\n";
						response += "<html><body><h1>We don't find what you are looking for. Sorry :(</h1></body></html>";
					}
				}
				
				System.out.println(response);
				bos.write(response.getBytes());
				System.out.println("Bos write successful.");
			} while (keepAlive);
			client.close();
		} catch (Exception e) {
			Logger.getLogger(StreamTest.class.getName()).log(Level.SEVERE, null, e);
		}
		// TODO Auto-generated method stub
	}
}
