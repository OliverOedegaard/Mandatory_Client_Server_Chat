package com.company;


import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler extends Thread {


    private String clientName = null;
    private Scanner clientInput = null;
    private PrintStream serverOutput = null;
    private final ClientHandler[] threads;
    private Socket clientSocket;
    private int maxClientCount;


    public ClientHandler(Socket clientSocket, ClientHandler[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientCount = threads.length;


    }

    public void run() {

        int maxClientCount = this.maxClientCount;
        ClientHandler[] threads = this.threads;

        try {

            clientInput = new Scanner(clientSocket.getInputStream());
            serverOutput = new PrintStream(clientSocket.getOutputStream());

            String username;
            int maxUsernameLength = 12;

            while (true) {

                serverOutput.println("Enter Your username: ");
                username = clientInput.nextLine().trim();

                if (username.length() <= maxUsernameLength && username.matches("^[a-zA-Z0-9_]+$")) {
                    break;
                }
                if (username.length() > maxUsernameLength && !username.matches("^[a-zA-Z0-9_]+$")) {
                    serverOutput.println("J_ER <<err_code1>>: <<err_Username must be max 12 chars long, only letters, digits, ‘-‘ and ‘_’ allowed");
                }

                if (username.length() <= maxUsernameLength && !username.matches("^[a-zA-Z0-9_]+$")) {
                    serverOutput.println("J_ER <<err_code2>>: <<err_username not accepted, only letters, digits, ‘-‘ and ‘_’ allowed>>");
                }

                if (username.length() > maxUsernameLength && username.matches("^[a-zA-Z0-9_-]+$")) {
                    serverOutput.println("J_ER <<err_code3>>: <<err_username to long, must be max 12 characters long>>");
                }

                if (username.equals(threads));
                    serverOutput.println("J_ER <<err_code4>>: <<err_username already exists>>");

            }
            serverOutput.println("J_OK");

            synchronized (this){
                for (int i = 0; i < maxClientCount; i++) {
                    if (threads[i] != null && threads[i] == this){
                        clientName = username;
                        break;
                    }

                }

                for (int i = 0; i < maxClientCount; i++) {
                    if (threads[i] != null && threads[i] != this) {
                        threads[i].serverOutput.println("<<A new user " + username
                                + " entered the chat room>>");


                    }
                }
            }

            while (true) {
                String userText = clientInput.nextLine();

                if (userText.startsWith("QUIT")) {
                    break;
                }
                if (userText.length() > 250) {
                    serverOutput.println("J_ER <<err_code4>>: <<err_text is to big, must be max 250 characters including spaces>>");

                } else {
                    synchronized (this) {
                        for (int i = 0; i < maxClientCount; i++) {
                            if (threads[i] != null && threads[i].clientName != null) {
                                threads[i].serverOutput.println(username
                                        + " : " + "<<" + userText + ">>");

                            }
                        }
                    }
                }

            }
            synchronized (this) {
                for (int i = 0; i < maxClientCount; i++) {
                    if (threads[i] != null && threads[i] != this
                            && threads[i].clientName != null) {
                        threads[i].serverOutput.println("<<The user " + username
                                + " is leaving the group>>");

                    }
                }
            }

            serverOutput.println("Bye " + username + "!");


            synchronized (this) {
                for (int i = 0; i < maxClientCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }

            


            //close the stream
            this.clientInput.close();
            this.serverOutput.close();
            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}