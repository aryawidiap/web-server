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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StreamTest {
	
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
				ClientThread client = new ClientThread(server, websiteList);
				client.start();
			}
			server.close();
		} catch (IOException ex) {
			Logger.getLogger(StreamTest.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}