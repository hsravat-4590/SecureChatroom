package com.ravat.hanzalah.securechat.common.packets;

import com.ravat.hanzalah.securechat.common.net.AddressInfo;

public abstract class SecurePayload extends DataPayload {

    public SecurePayload(AddressInfo addrInfo){
        super(addrInfo);
    }
}
