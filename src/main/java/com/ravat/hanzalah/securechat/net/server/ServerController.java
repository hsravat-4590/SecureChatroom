package com.ravat.hanzalah.securechat.net.server;


import java.io.IOException;

public class ServerController {

    protected AcceptThread acceptThread;
    protected final int port;
    private static volatile boolean isRunning = false;

    public ServerController(int listenPort) throws IOException {
        this.port = listenPort;
        initialise();
        isRunning = true;
        acceptThread.start();
    }

    protected void initialise() throws IOException {
        this.acceptThread = new AcceptThread(port);
    }

    public String getServerAddress(){
        return acceptThread.getHostAddress();
    }

    public static boolean isServerRunning(){return isRunning;}


}
