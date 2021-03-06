/* 
 *  MIT License
 *  
 *  Copyright (c) 2021-2021 Hanzalah Ravat
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.ravat.hanzalah.securechat.se.net.server;

import com.ravat.hanzalah.securechat.net.ACKPayload;
import com.ravat.hanzalah.securechat.net.ChatPayload;
import com.ravat.hanzalah.securechat.net.Packet;
import com.ravat.hanzalah.securechat.net.server.*;
import com.ravat.hanzalah.securechat.se.net.payloads.SEHandshakePayload;
import com.ravat.hanzalah.securechat.se.net.payloads.SEServerHandshakePayload;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;

/**
 * Runs on top of AcceptThread but with SSLServerSockets instead of unsecured sockets and establishes SSL Sockets for inbound connections and prepares the connection for End-to-End Encryption
 */
public class SEAcceptThread extends AcceptThread {
    /**
     * Constructs an SEAcceptThread to listen on the specified port.
     * @param listenPort
     * @throws IOException
     */
    public SEAcceptThread(int listenPort) throws IOException {
        super(listenPort);
    }

    @Override
    protected void initialise() throws IOException{
        serverSocket = SSLServerSocketFactory.getDefault().createServerSocket(port);
        ((SSLServerSocket) serverSocket).setEnabledProtocols(new String[]{"SSLv3","TLSv1"});
    }
    
    @Override
    public void run(){
        while (ServerController.isServerRunning()) {
            try {
                System.out.println("Waiting for Clients...");
                Socket inSocket = serverSocket.accept();
                if(inSocket instanceof SSLSocket){
                    System.out.println("The Socket is an instance of an SSL Socket");
                    ((SSLSocket) inSocket).startHandshake();
                }
                System.out.println("Client Found: " + inSocket.getRemoteSocketAddress());
                ObjectOutputStream outputStream = new ObjectOutputStream(inSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(inSocket.getInputStream());
                System.out.print("Waiting for Handshake...");
                Packet.Payload handshakePacket = (Packet.Payload) inputStream.readObject();
                System.out.println("done");
                String usr = handshakePacket.metaData.author;
                //Extract Public Key
                PublicKey userPubKey = null;
                if(handshakePacket.payload instanceof SEHandshakePayload){
                    userPubKey =  ((SEHandshakePayload) handshakePacket.payload).getPublicKey();
                }
                System.out.println("User Connected: " + inSocket.getRemoteSocketAddress().toString() + " Username is: " + usr);
                // Assume that this client has sent their chatroom within the handshake packet
                if (handshakePacket.payload instanceof ChatPayload) {
                    String chatName = ((ChatPayload) handshakePacket.payload).getChatMessage();
                    SEConnection connection = new SEConnection(inSocket, outputStream, inputStream,userPubKey);
                    int addStatus = ChatRoom.createOrJoin(chatName, usr, connection);
                    connection.setMyStatus(addStatus);
                }
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
        }
    }
}
