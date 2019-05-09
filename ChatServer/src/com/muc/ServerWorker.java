package com.muc;


import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.sql.CallableStatement;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class ServerWorker extends Thread{
    private final Socket clientSocket;
    private Server server;
    private String login = null;
    private OutputStream outputStream;
    private String onlineMsg;
    private HashSet<String> topicSet = new HashSet<>();

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
               if ("logoff".equalsIgnoreCase(cmd) || "logout".equalsIgnoreCase(cmd) ||"quit".equalsIgnoreCase(cmd)) {
                   handlelogoff();
                   break;
               } else if("login".equalsIgnoreCase(cmd)){
                handleLogin(outputStream, tokens);
               } else if ("msg".equalsIgnoreCase(cmd)) {
                   String[] tokensMsg = StringUtils.split(line, null, 3);
                   handleMessage(tokensMsg);
               }else if ("join".equalsIgnoreCase(cmd)) {
                   handleJoin(tokens);
               }else if ("leave".equalsIgnoreCase(cmd)){
                   handleLeave(tokens);
               } else{
                   String msg = "Unknown: " + cmd + "\n";
                   outputStream.write(msg.getBytes());
               }
           }
       }

        clientSocket.close();
    }

    private void handleLeave(String[] tokens) {

        if (tokens.length > 1){
            String topic = tokens[1];
            topicSet.add(topic);
        }
    }



    public boolean ismemberOfTopic(String topic){
        return topicSet.contains(topic);
    }
    private void handleJoin(String[] tokens) {
        if (tokens.length > 1){
            String topic = tokens[1];
            topicSet.add(topic);
        }

    }

    //format msg login body
    //format msg #topic body
    private void handleMessage(String[] tokens) throws IOException {
        String sendTo = tokens[1];
        String body = tokens[2];

        boolean isTopic = sendTo.charAt(0) == '#';

        List<ServerWorker> workerList = server.getWorkerList();
        for(ServerWorker worker : workerList){
            if(isTopic){
                if(worker.ismemberOfTopic(sendTo)){
                    String outMsg = "msg " + sendTo + ": " + login + " : " + body + "\n";
                    worker.send(outMsg);
                }
            }
            if (sendTo.equalsIgnoreCase(worker.getLogin())) {
                String outMsg = "msg " + login + " : " + body + "\n";
                worker.send(outMsg);
            }
        }

    }

    private void handlelogoff() throws IOException {
        server.removeWorker(this);
        List<ServerWorker> workerList = server.getWorkerList();
        String onlineMsg = "offline: " + login + "\n";
        for(ServerWorker worker : workerList){
            if(!login.equals(worker.getLogin())){
                worker.send(onlineMsg);

            }

        }
        clientSocket.close();


    }

    public String getLogin(){
        return login;
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];

            if ((login.equals("guest") && password.equals("guest")) || (login.equals("bob") && password.equals("bob"))) {
                String msg = "ok login" + "\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User logged in succesfully: " + login + "\n");


            List<ServerWorker> workerList = server.getWorkerList();

            //send current user and other lonline logins
                for(ServerWorker worker : workerList){
                    //this is going to make null go bye bye
                    if(!login.equals(worker.getLogin())){
                        if(worker.getLogin() != null) {

                            String msg2 = "online: " + worker.getLogin() + "\n";
                            send(msg2);
                        }
                    }

                }

                String onlineMsg = "online: " + login + "\n";

                //send other online users current users status
            for(ServerWorker worker : workerList){
                if(!login.equals(worker.getLogin())) {
                    worker.send(onlineMsg);
                }
            }
            } else {
                String msg = "error login" + "\n";
                outputStream.write(msg.getBytes());
            }
        }
    }

    private void send(String msg) throws IOException{
        if (login != null){
            outputStream.write(msg.getBytes());
        }


    }

}



/** Point 1- go search up StringUtils apache download the first file
    * extract to file\project structure
    * add module(the one you extracted)
    * accept hint
 **/






































































































