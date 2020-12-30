package com.ravat.hanzalah.securechat.se;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Loads and sets the correct truststore for this application.
 */
public class ChatTrustStore {
    private final X509TrustManager jreTrustManager = getJreTrustManager();
    private final X509TrustManager mTrustManager = getMyTrustManager();
    private final X509TrustManager mergedTrustManager = createMergedTrustManager(jreTrustManager,mTrustManager);
    public ChatTrustStore() throws KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
        setSystemTrustManager();
    }


    private X509TrustManager getJreTrustManager()
            throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        return findDefaultTrustManager(null);
    }

    private X509TrustManager getMyTrustManager()
            throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException{
        File mKeyFile = new File(System.getProperty("user.home")+"/SecureChat/secureChatTrustStore.jks");
        try{
            KeyStore mKeyStore = KeyStore.getInstance("jks");
            if(mKeyFile.exists()) {
                FileInputStream myKeys = new FileInputStream(mKeyFile);
                mKeyStore.load(myKeys, "password".toCharArray());
            } else{
                mKeyStore.load(null,"password".toCharArray());
                mKeyFile.getParentFile().mkdirs();
                try(FileOutputStream fileOutputStream = new FileOutputStream(mKeyFile)){
                    mKeyStore.store(fileOutputStream,"password".toCharArray());
                }
            }
            return findDefaultTrustManager(mKeyStore);
        } catch (FileNotFoundException ex){
            ex.printStackTrace();
        }
        return null;
    }

    private  X509TrustManager findDefaultTrustManager(KeyStore keyStore)
            throws NoSuchAlgorithmException,KeyStoreException{
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore); //If null i.e; when called from getJRETrustManager, it will initialise the default one
        for(TrustManager trustManager: trustManagerFactory.getTrustManagers()){
            if(trustManager instanceof X509TrustManager){
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }

    private X509TrustManager createMergedTrustManager(X509TrustManager jreTrustManager,X509TrustManager customTrustManager){
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                jreTrustManager.checkClientTrusted(x509Certificates,s);
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                try{
                    customTrustManager.checkServerTrusted(x509Certificates,s);
                } catch(CertificateException e){
                    jreTrustManager.checkServerTrusted(x509Certificates,s); // This will also throw a certificiate exception if not found in default trust manager
                }
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return jreTrustManager.getAcceptedIssuers();
            }
        };
    }

    /**
     * Set our Merged TrustStore as default in the SSLContext for TLS connections
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private void setSystemTrustManager() throws NoSuchAlgorithmException,KeyManagementException{
        SSLContext mSSLContext = SSLContext.getInstance("TLS");
        mSSLContext.init(null,new TrustManager[]{mergedTrustManager},null);
        SSLContext.setDefault(mSSLContext);
    }

}
