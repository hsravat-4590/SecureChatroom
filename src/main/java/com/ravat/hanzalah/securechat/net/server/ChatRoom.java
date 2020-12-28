package com.ravat.hanzalah.securechat.net.server;

import com.ravat.hanzalah.securechat.net.ACKPayload;
import com.ravat.hanzalah.securechat.net.Packet;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ChatRoom {
    public static volatile Map<String,ChatRoom> chatRooms;
    private volatile Map<String,Connection> connections;
    private volatile Queue<Packet.Payload> messageQueue;
    private final Connection admin;

    static{
        chatRooms = new HashMap<>();
    }
    private ChatRoom(String userName,Connection admin){
        connections = new HashMap<>();
        messageQueue = new LinkedList<>();
        this.admin = admin;
        connections.put(userName, admin);
        readThread.start();
        writeThread.start();
    }

    public synchronized static ChatRoom createOrJoin(String chatName,String userName,Connection joiner){
        if(!(chatRooms.containsKey(chatName))){
            System.out.println("This Chatroom does not exist... Creating");
            chatRooms.put(chatName,new ChatRoom(userName,joiner));
            return chatRooms.get(chatName);
        } else {
            ChatRoom toReturn = chatRooms.get(chatName);
            toReturn.addUser(userName, joiner);
            return toReturn;
        }
    }

    private synchronized void addUser(String name,Connection connection){
        connections.put(name,connection);
        messageQueue.add(new Packet.Payload(new ServerChatPayload(ServerChatPayload.MessageTypes.NEW_USER,name)));
    }

    private synchronized void removeUser(String name){
        connections.remove(name);
        messageQueue.add(new Packet.Payload(new ServerChatPayload(ServerChatPayload.MessageTypes.USER_LEFT,name)));
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
                        messageQueue.add(inMessage);
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
                Packet.Payload outPayload = messageQueue.remove();
                for (Map.Entry<String, Connection> entry : connections.entrySet()) {
                    //System.out.println("Sending Message to user: " + entry.getKey());
                    entry.getValue().sendPacket(outPayload);
                }
            }
        }
    });

}
