package com.ravat.hanzalah.securechat.net.server;

import com.ravat.hanzalah.securechat.net.ACKPayload;
import com.ravat.hanzalah.securechat.net.Packet;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class ClientConnection extends Thread{
    private final Socket socket;
    private final Server serverInstance;
    private volatile Queue<PacketRecord> outQueue;
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;
    private final Set<Integer> hashedMessages;
    private final String userName;


    public ClientConnection(String userName,Socket socket,Server server,ObjectOutputStream outputStream, ObjectInputStream inputStream) throws IOException {
        super();
        this.userName = userName;
        this.socket = socket;
        this.serverInstance = server;
        outQueue = new LinkedList<>();
        this.objectOutputStream = outputStream;
        this.objectInputStream = inputStream;
        this.hashedMessages = new HashSet<>();
    }

    public synchronized void addToOutQueue(String author,Packet.Payload outMessage){
        outQueue.add(new PacketRecord(author,outMessage));
    }
    @Override
    public void run(){
        while(serverInstance.isServerRunning()){
            // This thread will monitor the socket connection and read messages and send messages from the outQueue
            //Send first then recieve
            try {
                if(outQueue.size() > 0) {
                    PacketRecord out = outQueue.remove();
                    if(!out.getAuthor().equals(userName)){
                        Packet.Payload outMsg = out.getPacket();
                        objectOutputStream.writeObject(outMsg);
                        System.out.println("Sent The message to: " + userName);
                    }
                }
                try {
                    byte[] in = new byte[Packet.MAX_PACKET_LENGTH];
                    Packet.Payload inPayload = (Packet.Payload) objectInputStream.readObject();
                    if (inPayload != null) {
                        if (inPayload.payload instanceof ACKPayload) {
                            System.out.println("ACK recieved");
                        }
                        else {
                            System.out.println("Message Recieved from " + inPayload.metaData.author);
                        }
                        objectOutputStream.writeObject(new ACKPayload());
                        serverInstance.addOutMessage(userName, inPayload);
                    } else{
                        serverInstance.onClientDisconnected(userName);
                    }
                } catch (SocketException socketException){
                    //The connection may have been interrupted
                    serverInstance.onClientDisconnected(userName);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

}
