package ua.com.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Mykhailo on 04/03/2018.
 */
public class ChatServer {
    public ChatServer(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Accepted from " + socket.getInetAddress());
                ChatHandler handler = new ChatHandler(socket);
                handler.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            serverSocket.close();
        }
    }
    public static void main(String[] args) {
        try {
            String args0 = "8082";
            new ChatServer(Integer.parseInt(args0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
