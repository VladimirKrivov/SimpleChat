package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class PrimitiveServer {
    ArrayList clientOutputStream;
    JFrame frame;
    JPanel panel;
    JTextArea textArea;

    public static void main(String[] args) {
        new PrimitiveServer().go();
    }

    public void go() {

        frame = new JFrame("Сервер чат");
        panel = new JPanel();
        textArea = new JTextArea(20,40);
        textArea.setEditable(false);
        JScrollPane winScroller = new JScrollPane(textArea);
        winScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        winScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);


        JButton startButton = new JButton("Включить сервер");
        startButton.addActionListener(new startServer());
        JButton offButton = new JButton("Отключить сервер");

        panel.add(textArea);
//        panel.add(startButton);
//        panel.add(offButton);

        frame.setSize(550, 440);
        frame.setVisible(true);
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        clientOutputStream = new ArrayList();
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStream.add(writer);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                textArea.append("Есть подключение" + "\n");
                System.out.println("Есть подключение");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;

        public ClientHandler(Socket clientSocket) {
            try {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    textArea.append(message + "\n");
                    System.out.println("Сообщение: " + message);
                    tellEveryone(message);
                }
            } catch (Exception ex) {ex.printStackTrace();}
        }
    }

    public void tellEveryone(String message) {
        Iterator it = clientOutputStream.iterator();
        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            } catch (Exception ex) {ex.printStackTrace();}
        }
    }

    //Запуск сервера
    public class startServer implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

}
