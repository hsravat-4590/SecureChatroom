package com.ravat.hanzalah.securechat.net.server;


import com.ravat.hanzalah.securechat.se.net.server.SEChatRoom;

import java.io.IOException;

/**
 * A server controller is the class responsible for starting the main server thread (accept thread) and also provides some information regarding the server.
 */
public class ServerController {

    protected AcceptThread acceptThread;
    protected final int port;
    private static volatile boolean isRunning = false;

    /**
     * Constructs a ServerController and starts the AcceptThread
     * @param listenPort
     * @throws IOException
     */
    public ServerController(int listenPort) throws IOException {
        this.port = listenPort;
        initialise();
        isRunning = true;
        new SEChatRoom(); // Create an SEChatRoom before starting the server to ensure that the SE Derivator is used over the Default one for some methods
        acceptThread.start();
    }

    protected void initialise() throws IOException {
        this.acceptThread = new AcceptThread(port);
    }

    /**
     * Gets the Server's IP address
     * @return The IP Address of the server
     */
    public String getServerAddress(){
        return acceptThread.getHostAddress();
    }

    /**
     * Checks if the server is running
     * @return
     */
    public static boolean isServerRunning(){return isRunning;}


}
