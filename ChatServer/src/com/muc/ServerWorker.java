package com.muc;


import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.sql.CallableStatement;
import java.util.Date;
import java.util.List;

public class ServerWorker extends Thread{
    private final Socket clientSocket;
    private Server server;
    private String login = null;
    private OutputStream outputStream;
    private String onlineMsg;

    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    public void run(){
        try {
            clientHandle();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clientHandle() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();


       BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream));
       String line;
       while( (line = reader.readLine()) != null) {
           // Had to download appache refer to point 1
           String[] tokens = StringUtils.split(line);
           if(tokens != null && tokens.length > 0){
               String cmd = tokens[0];
               if ("quit".equalsIgnoreCase(line)) {
                   break;
               } else if("login".equalsIgnoreCase(cmd)){
                handleLogin(outputStream, tokens);
               }else {
                   String msg = "Unknown  " + cmd + "\n";
                   outputStream.write(msg.getBytes());
               }
           }
       }
        clientSocket.close();
    }

    public String getLogin(){
        return login;
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3){
            String login = tokens[1];
            String password = tokens[2];

            if ((login.equals("guest") && password.equals("guest")) || (login.equals("jim") && password.equals("jim"))) {
                String msg = "ok login";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User logged in succesfully: " + login);
            String onlineMsg = "online " + login + "\n";
            List<ServerWorker> workerList = server.getWorkerList();
            for(ServerWorker worker : workerList){
                worker.send(onlineMsg);
            }
            } else {
                String msg = "error login";
                outputStream.write(msg.getBytes());
            }
        }
    }

    private void send(String msg) throws IOException{
        outputStream.write(msg.getBytes());

    }

}



/** Point 1- go search up StringUtils apache download the first file
    * extract to file\project structure
    * add module(the one you extracted)
    * accept hint
 **/

