package com.ravat.hanzalah.securechat;

/**
 * Manifest for the SecureChat Application
 */
public class ApplicationManifest {

    public static final String APPLICATION_NAME = "Secure Chat";
    public static final class version{
        public static final int MAJOR = 1;
        public static final int MINOR = 0;
        public static final int PATCH = 0;
        public static String getSemanticString(){
            return MAJOR + "." + MINOR + "." + PATCH;
        }
    }
}
