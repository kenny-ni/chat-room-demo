package com.tcp;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.Socket;

public class Client {
    static boolean isRunning = false;
    public static void main(String[] args) throws IOException {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        boolean logInSuccess = false;
        Socket server = new Socket("localhost", 8888);
        System.out.println("client start");
        SendMsg sender = new SendMsg(server, console);
        ReceiveMessage receiver = new ReceiveMessage(server);
        while (!logInSuccess) {
            System.out.print("User Name: ");
            String userName = console.readLine();
            System.out.print("pass word: ");
            String passWord = console.readLine();
            System.out.println("");
            sender.send(userName + '&' + passWord);
            String ack = receiver.receive();
            System.out.println("log in " + ack);
            if (ack.equals("success")) logInSuccess = true;
        }

        isRunning = true;
        new Thread(sender).start();
        new Thread(receiver).start();

    }
}

class SendMsg implements Runnable{
    private Socket server;
    private DataOutputStream dos;
    private BufferedReader console;

    @Override
    public void run(){
        try {
            while (Client.isRunning) {
                String msg = console.readLine();
                send(msg);
                if(msg.equals("exit")){
                    Client.isRunning = false;
                }
            }
            release();
        }
        catch (IOException e) {
            e.printStackTrace();
            try{
                release();
            }catch (IOException e1){
                e1.printStackTrace();
            }
        }

    }

    public SendMsg(Socket socket, BufferedReader bf) throws IOException {
        this.server = socket;
        this.dos = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));
        this.console = bf;
//        send(name);
    }

    public void send(String msg) throws IOException {
        dos.writeUTF(msg);
        dos.flush();
    }

    public void release() throws IOException {
        if (dos!=null) dos.close();
        if (server != null) server.close();
    }
}

class ReceiveMessage implements Runnable{
    private Socket server;
    private DataInputStream dis;

    @Override
    public void run(){
        try{
            while (Client.isRunning){
                String msg = receive();
                System.out.println(msg);
            }
        }catch (IOException e){
            try{
                release();
            }
            catch (IOException e1){
             e1.printStackTrace();
            }
        }
    }

    public ReceiveMessage(Socket socket) throws IOException {
        this.server = socket;
        this.dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    public String receive() throws IOException {
        return dis.readUTF();
    }

    public void release() throws IOException {
        if (dis != null) dis.close();
        if (server != null) server.close();
    }
}
