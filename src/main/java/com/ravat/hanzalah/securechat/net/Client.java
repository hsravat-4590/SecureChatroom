package com.ravat.hanzalah.securechat.net;

import com.ravat.hanzalah.securechat.GlobalContext;
import soot.Pack;

import java.io.*;
import java.net.Socket;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Client implements Runnable{
    private final Socket mSocket;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;
    private volatile Packet.Payload lastPayload;
    private Packet.Payload lastChatPayload;
    private ZonedDateTime lastMessageTime;
    private volatile Queue<Packet.Payload> inQueue;
    private volatile Queue<byte[]> outQueue;
    private volatile List<ChatListener.InboundListener> inListeners;
    private volatile List<ChatListener.OutboundListener> outListeners;
    private final Thread mThread;

    public Client(AddressInfo addressInfo) throws IOException {
        mSocket = new Socket(addressInfo.host,addressInfo.port);
        dataInputStream = new DataInputStream(mSocket.getInputStream());
        dataOutputStream = new DataOutputStream(mSocket.getOutputStream());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.print("Sending Handshake...");
        dataOutputStream.write(Packet.serialiseObject(new Packet.Payload(ChatPayload.HANDSHAKE_PAYLOAD)));
        System.out.println("done");
        inQueue = new LinkedList<>();
        outQueue = new LinkedList<>();
        inListeners = new LinkedList<>();
        outListeners = new LinkedList<>();
        mThread = new Thread(this::run,"CLIENT_THREAD");
        mThread.start();
    }

    public void sendMessage(String message){
        System.out.println("Sending a Message!");
        Packet.Payload sendPayload = new Packet.Payload(new ChatPayload(message));
        setLastChatPayload(sendPayload);
        lastPayload = sendPayload;
        outQueue.add(Packet.serialiseObject(sendPayload));
        byte[] toSend = Packet.serialiseObject(sendPayload);
        try {
            dataOutputStream.write(toSend,0,toSend.length);
            for(ChatListener.OutboundListener outListener:
                    outListeners){
                System.out.println("Sending message sent broadcast");
                outListener.onMessageSent();
            }
            lastChatPayload = sendPayload;
        } catch (IOException exception) {
            exception.printStackTrace();
        }

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
    @Override
    public void run(){
        while (GlobalContext.getInstance().getStatus()) {
            try {
                if (outQueue.peek() != null) {
                    // We have to send some messages!
                    byte[] toSend = outQueue.remove();
                    System.out.print("Sending...");
                    dataOutputStream.write(toSend,0,toSend.length);
                    System.out.println("Message Sent Dud");
                    for(ChatListener.OutboundListener outListener:
                            outListeners){
                        System.out.println("Sending message sent broadcast");
                        outListener.onMessageSent();
                    }
                }
                byte[] inboundMessage = new byte[1000];
                int inboundMessageValue = dataInputStream.read(inboundMessage);
                if(inboundMessageValue > 0) {
                    Packet.Payload inPayload = Packet.deserializeObject(inboundMessage);
                    inQueue.add(inPayload);
                    System.out.println("Added a message to the inQueue");
                    for (ChatListener.InboundListener inListener :
                            inListeners) {
                        setLastChatPayload(inPayload);
                        inListener.onMessageRecieved();
                    }
                }
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void addListener(ChatListener listener){
        if(listener instanceof ChatListener.InboundListener)
            inListeners.add((ChatListener.InboundListener) listener);
        if(listener instanceof ChatListener.OutboundListener){
            System.out.println("Adding to outbound listeners");
            outListeners.add((ChatListener.OutboundListener) listener);}
    }
    public interface ChatListener{
        interface InboundListener extends ChatListener{
            void onMessageRecieved();
        }
        interface OutboundListener extends ChatListener{
            void onMessageSent();
        }
    }
}
