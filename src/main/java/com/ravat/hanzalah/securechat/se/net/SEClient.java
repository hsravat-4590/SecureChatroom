package com.ravat.hanzalah.securechat.se.net;

import com.ravat.hanzalah.securechat.GlobalContext;
import com.ravat.hanzalah.securechat.net.AddressInfo;
import com.ravat.hanzalah.securechat.net.Client;
import com.ravat.hanzalah.securechat.se.ChatTrustStore;
import com.ravat.hanzalah.securechat.se.interfaces.AddCertificate;
import com.ravat.hanzalah.securechat.se.trust.TrustStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.*;

public class SEClient extends Client {

    private final SSLSession mSSLSession;
  //  private final List<X509Certificate> x509Certificates;
    public SEClient(AddressInfo addressInfo, String chatName) throws IOException {
        super();
        System.setProperty("javax.net.ssl.trustStore", getClass().getResource("/keys/ClientKeyStore.jks").getFile());
        //System.setProperty("javax.net.ssl.keyStorePassword", KEYSTORE_PASSWORD);

        super.mSocket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(addressInfo.host,addressInfo.port);
        mSSLSession = ((SSLSocket) mSocket).getSession();
        ((SSLSocket) mSocket).startHandshake();
        super.objectOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
        super.objectInputStream = new ObjectInputStream(mSocket.getInputStream());
        super.startThreads();
    }

}
