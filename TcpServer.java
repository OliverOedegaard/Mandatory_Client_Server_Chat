package com.company;



import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;


public class TcpServer {


    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;

    private static final int maxClientCount = 10;
    private static final ClientHandler[] threads = new ClientHandler[maxClientCount];

    public static void main(String[] args) {
        // write your code here
        int portnumber = 5000;

        if (args.length < 1){
            System.out.println("Portnumber: " + portnumber);
        } else {
            portnumber = Integer.valueOf(args[0]).intValue();
        }
        try {
            serverSocket = new ServerSocket(portnumber);

        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true){
            try {
                clientSocket = serverSocket.accept();

                int i=0;
                for (i = 0; i < maxClientCount; i++) {
                    if (threads[i] ==null){
                        (threads[i] = new ClientHandler(clientSocket, threads)).start();
                        break;

                    }

                }
                if (i == maxClientCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server too busy. Try later.");
                    os.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}