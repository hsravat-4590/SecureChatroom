package com.ravat.hanzalah.securechat.se.trust;

import com.ravat.hanzalah.securechat.se.ChatTrustStore;

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

public class TrustStore {
    private final X509TrustManager jreTrustManager = getJreTrustManager();
    private final X509TrustManager mTrustManager = getMyTrustManager();
    private final X509TrustManager mergedTrustManager = createMergedTrustManager(jreTrustManager,mTrustManager);
    private static TrustStore instance;
    private static final String TRUST_STORE_PATH = System.getProperty("user.home")+"/SecureChat/SecureChatTrustStore.jks";
    private static final char[] PASSWORD = "password".toCharArray();
    public static TrustStore getInstance() {
        if (instance == null){
            try {
                return instance = new TrustStore();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
    private TrustStore() throws KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
        setSystemTrustManager();
    }


    private X509TrustManager getJreTrustManager()
            throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        return findDefaultTrustManager(null);
    }

    private X509TrustManager getMyTrustManager()
            throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException{
        File mKeyFile = new File(TRUST_STORE_PATH);
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

    public boolean validateServerCertificates(X509Certificate[] certificates,String authType){
        try {
            mergedTrustManager.checkServerTrusted(certificates,authType);
            return true;
        } catch (CertificateException e) {
            e.printStackTrace();

        }
        return false;
    }

    /**
     * Adds a provided certificate to the TrustStore
     * @param alias
     * @param cert
     */
    public void addCertificate(String alias,X509Certificate cert){
        // Assume that keystore exists already due to the application flow
        try{
            KeyStore mKeystore = KeyStore.getInstance("jks");
            File trustFile = new File(TRUST_STORE_PATH);
            FileInputStream fileInputStream = new FileInputStream(trustFile);
            mKeystore.load(fileInputStream,PASSWORD);
            fileInputStream.close();
            mKeystore.setCertificateEntry(alias,cert);
            FileOutputStream fileOutputStream = new FileOutputStream(trustFile);
            mKeystore.store(fileOutputStream,PASSWORD);
            fileOutputStream.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
