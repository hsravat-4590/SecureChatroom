package com.ravat.hanzalah.securechat.net;

/**
 * Payload for Acknowledging that a packet has been received
 */
public class ACKPayload implements Packet.DataPayload {
    public final String ack;
    public ACKPayload() {
        ack = "acknowledged";
    }
    
    public static final byte[] ACK_BYTES = Packet.serialiseObject(new Packet.Payload(new ACKPayload()));
}
