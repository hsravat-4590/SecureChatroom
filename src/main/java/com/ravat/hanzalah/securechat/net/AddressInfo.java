package com.ravat.hanzalah.securechat.net;

import java.io.Serializable;

/**
 * Class which holds Address Information
 */
public class AddressInfo implements Serializable {
    public final String host;
    public final int port;

    public AddressInfo(String host, int port){
        this.host = host;
        this.port = port;
    }
}
