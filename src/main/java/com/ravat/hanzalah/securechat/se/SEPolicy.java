package com.ravat.hanzalah.securechat.se;

public class SEPolicy {

    public enum SEModes{
        PERMISSIVE,
        ENFORCING
    }

    private SEPolicy(){}

    private SEPolicy instance;

    public SEPolicy getPolicy(){
        if(instance == null){
            instance = new SEPolicy();
        }
        return instance;
    }
}
