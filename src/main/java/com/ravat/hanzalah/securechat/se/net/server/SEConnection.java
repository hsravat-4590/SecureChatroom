package com.ravat.hanzalah.securechat.se.net.server;

import com.ravat.hanzalah.securechat.net.server.Connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.security.PublicKey;

/**
 * Security Enchanced Connection Data Class. This class also hold the connection's public key
 */
public class SEConnection extends Connection {

    public final PublicKey publicKey;

    public SEConnection(Socket socket, PublicKey publicKey) throws IOException {
        super(socket);
        this.publicKey = publicKey;
    }

    public SEConnection(Socket socket, ObjectOutputStream outputStream, ObjectInputStream inputStream, PublicKey publicKey) throws SocketException {
        super(socket, outputStream, inputStream);
        this.publicKey = publicKey;
    }
}
