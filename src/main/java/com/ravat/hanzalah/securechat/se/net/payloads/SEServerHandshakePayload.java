package com.ravat.hanzalah.securechat.se.net.payloads;

import com.ravat.hanzalah.securechat.net.ACKPayload;

public class SEServerHandshakePayload extends ACKPayload {
    private final int status;
    public SEServerHandshakePayload(int status){
        this.status = status;
    }

    public int getStatus(){return status;}
}
