/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication;

import java.io.*;
import java.awt.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;

/**
 *
 * @author test
 */
public class Server extends JFrame {

    private JTextField field;
    private JTextArea fieldArea;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private ServerSocket sock;
    private Socket connection;

    public Server() {
        super("Instant Messenger");
        field = new JTextField();
        field.setEditable(false);
        
        field.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendIt(e.getActionCommand());
                field.setText("");
            }

        });
        add(field, BorderLayout.NORTH);
        fieldArea = new JTextArea();
        fieldArea.setEditable(false);
        add(new JScrollPane(fieldArea));
        setSize(500, 500);
        setVisible(true);

    }

    public void startRunning() {
        try {
            sock = new ServerSocket(6789, 100);
            while (true) {
                try {
                    waitForConnection();
                    setUpStreams();
                    whileChatting();
                } catch (EOFException exe) {
                    showIt("Server Closed");
                } finally {
                    closeIt();
                }
            }
        } catch (IOException exeption) {
            System.err.println("It says it was an error");
        }
    }

    private void waitForConnection() throws IOException {
        showIt("Waiting for connection.....");
        connection = sock.accept();
        showIt("Now connected to " + connection.getInetAddress().getHostName());
    }

    private void setUpStreams() throws IOException {
        outStream = new ObjectOutputStream(connection.getOutputStream());
        outStream.flush();
        inStream = new ObjectInputStream(connection.getInputStream());
        showIt("Streams are set");
    }

    private void whileChatting() throws IOException {
        String words = "You are connected";
        sendIt(words);
        ableToType(true);
        do {
            try {
                words = (String) inStream.readObject();
                showIt(words);
            } catch (ClassNotFoundException exe) {
                showIt("Message couldn't send");
            }
        } while (!words.equals("CLINT-END"));
    }

    private void closeIt() {
        showIt("Closing channels");
        ableToType(false);
        try {
            outStream.close();
            inStream.close();
            connection.close();
        } catch (IOException exe) {

        }
    }

    private void sendIt(String mess) {
        try {
            outStream.writeObject("SERVER- " + mess);
        } catch (IOException exe) {
            fieldArea.append("\n ERROR: can't send message");
        }

    }

    private void showIt(final String it) {
        SwingUtilities.invokeLater(
                new Runnable() {
            @Override
            public void run() {
             fieldArea.append(it);
            }
        }
        );

    }
    private void ableToType(final boolean condition){
        SwingUtilities.invokeLater(
                new Runnable() {
            @Override
            public void run() {
             field.setEditable(condition);
            }
        }
        );
    }
}
