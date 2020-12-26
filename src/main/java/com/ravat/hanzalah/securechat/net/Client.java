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
        outThread.start();
        inThread.start();
    }

    public void sendMessage(String message){
        System.out.println("Sending a Message!");
        Packet.Payload sendPayload = new Packet.Payload(new ChatPayload(message));
        setLastChatPayload(sendPayload);
        lastPayload = sendPayload;
        outQueue.add(Packet.serialiseObject(sendPayload));
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
            while (GlobalContext.getInstance().getStatus()) {
                try {
                    System.out.println("Trying to read these daym packets");
                    byte[] inboundMessage = new byte[Packet.MAX_PACKET_LENGTH];
                    int inboundMessageValue = dataInputStream.read(inboundMessage);
                    System.out.println("A message has been recieved of length: " + inboundMessageValue);
                    //Send an ACK payload
                    dataOutputStream.write(ACKPayload.ACK_BYTES);
                    if (inboundMessageValue > 0) {
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
                        byte[] toSend = outQueue.remove();
                        if (toSend != null) {
                            // We have to send some messages!
                            System.out.print("Sending...");
                            dataOutputStream.write(toSend, 0, toSend.length);
                            System.out.println("Message Sent Dud");
                            for (ChatListener.OutboundListener outListener :
                                    outListeners) {
                                System.out.println("Sending message sent broadcast");
                                outListener.onMessageSent();
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
            void onMessageRecieved();
        }
        interface OutboundListener extends ChatListener{
            void onMessageSent();
        }
    }
}
