package com.ravat.hanzalah.securechat.net.server;

import com.ravat.hanzalah.securechat.GlobalContext;
import com.ravat.hanzalah.securechat.net.ACKPayload;
import com.ravat.hanzalah.securechat.net.Client;
import com.ravat.hanzalah.securechat.net.Packet;
import soot.Pack;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server implements Runnable{
    private final int port;
    private volatile Map<String,ClientConnection> connectionMap;
    private final ServerSocket serverSocket;
    private volatile Queue<PacketRecord> outQueue;
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

    public synchronized void addOutMessage(String author, Packet.Payload message){
        for(Map.Entry<String, ClientConnection> entry: connectionMap.entrySet()){
            System.out.println("Writing to MSG Queue of: " + entry.getKey());
            entry.getValue().addToOutQueue(author,message);
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
                System.out.println("Waiting for Clients...");
                Socket inSocket = serverSocket.accept();
                System.out.println("Client Found: "+ inSocket.getRemoteSocketAddress());
                ObjectOutputStream outputStream = new ObjectOutputStream(inSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(inSocket.getInputStream());
                System.out.print("Waiting for handshake...");
                Packet.Payload handshakePacket = (Packet.Payload) inputStream.readObject();
                System.out.println("done");
                String usr = handshakePacket.metaData.author;
                System.out.println("User Connected: " + inSocket.getRemoteSocketAddress().toString() + " Username is: " + usr);
                outputStream.writeObject(new Packet.Payload(new ACKPayload())); //Send an ACK Payload to acknowledge the connection
                // Send a server message indicating that a new user has joined
                ClientConnection newConnection = new ClientConnection(usr,inSocket,this,outputStream,inputStream);
                connectionMap.put(usr,newConnection);
                Packet.Payload toSend = new Packet.Payload(new ServerChatPayload(ServerChatPayload.MessageTypes.NEW_USER,usr));
                System.out.println("Sending out Message to All Users indicating that a new user has joined the Chat");
                addOutMessage("SERVER",toSend);
                newConnection.start();
            } catch (IOException | ClassNotFoundException ex){
                ex.printStackTrace();
            }
        }
    }

    public synchronized void onClientDisconnected(String client){
        connectionMap.remove(client);
    }
}
