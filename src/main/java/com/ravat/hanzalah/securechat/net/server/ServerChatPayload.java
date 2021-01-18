package com.ravat.hanzalah.securechat.net.server;

import com.ravat.hanzalah.securechat.net.Packet;

/**
 * A ServerChatPayload is a DataPayload that contains information regarding users joining and leaving a chatroom
 */
public class ServerChatPayload implements Packet.DataPayload {
    public enum MessageTypes{
        /**
         * Indicates a new use has joined
         */
        NEW_USER,
        /**
         * Indicates a user has left
         */
        USER_LEFT,
    }

    /**
     * The MessageType for this DataPayload
     */
    public final MessageTypes messageTypes;
    /**
     * The user in question.
     */
    public final String userName;

    public ServerChatPayload(MessageTypes messageType, String userName){
        this.messageTypes = messageType;
        this.userName = userName;
    }
}