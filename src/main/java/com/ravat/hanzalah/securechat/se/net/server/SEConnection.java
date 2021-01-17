package com.ravat.hanzalah.securechat.se.net.server;

import com.ravat.hanzalah.securechat.net.Packet;
import com.ravat.hanzalah.securechat.net.server.BlockingConnection;
import com.ravat.hanzalah.securechat.net.server.Connection;
import com.ravat.hanzalah.securechat.se.net.payloads.SEServerHandshakePayload;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.PublicKey;

/**
 * Security Enchanced Connection Data Class. This class also hold the connection's public key
 */
public class SEConnection extends Connection implements BlockingConnection.BlockableConnection {

    public final PublicKey publicKey;
    private volatile boolean handshakeSent = false;
    private volatile boolean statusSet = false;
    private volatile int mStatus;
    public SEConnection(Socket socket, PublicKey publicKey) throws IOException {
        super(socket);
        this.publicKey = publicKey;
    }

    public SEConnection(Socket socket, ObjectOutputStream outputStream, ObjectInputStream inputStream, PublicKey publicKey) throws SocketException {
        super(socket, outputStream, inputStream);
        this.publicKey = publicKey;
    }

    public void setMyStatus(int status){
        mStatus = status;
        statusSet = true;
    }
    @Override
    public void performBlockTask() {
        //System.out.println("Invoking Blocking Task..." + "Handshake Sent is: " + handshakeSent + " Status is set as: " + mStatus);
        if((!handshakeSent)&& statusSet){
            System.out.print("Writing the ACK packet to acknowledge the user can join...");
            try {
                super.readPayload();
            } catch (SocketTimeoutException exception) {
                exception.printStackTrace();
            } catch (SocketException socketException) {
                socketException.printStackTrace();
            }
            super.sendPacket(new Packet.Payload(new SEServerHandshakePayload(mStatus)));
            System.out.println("sent");
            handshakeSent = true;
        }
    }

    @Override
    public boolean blockingStillRequired() {
        return !handshakeSent;
    }
}
