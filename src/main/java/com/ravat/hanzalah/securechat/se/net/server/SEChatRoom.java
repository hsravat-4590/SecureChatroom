package com.ravat.hanzalah.securechat.se.net.server;

import com.ravat.hanzalah.securechat.net.Packet;
import com.ravat.hanzalah.securechat.net.server.ChatRoom;
import com.ravat.hanzalah.securechat.net.server.ChatRoomDeriver;
import com.ravat.hanzalah.securechat.net.server.Connection;
import com.ravat.hanzalah.securechat.net.server.ServerChatPayload;
import com.ravat.hanzalah.securechat.se.net.payloads.SEServerChatPayload;
import com.ravat.hanzalah.securechat.se.net.payloads.SEServerHandshakePayload;

public class SEChatRoom extends ChatRoomDeriver {

    public SEChatRoom() throws ChatRoomDeriverSetException{
        super();
    }
    @Override
    public Packet.Payload generateServerChatPayload(ServerChatPayload.MessageTypes messageType, Connection user, String userName) {
        if(messageType == ServerChatPayload.MessageTypes.NEW_USER && user instanceof SEConnection){
            return new Packet.Payload(new SEServerChatPayload(userName,((SEConnection) user).publicKey));
        } else{
            return generateServerChatPayload(messageType, user, userName);
        }
    }
}
