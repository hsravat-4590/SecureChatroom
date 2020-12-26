package com.ravat.hanzalah.securechat.net.server;

import com.ravat.hanzalah.securechat.net.ACKPayload;
import com.ravat.hanzalah.securechat.net.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class ClientConnection extends Thread{
    private final Socket socket;
    private final Server serverInstance;
    private volatile Queue<PacketRecord> outQueue;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;
    private final Set<Integer> hashedMessages;
    private final String userName;


    public ClientConnection(String userName,Socket socket,Server server) throws IOException {
        super();
        this.userName = userName;
        this.socket = socket;
        this.serverInstance = server;
        outQueue = new LinkedList<>();
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        this.hashedMessages = new HashSet<>();
    }

    public synchronized void addToOutQueue(String author,byte[] outMessage){
        outQueue.add(new PacketRecord(author,outMessage));
    }
    @Override
    public void run(){
        while(serverInstance.isServerRunning()){
            // This thread will monitor the socket connection and read messages and send messages from the outQueue
            //Send first then recieve
            try {
                if(outQueue.size() > 0) {
                    PacketRecord out = outQueue.remove();
                    if(!out.getAuthor().equals(userName)){
                        byte[] outMsg = out.getPacket();
                        dataOutputStream.write(outMsg, 0, outMsg.length);
                        System.out.println("Sent The message to: " + userName);
                    }
                }
                byte[] in = new byte[Packet.MAX_PACKET_LENGTH];
                int inputValue = dataInputStream.read(in);
                if(inputValue > 0) {
                    System.out.println("Message Recieved of length: " + inputValue);
                    dataOutputStream.write(ACKPayload.ACK_BYTES);
                    hashedMessages.add(Arrays.hashCode(in));
                    serverInstance.addOutMessage(userName,in);
                } else if(inputValue == -1){
                    // The user has disconnected it seems
                    serverInstance.onClientDisconnected(userName);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

}
