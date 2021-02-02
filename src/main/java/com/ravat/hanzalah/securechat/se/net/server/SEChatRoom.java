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
