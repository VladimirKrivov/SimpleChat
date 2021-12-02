package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SimpleChatClientA {
    JFrame frame;
    JPanel panel;
    JTextField myField;
    JTextArea textArea;

    Socket sock;
    PrintWriter writer;
    BufferedReader reader;

    public static void main(String[] args) {
        SimpleChatClientA start = new SimpleChatClientA();
        start.go();
    }

    public void go() {
        frame = new JFrame("Примитивный чат");
        panel = new JPanel();
        myField = new JTextField(30);
        textArea = new JTextArea(20,40);


        JButton sendButton = new JButton("Отправить");
        sendButton.addActionListener(new SendMes());

        panel.add(myField);
        panel.add(sendButton);

        frame.setSize(550, 150);
        frame.setVisible(true);
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SetUpNetworking();

        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();
    }

    public void SetUpNetworking() {

        try {
            sock = new Socket("192.168.1.140", 5000);
            System.out.println("Соединение установленно");
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("Исходящий поток: ОК");

            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            System.out.println("Входящий поток: ОК");
        }catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Соединение не установленно");
        }

    }

    public class IncomingReader implements Runnable {

        @Override
        public void run() {
            String message;
            System.out.println("Запуск доп потока");
            try {
                while ((message = reader.readLine()) != null){
                    System.out.println("Входящий поток: " + message);

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public class SendMes implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            try {
                writer.println(myField.getText());
                writer.flush();
                System.out.println("Сообщение отправленно");
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Ошибка отправки");
            }
            myField.setText("");
            myField.requestFocus();
        }
    }
}
