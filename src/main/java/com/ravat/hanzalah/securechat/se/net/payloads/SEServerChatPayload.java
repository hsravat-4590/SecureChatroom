package com.ravat.hanzalah.securechat.se.net.payloads;

import com.ravat.hanzalah.securechat.net.server.ServerChatPayload;

import java.security.PublicKey;

public class SEServerChatPayload extends ServerChatPayload {

    private PublicKey publicKey;

    public SEServerChatPayload(MessageTypes messageType, String userName) {
        super(messageType, userName);
    }

    /**
     * Constructs a ServerChatPayload for a *NEW* User and takes their public key
     * @param userName
     * @param key
     */
    public SEServerChatPayload(String userName, PublicKey key){
        super(MessageTypes.NEW_USER,userName);
        this.publicKey = key;
    }

    public PublicKey getPublicKey(){return publicKey;}
}
