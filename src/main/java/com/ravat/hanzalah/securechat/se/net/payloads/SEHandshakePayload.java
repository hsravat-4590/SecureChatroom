package com.ravat.hanzalah.securechat.se.net.payloads;

import com.ravat.hanzalah.securechat.net.ChatPayload;
import com.ravat.hanzalah.securechat.se.crypto.RSA;

import java.security.PublicKey;

/**
 * SE Handshake payload is derived from the standard ChatPayload but it also carries the User's Public Key along with the chatname
 */
public class SEHandshakePayload extends ChatPayload {
    private final PublicKey publicKey;
    public SEHandshakePayload(String chatName) {
        super(chatName);
        publicKey = RSA.getInstance().publicKey;
    }

    public PublicKey getPublicKey(){return publicKey;}
}
