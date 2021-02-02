/* 
 *  MIT License
 *  
 *  Copyright (c) 2021-2021 Hanzalah Ravat
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

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
