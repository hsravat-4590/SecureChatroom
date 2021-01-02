package com.ravat.hanzalah.securechat.se;

import javax.net.ssl.SSLSession;
import java.math.BigInteger;

public class SessionInfo {

    public static void printSessionInfo(SSLSession session){
        println("========================================");
        println("SSL Session Information");
        println("========================================");
        println("Peer host is: " + session.getPeerHost());
        println("Cipher is: " + session.getCipherSuite());
        println("Protocol is: " + session.getProtocol());
        println("Session ID: "+ new BigInteger(session.getId()));
        println("Session Created at: " + session.getCreationTime());
        println("Session last Accessed at:  " + session.getLastAccessedTime());
        println("========================================");
    }

    private static void println(String string){
        System.out.println(string);
    }
}
