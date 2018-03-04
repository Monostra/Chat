package ua.com.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

/**
 * Created by Mykhailo on 04/03/2018.
 */
public class ChatClient extends JFrame implements Runnable {

    Socket socket;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    JTextArea textArea;
    JTextField textField;
    boolean isOn;

    public ChatClient(String title, Socket s, DataInputStream dis, DataOutputStream dos) {
        super(title);
        socket = s;
        inputStream = dis;
        outputStream = dos;

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(BorderLayout.CENTER, textArea = new JTextArea());
        textArea.setEditable(false);
        cp.add(BorderLayout.SOUTH, textField = new JTextField());

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    outputStream.writeUTF(textField.getText());
                    outputStream.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    isOn = false;
                }
                textField.setText("");
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                isOn = false;
                try {
                    outputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400,500);
        setVisible(true);
        textField.requestFocus();
        (new Thread(this)).start();
    }


    @Override
    public void run() {
        isOn = true;
        try {
            while (isOn) {
                String line = inputStream.readUTF();
                textArea.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            textField.setVisible(false);
            validate();
        }
    }

    public static void main(String[] args) throws IOException {
        String args0 = "localhost";
        String args1 = "8082";

        Socket socket = new Socket(args0, Integer.parseInt(args1));
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try {
            dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            new ChatClient("Chat " + args0 + ":" + args1, socket, dis, dos);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (dos != null) {
                    dos.close();
                }
            } catch (IOException ex2) {
                ex2.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException ex3) {
                ex3.printStackTrace();
            }

        }
    }
}
