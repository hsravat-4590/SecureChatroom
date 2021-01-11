package com.ravat.hanzalah.securechat.se;

import com.ravat.hanzalah.securechat.Main;
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
        //Set Permissive SSL
        PermissiveSSL.disableSSLValidation();
        SEActive = true;
        Main.main(args);
    }
}
