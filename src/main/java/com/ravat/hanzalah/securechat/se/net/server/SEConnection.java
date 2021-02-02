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
