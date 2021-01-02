package com.ravat.hanzalah.securechat.se.net;

import com.ravat.hanzalah.securechat.GlobalContext;
import com.ravat.hanzalah.securechat.net.AddressInfo;
import com.ravat.hanzalah.securechat.net.Client;
import com.ravat.hanzalah.securechat.se.ChatTrustStore;
import com.ravat.hanzalah.securechat.se.interfaces.AddCertificate;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SEClient extends Client {

    private final SSLSession mSSLSession;
    private final List<X509Certificate> x509Certificates;

    public SEClient(AddressInfo addressInfo, String chatName) throws IOException {
        super();
        super.mSocket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(addressInfo.host,addressInfo.port);
        super.objectOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
        super.objectInputStream = new ObjectInputStream(mSocket.getInputStream());
        mSSLSession = ((SSLSocket) mSocket).getSession();
        X509Certificate[] mCChain = (X509Certificate[]) mSSLSession.getPeerCertificates();
        List<X509Certificate> certificateList = new ArrayList<>(mCChain.length);
        certificateList.addAll(Arrays.asList(mCChain));
        x509Certificates = Collections.unmodifiableList(certificateList);
        if(ChatTrustStore.getInstance().validateServerCertificates(x509Certificates.toArray(new X509Certificate[x509Certificates.size()]),"TLS")){
            // This server is is gud
        } else{
            // Invoke the user and ask if they want to trust this user.
            AddCertificate.CertificateAcceptor mCertAcceptor = new AddCertificate.CertificateAcceptor() {
                @Override
                public void onAccept() {
                    
                }

                @Override
                public void onDecline() {

                }

                @Override
                public void onJustOnce() {

                }
            };
            AddCertificate.addCertDiag(mCertAcceptor);
        }

    }

}
