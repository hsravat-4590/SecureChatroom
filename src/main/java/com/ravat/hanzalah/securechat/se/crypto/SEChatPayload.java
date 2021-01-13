package com.ravat.hanzalah.securechat.se.crypto;

import com.ravat.hanzalah.securechat.net.ChatPayload;
import com.ravat.hanzalah.securechat.se.SESessionContext;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

/**
 * A derivative of ChatPayload but the message is encrypted
 */
public class SEChatPayload extends ChatPayload {

    public SEChatPayload(String chatMessage) throws BadPaddingException, IllegalBlockSizeException {
        super(SESessionContext.getInstance().getAesFaclilitator().encrypt(chatMessage));
    }

    @Override
    public String getChatMessage(){
        try {
            return SESessionContext.getInstance().getAesFaclilitator().decrypt(super.getChatMessage());
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return "Error: Message could not be processed";
        }
    }
}
