package com.ravat.hanzalah.securechat.net.server;

import com.ravat.hanzalah.securechat.net.ACKPayload;
import com.ravat.hanzalah.securechat.net.Packet;
import com.ravat.hanzalah.securechat.se.net.server.SEConnection;
import com.robovm.debug.server.d.B;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A ChatRoom is a class which monitors all chatrooms the server is hosting and also provides the read and write threads for each chatroom. The chatroom also hosts the messageQueue and all connections in a chatroom
 */
public class ChatRoom{
    public static volatile Map<String,ChatRoom> chatRooms;
    private volatile Map<String,Connection> connections;
    private volatile Map<String, BlockingConnection.BlockableConnection> blockingConnections; // Connections of users that have just joined. They are initially put in a blocking list indicating that they (the connection) needs to perform some activity
    private volatile Queue<Packet.Payload> messageQueue;
    private final Connection admin;
    private static volatile ChatRoomDeriver mDeriver;

    /**
     * Initialises the HashMap which is used to hold all ChatRoom instances.
     */
    static{
        chatRooms = new HashMap<>();
    }
    private ChatRoom(String userName,Connection admin){
        connections = new ConcurrentHashMap<>();
        blockingConnections = new ConcurrentHashMap<>();
        messageQueue = new LinkedList<>();
        this.admin = admin;
        // Simple Check to decide if connection requries blocking or not
        if(admin instanceof BlockingConnection.BlockableConnection){
            blockingConnections.put(userName, (BlockingConnection.BlockableConnection) admin);
        } else {
            connections.put(userName, admin);
        }
        readThread.start();
        writeThread.start();
    }

    private class ConnectionWUserName{
        String userName;
        Connection connection;
        ConnectionWUserName(String name,Connection conn){
            userName = name;
            connection = conn;
        }
    }

    /**
     * Creates or adds a user to a chatroom
     * @param chatName
     * @param userName
     * @param joiner
     * @return -1 if the user could not be added to the chatroom, 0 if they were added successfully and 1 if they were added as an admin
     */
    public synchronized static int createOrJoin(String chatName,String userName,Connection joiner){
        if(mDeriver == null){
            mDeriver = new DefaultDeriver();
        }
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
        if(connections.containsKey(name) || blockingConnections.containsKey(name))
            return false;
        else {
            if(connection instanceof BlockingConnection.BlockableConnection){
                blockingConnections.put(name, (BlockingConnection.BlockableConnection) connection);
            } else {
                connections.put(name, connection);
            }
            addToMessageQueue(mDeriver.generateServerChatPayload(ServerChatPayload.MessageTypes.NEW_USER,connection,name));
            return true;
        }
    }

    private synchronized void removeUser(String name){
        connections.remove(name);
        addToMessageQueue(mDeriver.generateServerChatPayload(ServerChatPayload.MessageTypes.USER_LEFT,null,name));
    }

    private final Thread readThread = new Thread(() -> {
        while(ServerController.isServerRunning()){
            try {
                // Take a list of all block-complete connections to remove and put into connections after

                for (Map.Entry<String, Connection> entry : connections.entrySet()) {
                    try {
                        Packet.Payload inMessage = entry.getValue().readPayload();
                        if (inMessage == null) {
                            continue;
                        }
                        if ((!(inMessage.payload instanceof ACKPayload))) {
                            messageQueue.add(inMessage);
                        }
                    } catch (SocketTimeoutException exception) {
                        //System.out.println("Timeout when reading from user: " + entry.getKey());
                    } catch (SocketException exception) {
                        //The user has been disconnected
                        System.out.println("The user has been disconnected!!!");
                        removeUser(entry.getKey());
                    }
                }
            } catch (ConcurrentModificationException concurrentModificationException){
                // Do nothing and continue...
            }
        }
    });
    private final Thread writeThread = new Thread(() ->{
        while (ServerController.isServerRunning()){
            try {
                List<ConnectionWUserName> movableConnections = new LinkedList<>();
                for (Map.Entry<String, BlockingConnection.BlockableConnection> entry : blockingConnections.entrySet()) {
                    entry.getValue().performBlockTask();
                    if(!entry.getValue().blockingStillRequired()){
                        String user = entry.getKey();
                        Connection userConn = (Connection) entry.getValue();
                        System.out.println("Blocking is no longer required for: " + entry.getKey() + " Switching to connection mode");
                        movableConnections.add(new ConnectionWUserName(entry.getKey(), (Connection) entry.getValue()));
                    }
                }
                for (ConnectionWUserName connectionRecord: movableConnections) {
                    System.out.println("Status of removing connection is: " + blockingConnections.remove(connectionRecord.userName,connectionRecord.connection));
                    connections.put(connectionRecord.userName,connectionRecord.connection);
                }
                if(messageQueue.size() > 0) {
                    //System.out.println("Sending Messages");
                    //System.out.println("Number of elements in the queue: " + messageQueue.size() );
                    Packet.Payload outPayload = removeFromMessageQueue();
                    if(outPayload !=null) {
                        for (Map.Entry<String, Connection> entry : connections.entrySet()) {
                            //System.out.println("Sending Message to user: " + entry.getKey());
                            entry.getValue().sendPacket(outPayload);
                        }
                    }
                }
            } catch (ConcurrentModificationException concurrentModificationException){}
        }
    });

    protected synchronized void addToMessageQueue(Packet.Payload message){
        if(message.payload instanceof ServerChatPayload){
            //Add some priority logic here...
        }
        messageQueue.add(message);
    }

    protected synchronized Packet.Payload removeFromMessageQueue(){
        return messageQueue.poll();
    }

    /**
     * Set's the Deriver for the ChatRoom. This will only work if a deriver hasn't already been set OR if a chatroom hasn't been constructed yet.
     * @param deriver The dervier class
     * @return True if a the deriver is set, false if it could not be set.
     */
    public static boolean setDeriver(ChatRoomDeriver deriver){
        if(mDeriver != null){
            return false;
        } else if(mDeriver instanceof DefaultDeriver){
            return false;
        } else{
            mDeriver = deriver;
            return true;
        }
    }

    /**
     * The Default Deriver class. This class will be used if a custom deriver is not set.
     */
    static final class DefaultDeriver extends ChatRoomDeriver {
        public DefaultDeriver() throws ChatRoomDeriverSetException {
            super();
        }

        @Override
        public Packet.Payload generateServerChatPayload(ServerChatPayload.MessageTypes messageType, Connection user, String userName) {
            return new Packet.Payload(new ServerChatPayload(messageType,userName));
        }
    }

}
