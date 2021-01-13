package com.ravat.hanzalah.securechat.se;

import com.ravat.hanzalah.securechat.se.crypto.AES;

import javax.crypto.SecretKey;

/**
 * Singleton Class which holds important classes for instances
 */
public class SESessionContext {

    private SESessionContext(){}
    private AES aesFaclilitator;

    private static SESessionContext instance;

    public static SESessionContext getInstance(){
        if(instance == null)
            instance = new SESessionContext();
        return instance;
    }

    public void createNewAESFacilitator(){
        aesFaclilitator = new AES();
    }

    public void setAesFaclilitator(SecretKey key){
        aesFaclilitator = new AES(key);
    }

    public AES getAesFaclilitator() throws InstanceNotFoundException{
        if(aesFaclilitator == null) {
            throw new InstanceNotFoundException("AESFacilitator");
        } else {
            return aesFaclilitator;
        }
    }

    public class InstanceNotFoundException extends NullPointerException{
        InstanceNotFoundException(String instanceName){
            super("An Instance for: "+ instanceName + " has not been set in the SESessionContext");
        }
    }
}
