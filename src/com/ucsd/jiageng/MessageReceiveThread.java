package com.ucsd.jiageng;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class MessageReceiveThread extends Thread {
    private DatagramSocket socket;
    private byte[] buffer = new byte[1024*60];

    public MessageReceiveThread(int srcPort) throws SocketException {
        this.socket = new DatagramSocket(srcPort);
    }

    public String receiveMessage(DatagramPacket packet) throws IOException {
            socket.receive(packet);
            int len = packet.getLength();
            String msg = new String(packet.getData(), 0, len);
            return msg;
    }

    @Override
    public void run(){
        try{
            System.out.println("receive thread initialed");
            DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
            while (true){
                String msg = receiveMessage(packet);
                System.out.println(msg);
                if (msg.equals("bye")) break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("receiver closed");
        socket.close();
    }
}
