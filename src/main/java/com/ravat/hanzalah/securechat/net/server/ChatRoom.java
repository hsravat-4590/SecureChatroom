package com.ravat.hanzalah.securechat.net.server;

import com.ravat.hanzalah.securechat.net.ACKPayload;
import com.ravat.hanzalah.securechat.net.Packet;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;

public class ChatRoom {
    public static volatile Map<String,ChatRoom> chatRooms;
    private volatile Map<String,Connection> connections;
    private volatile PriorityQueue<Packet.Payload> messageQueue;
    private final Connection admin;

    static{
        chatRooms = new HashMap<>();
    }
    private ChatRoom(String userName,Connection admin){
        connections = new HashMap<>();
        messageQueue = new PriorityQueue<>();
        this.admin = admin;
        connections.put(userName, admin);
        readThread.start();
        writeThread.start();
    }

    /**
     * Creates or adds a user to a chatroom
     * @param chatName
     * @param userName
     * @param joiner
     * @return -1 if the user could not be added to the chatroom, 0 if they were added successfully and 1 if they were added as an admin
     */
    public synchronized static int createOrJoin(String chatName,String userName,Connection joiner){
        if(!(chatRooms.containsKey(chatName))){
            System.out.println("This Chatroom does not exist... Creating");
            chatRooms.put(chatName,new ChatRoom(userName,joiner));
            return 1;
        } else {
            ChatRoom toReturn = chatRooms.get(chatName);
            boolean added = toReturn.addUser(userName, joiner);
            if(added)
                return 0;
            else
                return -1;
        }
    }

    private synchronized boolean addUser(String name,Connection connection){
        if(connections.containsKey(name))
            return false;
        else {
            connections.put(name, connection);
            addToMessageQueue(new Packet.Payload(new ServerChatPayload(ServerChatPayload.MessageTypes.NEW_USER, name)));
            return true;
        }
    }

    private synchronized void removeUser(String name){
        connections.remove(name);
        addToMessageQueue(new Packet.Payload(new ServerChatPayload(ServerChatPayload.MessageTypes.USER_LEFT,name)));
    }

    private final Thread readThread = new Thread(() -> {
        while(ServerController.isServerRunning()){
            //System.out.println("Reading message...");
            for(Map.Entry<String,Connection> entry: connections.entrySet()) {
                try {
                    Packet.Payload inMessage = entry.getValue().readPayload();
                    if (inMessage == null) {
                        continue;
                    }
                    if ((!(inMessage.payload instanceof ACKPayload))) {
                        addToMessageQueue(inMessage);
                    }
                } catch(SocketTimeoutException exception){
                    continue;
                } catch(SocketException exception){
                    //The user has been disconnected
                    System.out.println("The user has been disconnected!!!");
                    removeUser(entry.getKey());
                }
            }
        }
    });
    private final Thread writeThread = new Thread(() ->{
        while (ServerController.isServerRunning()){
            if(messageQueue.size() > 0) {
                //System.out.println("Sending Messages");
                //System.out.println("Number of elements in the queue: " + messageQueue.size() );
                Packet.Payload outPayload = removeFromMessageQueue();
                for (Map.Entry<String, Connection> entry : connections.entrySet()) {
                    //System.out.println("Sending Message to user: " + entry.getKey());
                    entry.getValue().sendPacket(outPayload);
                }
            }
        }
    });

    protected synchronized void addToMessageQueue(Packet.Payload message){
        if(message.payload instanceof ServerChatPayload){
            //Add some priority logic here...
        }
        messageQueue.add(message);
    }

    protected synchronized Packet.Payload removeFromMessageQueue(){
        return messageQueue.remove();
    }
}
