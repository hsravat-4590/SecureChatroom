package com.ravat.hanzalah.securechat.net;

public class ServerPayload implements Packet.DataPayload {
    public enum MessageTypes{
        NEW_USER,
        USER_LEFT,
    }

    public final MessageTypes messageTypes;

    public final String userName;

    public ServerPayload(MessageTypes messageType, String userName){
        this.messageTypes = messageType;
        this.userName = userName;
    }
}
