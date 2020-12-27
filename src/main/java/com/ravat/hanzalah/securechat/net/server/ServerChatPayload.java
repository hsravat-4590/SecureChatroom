package com.ravat.hanzalah.securechat.net.server;

import com.ravat.hanzalah.securechat.net.Packet;

public class ServerChatPayload implements Packet.DataPayload {
    public enum MessageTypes{
        NEW_USER,
        USER_LEFT,
    }

    public final MessageTypes messageTypes;

    public final String userName;

    public ServerChatPayload(MessageTypes messageType, String userName){
        this.messageTypes = messageType;
        this.userName = userName;
    }
}