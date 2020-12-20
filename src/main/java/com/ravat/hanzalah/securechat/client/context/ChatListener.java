package com.ravat.hanzalah.securechat.client.context;

import com.ravat.hanzalah.securechat.common.packets.DataPacket;

/**
 * ChatListener Interface
 */
public interface ChatListener{

    /**
     * Monitors messages recieved
     */
    interface MessageRecievedListener extends ChatListener{
         void onMessageRecieved();
    }

    /**
     * Monitors messages sent
     */
    interface MessageSentListener extends ChatListener{
         void onMessageSent();
    }

}
