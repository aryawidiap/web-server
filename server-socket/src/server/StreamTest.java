package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamTest {
	
	private static String host = "";
	private static String useRegex(final String input, final String regex) {
		// Compile regular expression
		final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		// Match regex against input
		final Matcher matcher = pattern.matcher(input);
		// Use results...
		if (matcher.find()) {
			return matcher.group();
		}
		return "";
	}
	private static String extractReqFileDir(final String input) {
		return useRegex(input, "(?<=(GET /))[^ ]+");
	}
	private static String extractHost(final String input) {
		return useRegex(input, "(?<=(Host: ))[^ ]+");
	}
	private static String extractExt(final String input) {
		return useRegex(input, "(?<=(\\.))[^ ]+");
	}

	public static void main(String[] args) {
		// Thread1 t1 = new Thread1("Thread-1");
		// Thread1 t2 = new Thread1("Thread-2");

		// Thread t2 = new Thread(new Thread2());
		// t1.start();
		// t2.start();
		// t1.setNama("NEW THREAD");

		try {
			ServerSocket server = new ServerSocket(8080);
			System.out.println("Start accepting");
			// telnet localhost 6666
			int x = 0;
			while (x <= 10) {
				Socket client = server.accept();
				System.out.println("New client connected");

				DataInputStream bis = new DataInputStream(client.getInputStream());
				DataOutputStream bos = new DataOutputStream(client.getOutputStream());

				ReadFile rf = new ReadFile();
				int line = 1;
				while (true) {
					// String buf = new String(bis.readAllBytes());
					@SuppressWarnings("deprecation")
					String buf = bis.readLine(); // Alternative -> BufferedReader
					if (buf.equals("")) {
						break;
					}
					// Ambil alamat requestedFile
					if (line == 1) {
						String reqFile = "";
						reqFile = extractReqFileDir(buf); // mungkin perlu di modify
						if (reqFile.equals("")) {
							rf.setRequestedFile("index.html");
						} else {
							rf.setRequestedFile(reqFile);
						}
					}
					if(line == 2) {
						host = extractHost(buf);
					}
					buf += " (from server)";
					System.out.println("Received a message: " + buf);
					line++;
				}

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
					if(!( extractExt( rf.getRequestedFile() ).equals("html") )) {
						response += "Content-Type: application/octet-stream\r\n"
								+ "Content-Disposition: attachment; filename=\""
								+ rf.getRequestedFile() + "\"\r\n";						
					} //else {						
						response += "\r\n";
						response += rf.getContent();
					//}
				} else {
					if (rf.getRequestedFile().equals("index.html")) {
						// show directory
						ServerDirectory directory = new ServerDirectory();
						directory.setFileSet(ServerDirectory.listFilesUsingDirectoryStream(System.getProperty("user.dir")));
						response = "HTTP/1.0 404 File does not exist\r\n";
						response += "\r\n";
						response += "<html><body><h1>File Directory</h1>"
								+ directory.listDirectoryAsHTML(host)
								+ "</body></html>";
					} else {
						// CONTENT
						response = "HTTP/1.0 404 File does not exist\r\n";
						response += "\r\n";
						response += "<html><body><h1>We don't find what you are looking for. Sorry :(</h1></body></html>";
					}
				}
				
				System.out.println(response);
				bos.write(response.getBytes());
				client.close();
				x++;
			}
			// server.close();
		} catch (IOException ex) {
			Logger.getLogger(StreamTest.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}