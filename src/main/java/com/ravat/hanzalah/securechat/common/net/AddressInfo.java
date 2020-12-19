package com.ravat.hanzalah.securechat.common.net;

import java.io.Serializable;

/**
 * Class which holds Address Information
 */
public class AddressInfo implements Serializable {
    public final String host;
    public final int localPort;
    public final int remotePort;

    public AddressInfo(String host, int localPort, int remotePort){
        this.host = host;
        this.localPort = localPort;
        this.remotePort = remotePort;
    }
}
