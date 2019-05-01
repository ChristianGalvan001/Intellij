package com.muc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerMain {
    //telnet localhost 8818
    public static void main(String[] args) {
        int port = 8818;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                System.out.println("About to accept client connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connetion from" + clientSocket);
                ServerWorker worker = new ServerWorker(clientSocket);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();


        }
    }




}
