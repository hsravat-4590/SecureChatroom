package com.ravat.hanzalah.securechat.se;

import com.ravat.hanzalah.securechat.Main;
import com.ravat.hanzalah.securechat.se.net.server.SEServerController;
import com.ravat.hanzalah.securechat.se.trust.TrustStore;

import java.io.File;
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
        File testCacerts = new File(SEMain.class.getResource("/keys/ClientKeyStore.jks").getFile());
        System.setProperty("javax.net.ssl.keyStore", SEMain.class.getResource("/keys/ServerKeyStore.jks").getFile());
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
        System.setProperty("javax.net.ssl.trustStore", SEMain.class.getResource("/keys/ClientKeyStore.jks").getFile());
       // System.setProperty("javax.net.debug","ssl");
        String keyStoreProp = System.getProperty("javax.net.ssl.keyStore");
        if(keyStoreProp != null){
            System.out.println("Got it: " + keyStoreProp);
        }
        SEActive = true;
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
