package com.ravat.hanzalah.securechat.net;

public class ChatPayload implements Packet.DataPayload {

    public final String chatMessage;

    public ChatPayload(String chatMessage){
        this.chatMessage = chatMessage;
    }
}
