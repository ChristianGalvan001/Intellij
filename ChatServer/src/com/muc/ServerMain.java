package com.muc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerMain {
    //telnet localhost 8818
    public static void main(String[] args) {

        int port = 8818;
        Server server = new Server(port);
        server.start();
    }




}
