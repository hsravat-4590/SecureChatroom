package com.ravat.hanzalah.securechat;

import com.ravat.hanzalah.securechat.net.server.ServerController;
import com.ravat.hanzalah.securechat.ui.MainActivity;

import java.io.IOException;

public class Main {

    public static void main(String[] args){
        if(args.length == 0){
            MainActivity.main(args);
        } else{
            int port = Integer.parseInt(args[0]);
            if(port > 0){
                ServerController chatServer = null;
                try {
                    chatServer = new ServerController(port);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
