package com.ucsd.jiageng;


public class Client {
    public static void main(String[] args) throws Exception{
        System.out.println("client started");
        new MessageSendThread(8888, 9999).start();
        Thread recv = new MessageReceiveThread(6666);
        recv.setDaemon(true);
        recv.start();
    }
}
