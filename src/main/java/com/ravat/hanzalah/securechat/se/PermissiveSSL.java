package com.ravat.hanzalah.securechat.se;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * When activated, SE mode switches from Enforcing to Permissive which means that SSL Certs are no longer validated
 */
public class PermissiveSSL {

    public static void disableSSLValidation(){
        // Create a custom trust manager that does not validate cert chains
        TrustManager[] permissiveTrustManager  = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        // Enable the all-trusting TM
        try{
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null,permissiveTrustManager,new SecureRandom());
            SSLContext.setDefault(sslContext);
        } catch(Exception ex){

        }
    }
}
