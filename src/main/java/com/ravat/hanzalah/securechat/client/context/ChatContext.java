package com.ravat.hanzalah.securechat.client.context;

import com.ravat.hanzalah.securechat.common.net.AddressInfo;
import com.ravat.hanzalah.securechat.common.packets.DataPacket;
import com.ravat.hanzalah.securechat.common.packets.SecurePayload;
import com.ravat.hanzalah.securechat.common.util.serialization.ActionSerializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ChatContext extends SecurePayload implements ActionSerializable {
    /**
     * The Name of the chat as displayed on the chat bar
     */
    public final String chatName;
    private volatile ZonedDateTime dateTimeOfLastMessage;
    private volatile String lastChat;
    private String chatBuffer;
    private transient Queue<DataPacket> sendQueue;
    private transient String messagesString;
    private transient List<ChatListener.MessageSentListener> sentListenerList;
    private transient List<ChatListener.MessageRecievedListener> recievedListenerList;

    /**
     * Constructs a ChatContext and will attempt to establish a connection with the server to send/recieve
     * @param chatName The Name of the chat to appear in the chat bar
     * @param chatHost The Address of the server
     * @param connectionPort The port of the server
     */
    public ChatContext(String chatName,String chatHost, int connectionPort){
        super(new AddressInfo(chatHost,GlobalContext.getInstance().getNextAvailablePort(), connectionPort));
        this.chatName = chatName;
        intializeEnvironment();
    }

    private void intializeEnvironment(){
        sendQueue = new LinkedList<DataPacket>();
        sentListenerList = new LinkedList<>();
        recievedListenerList = new LinkedList<>();
    }
    /**
     * Action to be performed before serialization
     */
    @Override
    public void onSerialize() {
        // Close the connection
        if(mSocket.isConnected()){
            try {
                mSocket.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Action to be performed after deserialization
     */
    @Override
    public void onDeserialize() {
        // Attempt to Re-establish Connection
        // If not, create a new connection to the server
        intializeEnvironment();
    }

    /**
     * Updates the chat buffer. The contents of this buffer is what is packaged into a DataPacket and sent to the server
     * @param content The new chat buffer content
     * @param append Indicates if the string provided should be appended onto the current buffer.
     */
    public void updateChatBuffer(String content,boolean append){
        if(append)
            chatBuffer = chatBuffer + content;
        else
            chatBuffer = content;
    }

    public ZonedDateTime getDateTimeOfLastMessage(){return dateTimeOfLastMessage;}
    public String getLastChat(){return lastChat;}

    public void addListener(ChatListener listener){
        if(listener instanceof ChatListener.MessageSentListener)
            sentListenerList.add((ChatListener.MessageSentListener) listener);
        if(listener instanceof ChatListener.MessageRecievedListener)
            recievedListenerList.add((ChatListener.MessageRecievedListener) listener);
    }
}
