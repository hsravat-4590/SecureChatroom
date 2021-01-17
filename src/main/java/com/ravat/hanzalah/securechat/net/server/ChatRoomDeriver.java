package com.ravat.hanzalah.securechat.net.server;

import com.ravat.hanzalah.securechat.net.Packet;

/**
 * ChatRoom cannot be extended in the colloquial sense due to constraints in it's fundamental architecture. This Interface provides methods that can be 'overriden'. To use your implementation, you must pass the class through the static setChatRoomDeriver(ChatRoomDeriver) method. NOTE: You can only call this method once and it must be called before a chatroom is constructed otherwise the standard method calls will be used
 */
public abstract class ChatRoomDeriver{

    public ChatRoomDeriver() throws ChatRoomDeriverSetException{
        boolean set = ChatRoom.setDeriver(this);
        if(!set){
            throw new ChatRoomDeriverSetException(getClass().getName());
        }
    }
    public abstract Packet.Payload generateServerChatPayload(ServerChatPayload.MessageTypes messageType, Connection user, String userName);

    public class ChatRoomDeriverSetException extends InstantiationError{
        public ChatRoomDeriverSetException(String name){
            super("Error setting: " + name + "as a ChatRoom Deriver. A deriver has already been set");
        }

    }

}