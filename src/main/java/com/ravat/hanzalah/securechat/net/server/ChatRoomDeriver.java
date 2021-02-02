/* 
 *  MIT License
 *  
 *  Copyright (c) 2021-2021 Hanzalah Ravat
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

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