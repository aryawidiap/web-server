package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StreamTestSingle {
	
	private static String ipAddr;
	private static int port;
	private static HashMap<String, String> websiteList = new HashMap<String, String>();
	private static void setIpAndPort() {
		try {
			File myObj = new File("config.ini");
			Scanner myReader;
			myReader = new Scanner(myObj);
			String empty = "";
			while (myReader.hasNextLine()) {
				String curLine = myReader.nextLine();
				if (!empty.equals(TextExtraction.useRegex(curLine, "(?<=(IP::))[^ ]+"))) {
					ipAddr = TextExtraction.useRegex(curLine, "(?<=(IP::))[^ ]+");
				} else if (!empty.equals(TextExtraction.useRegex(curLine, "(?<=(Port::))[^ ]+"))){
					port = Integer.parseInt(TextExtraction.useRegex(curLine, "(?<=(Port::))[^ ]+"));
				} else if (curLine.compareTo("# Website and root directory") == 0) {
					while(myReader.hasNextLine()) {
						curLine = myReader.nextLine();
						String[] webarr = curLine.split(" ", 2); 
 						websiteList.put(webarr[0], webarr[1]);
					}
				}
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static String host = "";
	public static void main(String[] args) {
		// Thread1 t1 = new Thread1("Thread-1");
		// Thread1 t2 = new Thread1("Thread-2");

		// Thread t2 = new Thread(new Thread2());
		// t1.start();
		// t2.start();
		// t1.setNama("NEW THREAD");

		try {
			setIpAndPort();
			System.out.println(ipAddr);
			System.out.println(port);
			InetAddress addr = InetAddress.getByName(ipAddr);
			ServerSocket server = new ServerSocket(port, 0, addr);
			System.out.println("Start accepting");
			// telnet localhost 6666
			int x = 0;
			while (x <= 10) {
				x++;
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
							if(!websiteList.get(host).equals("")) {						
//								rf.setRequestedFile(this.websiteList.get(host)+ "/" + rf.getRequestedFile());
								rf.setDir(websiteList.get(host) + "/");
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
						response = "HTTP/1.0 200 OK\r\n";
						if(keepAlive) {
							response += "Connection: Keep-Alive\r\n";						
							response += "Keep-Alive: timeout=50, max=1000\r\n";						
						}
						if(!( te.extractExt( rf.getRequestedFile() ).equals("html") )) {
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
//						response += "Date: "+ LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z"))+"\r\n"
//								+ "Last-Modified: "+ LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z"))+"\r\n"
//								+ "Server: Apache";
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
					//bos.close();
					//bis.close();
					System.out.println("Bos write successful.");
				} while (keepAlive);
				client.close();
//				Socket client = server.accept();
//				System.out.println("New client connected");
//
//				DataInputStream bis = new DataInputStream(client.getInputStream());
//				DataOutputStream bos = new DataOutputStream(client.getOutputStream());
//
//				ReadFile rf = new ReadFile();
//				int line = 1;
//				while (true) {
//					// String buf = new String(bis.readAllBytes());
//					@SuppressWarnings("deprecation")
//					String buf = bis.readLine(); // Alternative -> BufferedReader
//					if (buf.equals("")) {
//						break;
//					}
//					// Ambil alamat requestedFile
//					if (line == 1) {
//						String reqFile = "";
//						reqFile = extractReqFileDir(buf); // mungkin perlu di modify
//						if (reqFile.equals("")) {
//							rf.setRequestedFile("index.html");
//						} else {
//							rf.setRequestedFile(reqFile);
//						}
//					}
//					if(line == 2) {
//						host = extractHost(buf);
//					}
//					buf += " (from server)";
//					System.out.println("Received a message: " + buf);
//					line++;
//				}
//
//				rf.retrieveFile();
//				String response = "";
//				// cek rf
//				if (rf.isFileExist()) {
//					// Response message format
//					// HTTP/1.0 status_code msg_status_code
//					// Key: value
//					// Key: value
//					// Key: value
//					//
//					// CONTENT
//					response = "HTTP/1.0 200 OK\r\n";
//					if(!( extractExt( rf.getRequestedFile() ).equals("html") )) {
//						response += "Content-Type: application/octet-stream\r\n"
//								+ "Content-Disposition: attachment; filename=\""
//								+ rf.getRequestedFile() + "\"\r\n";						
//					} //else {						
//						response += "\r\n";
//						response += rf.getContent();
//					//}
//				} else {
//					if (rf.getRequestedFile().equals("index.html")) {
//						// show directory
//						ServerDirectory directory = new ServerDirectory();
//						directory.setFileSet(ServerDirectory.listFilesUsingDirectoryStream(System.getProperty("user.dir")));
//						response = "HTTP/1.0 302 Redirected to Server Directory\r\n";
//						response += "\r\n";
//						response += "<html><body><h1>File Directory</h1>"
//								+ directory.listDirectoryAsHTML(host)
//								+ "</body></html>";
//					} else {
//						// CONTENT
//						response = "HTTP/1.0 404 File does not exist\r\n";
//						response += "\r\n";
//						response += "<html><body><h1>We don't find what you are looking for. Sorry :(</h1></body></html>";
//					}
//				}
//				
//				System.out.println(response);
//				bos.write(response.getBytes());
//				if(true) {
//					client.close();					
//				}
//				x++;
			}
			server.close();
		} catch (IOException ex) {
			Logger.getLogger(StreamTest.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}