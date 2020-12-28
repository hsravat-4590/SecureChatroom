package com.ravat.hanzalah.securechat.net.server;


import java.io.IOException;

public class ServerController {

    private final AcceptThread acceptThread;

    private static volatile boolean isRunning = false;

    public ServerController(int listenPort) throws IOException {
        this.acceptThread = new AcceptThread(listenPort);
        isRunning = true;
        acceptThread.start();
    }

    public String getServerAddress(){
        return acceptThread.getHostAddress();
    }

    public static boolean isServerRunning(){return isRunning;}


}
