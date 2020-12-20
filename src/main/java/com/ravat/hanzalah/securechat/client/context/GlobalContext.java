package com.ravat.hanzalah.securechat.client.context;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
    private static final int STARTING_PORT = 270000;
    private int currentPort;
    private Map<String,ChatContext> chatContextMap;
    private volatile boolean isRunning;
    private volatile ChatContext currentChat;

    private GlobalContext(){
        mUserName = DEFAULT_USERNAME;
        chatContextMap = new HashMap<>();
    }
    private GlobalContext(String userName){
        chatContextMap = new HashMap<>();
        mUserName = userName;
        currentPort = STARTING_PORT;
    }
    /**
     * Gets the GlobalContext instance
     * @return The GlobalContext instance
     */
    public static GlobalContext getInstance(){
        if(context == null){
            return context = new GlobalContext();
        } else {
            return context;
        }
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
    /**
     * Gets a chatcontext from open chats
     * @param chatName The name of the chat
     * @return The ChatContext for the selected Chat, if available in the global context. If not, It will return null.
     */
    public ChatContext getChatContext(String chatName){
        return chatContextMap.get(chatName);
    }

    /**
     * Adds a new chat context to the chats
     * @param context The chatcontext to be added
     */
    public void addChatContext(ChatContext context){
        chatContextMap.put(context.chatName, context);
    }

    /**
     * Gets the map holding all chat contexts.
     * @return All current chat contexts in a map format
     */
    public Map<String,ChatContext> getContexts(){return chatContextMap;}
    public int getNextAvailablePort(){
        return currentPort++;
    }

    /**
     * Sets the Current Chat Context in view
     * @param context The Chat Context in View
     */
    public void setCurrentContext(ChatContext context){
        currentChat = context;
    }

    public ChatContext getCurrentChat() {
        return currentChat;
    }

    public boolean getStatus(){return isRunning;}
}
