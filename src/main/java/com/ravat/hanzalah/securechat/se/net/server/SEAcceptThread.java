package com.ravat.hanzalah.securechat.se.net.server;

import com.ravat.hanzalah.securechat.net.server.AcceptThread;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;

/**
 * Runs on top of AcceptThread but with SSLServerSockets instead of unsecured sockets and establishes SSL Sockets for inbound connections
 */
public class SEAcceptThread extends AcceptThread {

    public SEAcceptThread(int listenPort) throws IOException {
        super(listenPort);
    }

    @Override
    protected void initialise() throws IOException{
        serverSocket = SSLServerSocketFactory.getDefault().createServerSocket(port);
    }
}
