package com.ucsd.jiageng;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class Server {
    public static void main(String[] args) throws Exception{
        System.out.println("server started");
        new MessageSendThread(5555, 6666).start();
        Thread receive = new MessageReceiveThread(9999);
        receive.setDaemon(true);
        receive.start();
    }
}
