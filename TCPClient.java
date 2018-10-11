package com.company;

import javax.management.remote.rmi.RMIConnector;
import javax.xml.soap.SOAPConnectionFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TCPClient implements Runnable {

    private static Socket clientSocket = null;
    private static Scanner clientInput = null;
    private static PrintStream serverOutput = null;
    private static BufferedReader inputLine = null;
    private static boolean closed = false;

    private static int portNumber = 5000;
    //private static String host = "localhost";
    private static String host = "192.168.0.119";

    public static void main(String[] args) {

    if (args.length < 2) {
        System.out.println("Host: " + host + " - Port Number " + portNumber);

    }else {
        host = args[0];
        portNumber = Integer.valueOf(args[1]).intValue();
    }

    try {
        clientSocket = new Socket(host, portNumber);
        inputLine = new BufferedReader(new InputStreamReader(System.in));
        serverOutput = new PrintStream(clientSocket.getOutputStream());
        clientInput = new Scanner(clientSocket.getInputStream());

    } catch (UnknownHostException e) {
        System.err.println("J_ER <<err_code6>> : <<err_unknown host>>");
    } catch (IOException e) {
        e.printStackTrace();
    }



    if (clientSocket != null && serverOutput != null && clientInput != null) {
        try {
            new Thread(new TCPClient()).start();

            while (!closed) {
                serverOutput.println(inputLine.readLine().trim());
            }


            serverOutput.close();
            clientInput.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("IOExeption: " + e);
        }
    }

    }

    @Override
    public void run() {

        String responseLine;
        while ((responseLine = clientInput.nextLine()) != null) {
            System.out.println(responseLine);
            if (responseLine.indexOf("Goodbye") != -1)
                break;
        }

        closed = true;

    }
}
