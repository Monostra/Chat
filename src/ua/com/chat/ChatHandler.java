package ua.com.chat;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Mykhailo on 04/03/2018.
 */
public class ChatHandler extends Thread{
    Socket socket;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    boolean isOn;

    static List<ChatHandler> handlers = Collections.synchronizedList(new ArrayList<ChatHandler>());

    public ChatHandler(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    @Override
    public void run() {
        isOn = true;
        try {
            handlers.add(this);
            while (isOn) {
                String msg = inputStream.readUTF();
                broadcast(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void broadcast(String message) {
        synchronized (handlers) {
            Iterator<ChatHandler> iterator = handlers.iterator();
            while (iterator.hasNext()) {
                ChatHandler chatHandler = iterator.next();
                try {
                    synchronized (chatHandler.outputStream) {
                        chatHandler.outputStream.writeUTF(message);
                    }
                    chatHandler.outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    chatHandler.isOn = false;
                }
            }
        }
    }
}
