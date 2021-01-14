package com.ravat.hanzalah.securechat.se.net.payloads;

import com.ravat.hanzalah.securechat.net.Packet;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

/**
 * Payload responsible for facilitating KeyShares
 */
public class KeySharePayload implements Packet.DataPayload {
    private final SealedObject secretKey;

    public KeySharePayload(SealedObject secretKey){
        this.secretKey = secretKey;
    }
    public SealedObject getSecretKey(){return secretKey;}
}
