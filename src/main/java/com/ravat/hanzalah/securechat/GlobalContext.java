package com.ravat.hanzalah.securechat;

import com.ravat.hanzalah.securechat.net.Client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The global context for this application. Serialisable allowing the system to save and reload chats automatically
 */
public final class GlobalContext implements Serializable {
    private static transient GlobalContext context;
    /**
     * The username of the user in Chat
     */
    public final String mUserName;
    private static final String DEFAULT_USERNAME = "DEFAULT_USER";
    private volatile boolean isRunning;
    private volatile Client chatClient;

    private GlobalContext(){
        mUserName = DEFAULT_USERNAME;
        isRunning = true;
    }
    private GlobalContext(String userName){
        mUserName = userName;
        isRunning = true;
    }
    /**
     * Gets the GlobalContext instance
     * @return The GlobalContext instance
     */
    public static GlobalContext getInstance(){
        return Objects.requireNonNullElseGet(context, () -> context = new GlobalContext());
    }

    /**
     * Context Factory is responsible for generating a custom GlobalContext. Due to the nature of the Context, only one Global Context can be alive at a time.
     */
    public static class ContextFactory{
        private String userName;
        private String serialisedContext;
        private GlobalContext restoredContext;

        /**
         * Constructs a new GlobalContext
         * @param userName The userName of the user
         */
        public static void createNewContext(String userName){
            GlobalContext newContext = new GlobalContext(userName);
            context = newContext;
        }

        /**
         * Attempts to restore a previous GlobalContext
         * @param path The path to the file with the GlobalContext
         * @throws IOException If the file does not exist or is corrupted, this exception will be thrown
         * @throws ClassNotFoundException If the file does not hold a Java Object type or if the Object type does not match the required type (GlobalContext)
         */
        public static void restoreContext(String path) throws IOException,ClassNotFoundException {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
            Object readObject = ois.readObject();
            if(readObject instanceof GlobalContext){
                context = (GlobalContext) readObject;
            } else{
                throw new ClassNotFoundException("Error: Object is not of type: GlobalContext");
            }
        }

    }

    public synchronized boolean getStatus(){return isRunning;}

    public void setChatClient(Client chatClient){this.chatClient = chatClient;}

    public Client getChatClient(){return chatClient;}
}
