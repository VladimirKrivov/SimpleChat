package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Messenger {

    String nameClient = "DefaultName";
    JFrame frame;
    JPanel panel;
    JTextField textF;
    JButton sendButton;

    JTextField textName;
    JButton nameButton;

    JTextArea winArea;

    Socket sock;
    PrintWriter writer;
    BufferedReader reader;

    public static void main(String[] args) {
	// write your code here
        Messenger start = new Messenger();
        start.buildGui();
    }



    public void buildGui() {
        frame = new JFrame("Чат");
        panel = new JPanel();
        textF = new JTextField(40);
        textF.addKeyListener(new pressEnter());
        sendButton = new JButton("Отправить");
        sendButton.addActionListener(new SendMes());

        textName = new JTextField(40);
        nameButton = new JButton("Указать имя");
        nameButton.addActionListener(new SendName());



        winArea = new JTextArea(20, 60);
        winArea.setEditable(false);
        JScrollPane winScroller = new JScrollPane(winArea);
        winScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        winScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(textName);
        panel.add(nameButton);

        panel.add(winScroller);
        panel.add(textF);
        panel.add(sendButton);




        frame.setSize(750, 450);
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SetUpNetworking2();

        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();

    }

    public void SetUpNetworking2() {

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
                    if (message.length() > 0) {
                       // System.out.println("Входящий поток: " + message);
                        winArea.append(message + "\n");
                    }


                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    //Действие при нажатии Enter
    public  class pressEnter extends KeyAdapter {
        public void keyReleased(KeyEvent event) {
            if(event.getKeyCode() == KeyEvent.VK_ENTER ) {
                try {
                    if (textF.getText().equalsIgnoreCase("") || nameClient == "DefaultName"){

                        System.out.println("Ошибка отправки 2");
                    } else {
                        writer.println(nameClient + ": " + textF.getText());
                        writer.flush();
                        System.out.println("Сообщение отправленно");
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Ошибка отправки");
                }
                textF.setText("");
                textF.requestFocus();
            }
        }
    }

    //Действие кнопки "Указать имя"
    public class SendName implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (textName.getText().isEmpty()){
                System.out.println("Введите имя");
            } else {
                nameClient = textName.getText();
            }
        }
    }



    //Действие при нажатии кнопки "Отправить"
    public class SendMes implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            try {
                if (textF.getText().equalsIgnoreCase("") || nameClient == "DefaultName"){

                    System.out.println("Ошибка отправки 2");
                } else {
                    writer.println(textF.getText());
                    writer.flush();
                    System.out.println("Сообщение отправленно");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Ошибка отправки");
            }
            textF.setText("");
            textF.requestFocus();
        }
    }


    }


