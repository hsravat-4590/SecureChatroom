package com.ravat.hanzalah.securechat.net.server2;

import com.ravat.hanzalah.securechat.net.ACKPayload;
import com.ravat.hanzalah.securechat.net.ChatPayload;
import com.ravat.hanzalah.securechat.net.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AcceptThread extends Thread {

    private final ServerSocket serverSocket;
    public AcceptThread(int listenPort) throws IOException {
        super();
        serverSocket = new ServerSocket(listenPort);

    }

    @Override
    public void run(){
        while (ServerController.isServerRunning()) {
            try {
                System.out.println("Waiting for Clients...");
                Socket inSocket = serverSocket.accept();
                System.out.println("Client Found: " + inSocket.getRemoteSocketAddress());
                ObjectOutputStream outputStream = new ObjectOutputStream(inSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(inSocket.getInputStream());
                System.out.print("Waiting for Handshake...");
                Packet.Payload handshakePacket = (Packet.Payload) inputStream.readObject();
                System.out.println("done");
                String usr = handshakePacket.metaData.author;
                System.out.println("User Connected: " + inSocket.getRemoteSocketAddress().toString() + " Username is: " + usr);
                outputStream.writeObject(new Packet.Payload(new ACKPayload())); //Send an ACK Payload to acknowledge the connection
                // Assume that this client has sent their chatroom within the handshake packet
                if (handshakePacket.payload instanceof ChatPayload) {
                    String chatName = ((ChatPayload) handshakePacket.payload).chatMessage;
                    ChatRoom.createOrJoin(chatName, usr, new Connection(inSocket, outputStream, inputStream));
                }
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
        }
    }
}
