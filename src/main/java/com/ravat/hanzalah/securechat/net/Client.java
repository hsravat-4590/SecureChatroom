package com.ravat.hanzalah.securechat.net;

import com.ravat.hanzalah.securechat.GlobalContext;

import java.io.*;
import java.net.Socket;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Client{
    protected  Socket mSocket;
    protected  ObjectInputStream objectInputStream;
    protected  ObjectOutputStream objectOutputStream;
    private volatile Packet.Payload lastPayload;
    private Packet.Payload lastChatPayload;
    private ZonedDateTime lastMessageTime;
    private final Queue<Packet.Payload> inQueue;
    private final Queue<Packet.Payload> outQueue;
    private final List<ChatListener.InboundListener> inListeners;
    private final List<ChatListener.OutboundListener> outListeners;

    public Client(AddressInfo addressInfo,String chatName) throws IOException {
        mSocket = new Socket(addressInfo.host,addressInfo.port);
        objectOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
        objectInputStream = new ObjectInputStream(mSocket.getInputStream());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.print("Sending Handshake...");
        objectOutputStream.writeObject(new Packet.Payload(new ChatPayload(chatName)));
        System.out.println("done");
        inQueue = new LinkedList<>();
        outQueue = new LinkedList<>();
        inListeners = new LinkedList<>();
        outListeners = new LinkedList<>();
        outThread.start();
        inThread.start();
    }

    protected Client(){
        inQueue = new LinkedList<>();
        outQueue = new LinkedList<>();
        inListeners = new LinkedList<>();
        outListeners = new LinkedList<>();
        outThread.start();
        inThread.start();
    }

    public void sendMessage(String message){
        System.out.println("Sending a Message!");
        Packet.Payload sendPayload = new Packet.Payload(new ChatPayload(message));
        setLastChatPayload(sendPayload);
        lastPayload = sendPayload;
        outQueue.add(sendPayload);
        lastMessageTime = sendPayload.metaData.time;

    }
    private void setLastChatPayload(Packet.Payload payload) {
        if (payload.payload instanceof ChatPayload) {
            lastChatPayload = payload;
        }
    }

    public Packet.Payload getLastChat(){
        return lastChatPayload;
    }
    public synchronized Packet.Payload getLastPayload(){
        return lastPayload;
    }

    private final Thread inThread = new Thread() {
        @Override
        public void run() {
            int lastHash = -1;
            while (GlobalContext.getInstance().getStatus()) {
                try {
                    Packet.Payload inPayload = (Packet.Payload) objectInputStream.readObject();
                        System.out.println("A message has been recieved by" + GlobalContext.getInstance().mUserName);
                        //Send an ACK payload
                        objectOutputStream.writeObject(new Packet.Payload(new ACKPayload()));
                        if (inPayload != null) {
                            System.out.println("This message was sent by: " + inPayload.metaData.author);
                            inQueue.add(inPayload);
                            System.out.println("Added a message to the inQueue");
                            for (ChatListener.InboundListener inListener :
                                    inListeners) {
                                if(!(inPayload.metaData.author.equals(GlobalContext.getInstance().mUserName))) {
                                    setLastChatPayload(inPayload);
                                    inListener.onMessageRecieved(inPayload);
                                }
                            }
                        }
                    } catch (IOException | ClassNotFoundException exception) {
                        exception.printStackTrace();
                }
                // Add a buffer to improve performance and reduce risk of server overload
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Status is set to false... Quitting inThread");
        }

    };

    private final Thread outThread = new Thread() {
        @Override
        public void run() {
            while (GlobalContext.getInstance().getStatus()) {
                if(outQueue.size() > 0) {
                    try {
                        Packet.Payload toSend = outQueue.remove();
                        if (toSend != null) {
                            // We have to send some messages!
                            System.out.print("Sending...");
                            objectOutputStream.writeObject(toSend);
                            System.out.println("Message Sent Dud");
                            for (ChatListener.OutboundListener outListener :
                                    outListeners) {
                                System.out.println("Sending message sent broadcast");
                                outListener.onMessageSent(toSend);
                            }
                        }
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
                // Add a buffer to improve performance and reduce risk of server overload
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Status is set to false... Quitting inThread");
        }
    };

    public void addListener(ChatListener listener){
        if(listener instanceof ChatListener.InboundListener)
            inListeners.add((ChatListener.InboundListener) listener);
        if(listener instanceof ChatListener.OutboundListener){
            System.out.println("Adding to outbound listeners");
            outListeners.add((ChatListener.OutboundListener) listener);}
    }
    public interface ChatListener{
        interface InboundListener extends ChatListener{
            void onMessageRecieved(Packet.Payload payload);
        }
        interface OutboundListener extends ChatListener{
            void onMessageSent(Packet.Payload payload);
        }
    }
}
