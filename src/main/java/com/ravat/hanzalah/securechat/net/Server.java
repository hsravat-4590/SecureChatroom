package com.ravat.hanzalah.securechat.net;

import javax.print.DocFlavor;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private Thread mThread;
    private List<Socket> clients;
    private boolean isRunning;
    private ServerSocket mServerSocket;
    public Server(int port){
        this.port = port;
        clients = new ArrayList<>();
        isRunning = false;
        try{
            mServerSocket = new ServerSocket(port);
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void startServer(){
        mThread.start();
        isRunning = true;
    }
    public void stopServer(){
        mThread.interrupt();
    }

    private void acceptClients(){
        while(isRunning){
            try {
                Socket socket = mServerSocket.accept();
                System.out.println("Accepting socket: "+ socket.getRemoteSocketAddress() );

            } catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

}
