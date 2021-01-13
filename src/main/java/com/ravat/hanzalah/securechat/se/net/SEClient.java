package com.ravat.hanzalah.securechat.se.net;

import com.ravat.hanzalah.securechat.net.AddressInfo;
import com.ravat.hanzalah.securechat.net.ChatPayload;
import com.ravat.hanzalah.securechat.net.Client;
import com.ravat.hanzalah.securechat.net.Packet;
import com.ravat.hanzalah.securechat.se.crypto.SEChatPayload;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SEClient extends Client {

    private final SSLSession mSSLSession;
    public SEClient(AddressInfo addressInfo, String chatName) throws IOException {
        super();
        super.mSocket = SSLSocketFactory.getDefault().createSocket(addressInfo.host,addressInfo.port);
        mSSLSession = ((SSLSocket) mSocket).getSession();
        super.objectOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
        super.objectInputStream = new ObjectInputStream(mSocket.getInputStream());
        System.out.print("Sending Handshake...");
        objectOutputStream.writeObject(new Packet.Payload(new ChatPayload(chatName)));
        // Send Public key to the server
        System.out.println("done");
        super.startThreads();
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
