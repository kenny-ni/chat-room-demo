package com.ucsd.jiageng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class MessageSendThread extends Thread {
    private int DestPort;
    private DatagramSocket socket;

    public MessageSendThread(int srcPort, int destPort) throws SocketException {
        this.DestPort = destPort;
        this.socket = new DatagramSocket(srcPort);
    }

    private void sendMessage(String msg) throws IOException {
        byte[] data = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(data, 0, data.length, new InetSocketAddress("localhost", DestPort));
        this.socket.send(packet);
    }
    @Override
    public void run(){
        System.out.println("sender initiated");
        try{
            BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
            while (true){
                String msg = bf.readLine();
                sendMessage(msg);
                if (msg.equals("bye")) break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        this.socket.close();
        System.out.println("sender closed");
    }
}
