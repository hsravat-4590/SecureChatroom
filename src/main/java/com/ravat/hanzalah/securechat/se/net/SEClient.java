package com.ravat.hanzalah.securechat.se.net;

import com.ravat.hanzalah.securechat.net.AddressInfo;
import com.ravat.hanzalah.securechat.net.ChatPayload;
import com.ravat.hanzalah.securechat.net.Client;
import com.ravat.hanzalah.securechat.net.Packet;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SEClient extends Client {

    private final SSLSession mSSLSession;
  //  private final List<X509Certificate> x509Certificates;
    public SEClient(AddressInfo addressInfo, String chatName) throws IOException {
        super();
        //System.setProperty("javax.net.ssl.keyStorePassword", KEYSTORE_PASSWORD);
        super.mSocket = SSLSocketFactory.getDefault().createSocket(addressInfo.host,addressInfo.port);
        mSSLSession = ((SSLSocket) mSocket).getSession();
        //((SSLSocket) mSocket).startHandshake();
        super.objectOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
        super.objectInputStream = new ObjectInputStream(mSocket.getInputStream());
        System.out.print("Sending Handshake...");
        objectOutputStream.writeObject(new Packet.Payload(new ChatPayload(chatName)));
        System.out.println("done");
        super.startThreads();
    }

}
