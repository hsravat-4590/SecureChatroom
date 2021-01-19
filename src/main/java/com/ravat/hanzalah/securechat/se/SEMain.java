package com.ravat.hanzalah.securechat.se;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.ravat.hanzalah.securechat.Main;
import com.ravat.hanzalah.securechat.net.server.ServerController;
import com.ravat.hanzalah.securechat.se.net.server.SEServerController;
import com.ravat.hanzalah.securechat.ui.MainActivity;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;


/**
 * Derivative of Main Launcher class except that the TrustStores are loaded in and SSLContexts are set for this application
 */
public class SEMain extends Main {
    private static boolean SEActive = false;
    public static boolean isSEModeRunning(){return SEActive;}

    @Parameter(names = {"-keyStore"},description = "Set a custom keystore and not use the built in one")
    private String keyStore = ":-)";

    @Parameter(names = {"-setTrustStore" , "-trustStore"}, description = "Set a custom trustStore and not use the built in one")
    private String trustStore = ":-)";

    @Parameter(names = {"-debugssl","-ssldebug"}, description = "Enable SSL verbose output")
    private boolean sslDebug = false;

    @Parameter(names = {"-noSE","-seoff"}, description = "Switches to non-SE mode")
    private boolean noSE = false;

    public static void main(String[] args){
        SEMain main = new SEMain();
        JCommander commander = JCommander.newBuilder()
                .addObject(main)
                .build();
        commander.parse(args);
        if(main.printUsage){
            commander.usage();
            System.exit(0);
        }
        if(main.noSE){
            Main.main(args);
        }
        if(main.keyStore.equals(":-)")) {
            System.out.println("Using default keystore");
            System.setProperty("javax.net.ssl.keyStore", SEMain.class.getResource("/keys/ServerKeyStore.jks").getFile());
            System.setProperty("javax.net.ssl.keyStorePassword", "password");
        } else{
            System.out.println("Using specified keystore");
            String password;
            File newKeyStore = new File(main.keyStore);
            if(!newKeyStore.exists()){
                System.err.println("Error: Keystore does not exist... exiting");
                System.exit(-1);
            } else {
                password = new String(System.console().readPassword("Password for Keystore: "));
                System.setProperty("javax.net.ssl.keyStore", newKeyStore.getAbsolutePath());
                System.setProperty("javax.net.ssl.keyStorePassword",password);
            }
        }
        if(main.trustStore.equals(":-)")){
            System.setProperty("javax.net.ssl.trustStore", SEMain.class.getResource("/keys/ClientKeyStore.jks").getFile());
        } else{
            File newTrustStore = new File(main.trustStore);
            if(!newTrustStore.exists()){
                System.err.println("Error: TrustStore does not exist... exiting");
                System.exit(-1);
            } else{
                System.setProperty("javax.net.ssl.trustStore", newTrustStore.getAbsolutePath());
            }
        }
        if(main.sslDebug)
            System.setProperty("javax.net.debug","ssl");
        /**
        File serverKS = new File(SEMain.class.getResource("/keys/ServerKeyStore.jks").getFile());
        if(serverKS.exists()){
            System.out.println("Server KeyStore Exists");
        }
        String keyStoreProp = System.getProperty("javax.net.ssl.keyStore");
        if(keyStoreProp != null){
            System.out.println("Got it: " + keyStoreProp);
        }*/
        /**Don't bombard console with errors
        PrintStream original = System.out;
        System.setErr(new PrintStream(new OutputStream() {
            public void write(int b) {
                //DO NOTHING
            }
        }));
        //*/
        SEActive = true;
        if(main.portNumber > 0) {
            ServerController chatServer = null;
            try {
                chatServer = new SEServerController(main.portNumber);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else{
            MainActivity.main(args);
        }
    }
}
