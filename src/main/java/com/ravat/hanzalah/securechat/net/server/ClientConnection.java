package com.ravat.hanzalah.securechat.net.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class ClientConnection extends Thread{
    private final Socket socket;
    private final Server serverInstance;
    private volatile Queue<byte[]> outQueue;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;
    private final Set<Integer> hashedMessages;

    public ClientConnection(Socket socket,Server server) throws IOException {
        super();
        this.socket = socket;
        this.serverInstance = server;
        outQueue = new LinkedList<>();
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        this.hashedMessages = new HashSet<>();
    }

    public synchronized void addToOutQueue(byte[] outMessage){
        outQueue.add(outMessage);
    }
    @Override
    public void run(){
        while(serverInstance.isServerRunning()){
            // This thread will monitor the socket connection and read messages and send messages from the outQueue
            //Send first then recieve
            try {
                if(outQueue.size() > 0) {
                    byte[] outMsg = outQueue.remove();
                    if (!hashedMessages.contains(Arrays.hashCode(outMsg)))
                        dataOutputStream.write(outMsg,0,outMsg.length);
                }
                byte[] in = new byte[1000];
                int inputValue = dataInputStream.read(in);
                if(inputValue > 0) {
                    System.out.println("Message Recieved of length: " + inputValue);
                    hashedMessages.add(Arrays.hashCode(in));
                    serverInstance.addOutMessage(in);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

}
