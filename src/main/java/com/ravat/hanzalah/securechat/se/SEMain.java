package com.ravat.hanzalah.securechat.se;

import com.ravat.hanzalah.securechat.Main;
import com.ravat.hanzalah.securechat.se.net.server.SEServerController;

import java.io.IOException;


/**
 * Derivative of Main Launcher class except that the TrustStores are loaded in and SSLContexts are set for this application
 */
public class SEMain extends Main {
    private static boolean SEActive = false;
    public static boolean isSEModeRunning(){return SEActive;}
    public static void main(String[] args){

        System.setProperty("javax.net.ssl.keyStore", SEMain.class.getResource("/keys/ServerKeyStore.jks").getFile());
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
        System.setProperty("javax.net.ssl.trustStore", SEMain.class.getResource("/keys/ClientKeyStore.jks").getFile());
       // System.setProperty("javax.net.debug","ssl");
        SEActive = true;
        if(args.length == 0)
            Main.main(args);
        else{
            int port = Integer.parseInt(args[0]);
            if(port > 0){
                try {
                    new SEServerController(port);
                } catch(IOException exception){
                    exception.printStackTrace();
                }
            }
        }
    }
}
