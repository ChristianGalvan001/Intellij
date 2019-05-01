package com.muc;


import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

public class ServerWorker {
    private final Socket clientSocket;

    public ServerWorker(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    void run(){
        try {
            handleClientSocket();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();
        clientSocket.close();

        OutputStream outputStream = clientSocket.getOutputStream();
        //outputStream.write("Christian Galvan is here.\n".getBytes());
        for(int i=0; i < 10; i++){
            outputStream.write(("Time now is " + new Date() + "\n").getBytes());
            Thread.sleep(1000);
        }
        clientSocket.close();
    }

}
