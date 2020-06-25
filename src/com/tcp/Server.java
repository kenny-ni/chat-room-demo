package com.tcp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    static CopyOnWriteArrayList<Channel> users = new CopyOnWriteArrayList<>();
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8888);
        System.out.println("server started");
        while (true){
            Socket client = server.accept();
            System.out.println("a connection is established");
            new Thread(new Channel(client)).start();
        }
    }

    static class Channel implements Runnable{
        private Socket client;
        private DataInputStream dis;
        private DataOutputStream dos;
        private String userName;

        @Override
        public void run() {
            try{
                boolean isRunning = false;
                while(!isRunning) isRunning = sendAck();
                if(isRunning) publicMessage(userName + " joined", true);
                while (isRunning){
                    String msg = receiveMessage();
                    if(msg.startsWith("@")) privateMessage(msg);
                    else publicMessage(msg, false);
                }

            }catch (IOException e){

            }finally {
                try{
                    publicMessage(userName + " left", true);
                }catch (Exception e1){
                    e1.printStackTrace();
                }
                release();
                System.out.println("a connection is terminated");
            }
        }

        public Channel(Socket client) throws IOException {
            this.client = client;
            this.dis = new DataInputStream(client.getInputStream());
            this.dos = new DataOutputStream(client.getOutputStream());
            Server.users.add(this);
        }

        public String receiveMessage() throws IOException {
            String res = dis.readUTF();
//            System.out.println(res);
            return res;
        }

        public boolean sendAck() throws IOException {
            Set<String> names = new HashSet<>();
            names.add("kenny");
            names.add("jiageng");
            names.add("njg");
            boolean res = false;
            String info = receiveMessage();
            String[] kv = info.split("&");
            String userName = kv[0];
            String passWord = kv[1];
            if(names.contains(userName) && passWord.equals("960502")){
                dos.writeUTF("success");
                this.userName = userName;
                res = true;
            }
            else{
                dos.writeUTF("fail");
            }
            dos.flush();
            return res;
        }

        public void publicMessage(String msg, boolean isSys) throws IOException {
            if(!isSys) msg = this.userName + ": " + msg;
            for(Channel client: Server.users){
                if(client == this) continue;
                client.dos.writeUTF(msg);
            }
        }

        public void privateMessage(String msg) throws IOException {
            String[] temp = msg.substring(1).split(":");
            String targetName = temp[0];
            msg = temp[1];
            for (Channel client: Server.users){
                if (client.userName.equals(targetName)) {
                    client.dos.writeUTF(this.userName + "(private message): " + msg);
                    break;
                }
            }
        }

        public void release(){
            try{
                Server.users.remove(this);
                if(dis!=null) dis.close();
                if (dos != null) dos.close();
                if (client != null)client.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
