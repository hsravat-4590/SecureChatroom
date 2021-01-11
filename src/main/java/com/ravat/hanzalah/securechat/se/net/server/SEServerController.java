package com.ravat.hanzalah.securechat.se.net.server;

import com.ravat.hanzalah.securechat.net.server.ServerController;

import java.io.IOException;

/**
 * Overrides the original ServerController to ensure that SEVariants of the Server are used
 */
public class SEServerController extends ServerController {


    public SEServerController(int listenPort) throws IOException {
        super(listenPort);

    }

    @Override
    public void initialise() throws IOException{
        this.acceptThread = new SEAcceptThread(port);
    }
}
