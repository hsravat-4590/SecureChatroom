package com.ravat.hanzalah.securechat.net.server2;


import java.io.IOException;
import java.util.Map;

public class ServerController {

    private final AcceptThread acceptThread;

    private static volatile boolean isRunning = false;

    public ServerController(int listenPort) throws IOException {
        this.acceptThread = new AcceptThread(listenPort);
        isRunning = true;
        acceptThread.start();
    }

    public static boolean isServerRunning(){return isRunning;}


}
