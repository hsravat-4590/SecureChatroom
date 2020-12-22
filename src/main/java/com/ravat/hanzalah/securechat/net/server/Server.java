package com.ravat.hanzalah.securechat.net.server;

import com.ravat.hanzalah.securechat.GlobalContext;
import com.ravat.hanzalah.securechat.net.Client;
import com.ravat.hanzalah.securechat.net.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server implements Runnable{
    private final int port;
    private Map<String,ClientConnection> connectionMap;
    private final ServerSocket serverSocket;
    private volatile Queue<byte[]> outQueue;
    private volatile boolean isRunning;
    private Thread acceptThread;
    private int connections;

    public Server(int listenPort) throws IOException{
        port = listenPort;
        connectionMap = new HashMap<>();
        serverSocket = new ServerSocket(port);
        outQueue = new LinkedList<>();
        acceptThread = new Thread(this::run,"SERVER_ACCEPT_THREAD");
        isRunning = false;
        connections = 0;

    }

    public synchronized boolean isServerRunning(){return isRunning;}

    public synchronized void addOutMessage(byte[] message){
        for(Map.Entry<String, ClientConnection> entry: connectionMap.entrySet()){
            entry.getValue().addToOutQueue(message);
        }
    }
    public void startServer(){
        isRunning = true;
        acceptThread.start();
    }
    @Override
    public void run(){
        while (isRunning){
            try{
                Socket inSocket = serverSocket.accept();
                DataInputStream inputStream = new DataInputStream(inSocket.getInputStream());
                byte[] handshakePacket = inputStream.readAllBytes();
                ClientConnection newConnection = new ClientConnection(inSocket,this);
                String usr = Packet.deserializeObject(handshakePacket).metaData.author;
                connectionMap.put(usr,newConnection);
                // Send a server message indicating that a new user has joined
                byte[] toSend = Packet.serialiseObject(
                        new Packet.Payload(new ServerChatPayload(ServerChatPayload.MessageTypes.NEW_USER,usr))
                        );
                newConnection.start();
            } catch (IOException | ClassNotFoundException ex){
                ex.printStackTrace();
            }
        }
    }

}
