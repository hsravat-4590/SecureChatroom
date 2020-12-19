package com.ravat.hanzalah.securechat.client.context;

import com.ravat.hanzalah.securechat.common.net.AddressInfo;
import com.ravat.hanzalah.securechat.common.packets.DataPacket;
import com.ravat.hanzalah.securechat.common.util.serialization.ActionSerializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ChatContext implements ActionSerializable {
    /**
     * The Name of the chat as displayed on the chat bar
     */
    public final String chatName;
    private String chatBuffer;
    private final AddressInfo addressInfo;
    private transient Socket mSocket;
    private transient Queue<DataPacket> sendQueue;

    /**
     * Constructs a ChatContext and will attempt to establish a connection with the server to send/recieve
     * @param chatName The Name of the chat to appear in the chat bar
     * @param chatHost The Address of the server
     * @param connectionPort The port of the server
     */
    public ChatContext(String chatName,String chatHost, int connectionPort){
        this.chatName = chatName;
        addressInfo = new AddressInfo(chatHost,GlobalContext.getInstance().getNextAvailablePort(), connectionPort);
        intializeEnvironment();
    }

    private void intializeEnvironment(){
        try{
            mSocket = new Socket(addressInfo.host,addressInfo.remotePort);
        } catch(IOException ex){
            ex.printStackTrace();
        }
        sendQueue = new LinkedList<DataPacket>();
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

    public DataPacket generatePacket(){
        DataPacket packet = new DataPacket() {

        };
        return packet;
    }

}
