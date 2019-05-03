package com.muc;


import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ServerWorker extends Thread{
    private final Socket clientSocket;

    public ServerWorker(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    public void run(){
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


       BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream));
       String line;
       while( (line = reader.readLine()) != null) {
           // Had to download appache refer to point 1
           String[] tokens = StringUtils.split(line);
           if(tokens != null && tokens.length > 0){
               String cmd = tokens[0];
               if ("quit".equalsIgnoreCase(line)) {
                   break;
               } else {
                String msg = "unknown " + cmd + "\n";
                outputStream.write(msg.getBytes());
               }
               String msg = " You typed: " + line;
               outputStream.write(msg.getBytes());
           }
       }
        clientSocket.close();
    }

}

/** Point 1- go search up StringUtils apache download the first file
    * extract to file\project structure
    * add module(the one you extracted)
    * accept hint
 **/

