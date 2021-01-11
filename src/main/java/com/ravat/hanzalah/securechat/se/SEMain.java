package com.ravat.hanzalah.securechat.se;

import com.ravat.hanzalah.securechat.Main;
import com.ravat.hanzalah.securechat.se.net.server.SEServerController;
import com.ravat.hanzalah.securechat.se.trust.TrustStore;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Locale;

/**
 * Derivative of Main Launcher class except that the TrustStores are loaded in and SSLContexts are set for this application
 */
public class SEMain extends Main {
    private static boolean SEActive = false;
    public static boolean isSEModeRunning(){return SEActive;}
    public static void main(String[] args){
        /**
        try {
            TrustStore.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        SEActive = true;
        System.setProperty("javax.net.debug","ssl");
        if(args.length == 0)
            Main.main(args);
        else{
            int port = Integer.parseInt(args[0]);
            if(port > 0){
                try {
                    SEServerController serverController = new SEServerController(port);
                } catch(IOException exception){
                    exception.printStackTrace();
                }
            }
        }
    }
}
