package com.ravat.hanzalah.securechat.common.util;

/**
 * Singleton Logger Class based on java.util.logging.Logger
 */
public class Logger {

    private Logger mLogger;
    public Logger getLogger(){
        if(mLogger == null){
            mLogger = new Logger();
        }
        return mLogger;
    }

    private Logger(){
        // Get Log Level from Manifest
    }

}
