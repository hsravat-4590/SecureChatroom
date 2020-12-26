package com.ravat.hanzalah.securechat.net.server;

import com.ravat.hanzalah.securechat.GlobalContext;
import com.ravat.hanzalah.securechat.net.ACKPayload;
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

    public synchronized void addOutMessage(String author,byte[] message){
        for(Map.Entry<String, ClientConnection> entry: connectionMap.entrySet()){
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
                DataInputStream inputStream = new DataInputStream(inSocket.getInputStream());
                System.out.print("Waiting for handshake...");
                byte[] packet = new byte[Packet.MAX_PACKET_LENGTH];
                int handshakePacket = inputStream.read(packet);
                System.out.print("done" + "Packet Value was: " + handshakePacket +"\n");
                String usr = Packet.deserializeObject(packet).metaData.author;
                ClientConnection newConnection = new ClientConnection(usr,inSocket,this);
                connectionMap.put(usr,newConnection);
                System.out.println("User Connected: " + inSocket.getRemoteSocketAddress().toString() + " Username is: " + usr);
                new DataOutputStream(inSocket.getOutputStream()).write(ACKPayload.ACK_BYTES); //Send an ACK Payload to acknowledge the connection
                // Send a server message indicating that a new user has joined
                byte[] toSend = Packet.serialiseObject(
                        new Packet.Payload(new ServerChatPayload(ServerChatPayload.MessageTypes.NEW_USER,usr))
                        );
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
