package com.ravat.hanzalah.securechat.se.net;

import com.ravat.hanzalah.securechat.net.*;
import com.ravat.hanzalah.securechat.net.server.ServerChatPayload;
import com.ravat.hanzalah.securechat.se.SESessionContext;
import com.ravat.hanzalah.securechat.se.SessionInfo;
import com.ravat.hanzalah.securechat.se.crypto.RSA;
import com.ravat.hanzalah.securechat.se.crypto.SEChatPayload;
import com.ravat.hanzalah.securechat.se.net.payloads.KeySharePayload;
import com.ravat.hanzalah.securechat.se.net.payloads.SEHandshakePayload;
import com.ravat.hanzalah.securechat.se.net.payloads.SEServerChatPayload;
import com.ravat.hanzalah.securechat.se.net.payloads.SEServerHandshakePayload;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;

public class SEClient extends Client {

    private final SSLSession mSSLSession;
    public SEClient(AddressInfo addressInfo, String chatName) throws IOException {
        super();
        super.mSocket = SSLSocketFactory.getDefault().createSocket(addressInfo.host,addressInfo.port);
        mSSLSession = ((SSLSocket) mSocket).getSession();
        super.objectOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
        super.objectInputStream = new ObjectInputStream(mSocket.getInputStream());
        System.out.print("Sending Handshake...");
        // SE Sessions will be sending out Public Keys in their Handshakes rather than a simple chatname.
        objectOutputStream.writeObject(new Packet.Payload(new SEHandshakePayload(chatName)));
        // Send Public key to the server
        try {
            Object replyObject = objectInputStream.readObject();
            Packet.Payload object = (Packet.Payload) replyObject;
            boolean aesSet = false;
            System.out.println("Got a: " + object.payload.getClass().getName());
            if (object.payload instanceof SEServerHandshakePayload) {
                System.out.println("Got a SEServerHandshake");
                SEServerHandshakePayload reply = (SEServerHandshakePayload) object.payload;
                if (reply.getStatus() == 1) {
                    System.out.println("Got a reply from the server... I am the Admin!");
                    SESessionContext.getInstance().createNewAESFacilitator();
                } else if (reply.getStatus() == -1) {
                    System.out.println("Got a reply from the server... I cannot join the chat!");
                    mSocket.close();
                } else {
                    System.out.println("Got a reply from the server... I need to get the shared key before I can chat!");
                    // Otherwise we need to
                    // wait for the AES Facilitator to be sent from the admin
                    objectOutputStream.writeObject(new Packet.Payload(new ACKPayload()));
                    waitForAdminAES: while (true) {
                        Packet.Payload inPayload = (Packet.Payload) objectInputStream.readObject();
                        objectOutputStream.writeObject(new Packet.Payload(new ACKPayload()));
                        if (inPayload.payload instanceof KeySharePayload) {
                            SESessionContext.getInstance().setAesFaclilitator((SecretKey) RSA.getInstance().decrypt(((KeySharePayload) inPayload.payload).getSecretKey()));
                            break;
                        }
                        Thread.sleep(100);
                    }
                }
                aesSet = true;
            } else{
                if(object.payload instanceof SEServerChatPayload){
                   // System.out.println(((SEServerChatPayload) object.payload).userName);
                } else{
                   // System.out.println("Got a: " + object.payload.getClass().getName());
                }
            }
        } catch(ClassNotFoundException | InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("done");
        SessionInfo.printSessionInfo(mSSLSession);
        super.startThreads();
    }

    @Override
    protected void handlePayload(Packet.DataPayload payload){
        if(payload instanceof SEServerChatPayload && SESessionContext.getInstance().isAdmin() ){
            System.out.println("I'm the Admin... Sending the shared key to the user");
            if(((SEServerChatPayload) payload).messageTypes.equals(ServerChatPayload.MessageTypes.NEW_USER)) {
                PublicKey publicKey = ((SEServerChatPayload) payload).getPublicKey();
                SealedObject sealedObject = RSA.getInstance().encrypt(SESessionContext.getInstance().getAesFaclilitator().getKey(),publicKey);
                sendMessage(new KeySharePayload(sealedObject));
                System.out.println("Shared Key added to send list");
            }
        }
    }

    @Override
    public void sendMessage(String message){
        try {
            Packet.Payload sendPayload = new Packet.Payload(new SEChatPayload(message));
            addToSendQueue(sendPayload);

        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

}
