package com.ravat.hanzalah.securechat.net;

public class ChatPayload implements Packet.DataPayload {

    public final String chatMessage;

    public static final ChatPayload HANDSHAKE_PAYLOAD = new ChatPayload("Handshake");

    public ChatPayload(String chatMessage){
        this.chatMessage = chatMessage;
    }
}
