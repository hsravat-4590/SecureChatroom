package com.ravat.hanzalah.securechat.se;

import com.ravat.hanzalah.securechat.Main;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * Derivative of Main Launcher class except that the TrustStores are loaded in and SSLContexts are set for this application
 */
public class SEMain extends Main {

    public static void main(String[] args){
        try {
            ChatTrustStore.getInstance();
        } catch (KeyManagementException | NoSuchAlgorithmException | CertificateException | KeyStoreException | IOException e) {
            e.printStackTrace();
        }
        Main.main(args);
    }
}
