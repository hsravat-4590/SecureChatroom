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
    private Packet.Payload lastPayload;
    private Packet.Payload lastChatPayload;
    private ZonedDateTime lastMessageTime;
    private Queue<byte[]> inQueue;
    private Queue<byte[]> outQueue;
    private List<ChatListener.InboundListener> inListeners;
    private List<ChatListener.OutboundListener> outListeners;

    public Client(AddressInfo addressInfo) throws IOException {
        mSocket = new Socket(addressInfo.host,addressInfo.port);
        dataInputStream = new DataInputStream(mSocket.getInputStream());
        dataOutputStream = new DataOutputStream(mSocket.getOutputStream());
        inQueue = new LinkedList<>();
        outQueue = new LinkedList<>();
        inListeners = new LinkedList<>();
        outListeners = new LinkedList<>();
    }

    public void sendMessage(String message){
        Packet.Payload sendPayload = new Packet.Payload(new ChatPayload(message));
        outQueue.add(Packet.serialiseObject(sendPayload));
        setLastChatPayload(sendPayload);
        lastMessageTime = sendPayload.metaData.time;
    }
    private void setLastChatPayload(Packet.Payload payload){
        if(payload.payload instanceof ChatPayload){
            lastChatPayload = payload;
        }
    }

    public Packet.Payload getLastChat(){
        return lastChatPayload;
    }
    public Packet.Payload getLastPayload(){
        return lastPayload;
    }
    @Override
    public void run(){
        while (GlobalContext.getInstance().getStatus()) {
            try {
                inQueue.add(dataInputStream.readAllBytes());
                for (ChatListener.InboundListener inListener:
                     inListeners) {
                    inListener.onMessageRecieved();
                }
                if (outQueue.size() > 0) {
                    // We have to send some messages!
                    dataOutputStream.write(outQueue.remove());
                    for(ChatListener.OutboundListener outListener:
                    outListeners){
                        outListener.onMessageSent();
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
    public void addListener(ChatListener listener){
        if(listener instanceof ChatListener.InboundListener)
            inListeners.add((ChatListener.InboundListener) listener);
        if(listener instanceof ChatListener.OutboundListener)
            outListeners.add((ChatListener.OutboundListener) listener);
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
