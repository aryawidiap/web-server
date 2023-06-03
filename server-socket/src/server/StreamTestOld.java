package server;


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

public class StreamTestOld {

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(6666);
            System.out.println("Start accepting");
            // telnet localhost 6666
            int x = 0;
            while (x <= 3) {
                Socket client = server.accept();
                System.out.println("New client connected");

                DataInputStream bis = new DataInputStream(client.getInputStream());
                DataOutputStream bos = new DataOutputStream(client.getOutputStream());

                while (true) {
                    //String buf = new String(bis.readAllBytes());
                    @SuppressWarnings("deprecation")
					String buf = bis.readLine();
                    if (buf.equals("done")) {
                        break;
                    }
                    buf += " (from server)";
                    System.out.println("Received a message: " + buf);
                    bos.write(buf.getBytes());
                }
                client.close();
                x++;
            }
            // server.close();
        } catch (IOException ex) {
            Logger.getLogger(StreamTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
