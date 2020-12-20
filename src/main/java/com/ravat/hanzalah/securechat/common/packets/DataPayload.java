package com.ravat.hanzalah.securechat.common.packets;

import com.ravat.hanzalah.securechat.common.net.AddressInfo;
import com.ravat.hanzalah.securechat.common.util.serialization.ActionSerializable;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public abstract class DataPayload implements ActionSerializable {

    protected transient Socket mSocket;
    protected final AddressInfo addressInfo;
    private transient Queue<byte[]> outQueue;
    private transient Queue<byte[]> inQueue;
    protected transient Thread mThread;
    public DataPayload(AddressInfo addrInfo){
        this.addressInfo = addrInfo;
        initializeEnvironment();
    }

    /**
     * Gets the Socket associated with the Payload
     * @return The Socket Associated with the Payload
     */
    public Socket getSocket(){ return mSocket; }
    protected void initializeEnvironment(){
        try{
            mSocket = new Socket(addressInfo.host,addressInfo.remotePort);
            outQueue = new LinkedList<>();
            inQueue = new LinkedList<>();
        } catch(IOException ex){
            ex.printStackTrace();
        }
    }

    protected void addToQueue(DataPayload payload){
        DataPacket.Payload outPayload = new DataPacket.Payload(payload);
        outQueue.add(DataPacket.serialiseObject(outPayload));
    }

    /**
     * Returns the DataPayload (Without Metadata)
     * @return A DataPayload if there is anything to get otherwise null
     */
    protected DataPayload getInboxHeadPayload(){
        try {
            return DataPacket.deserializeObject(inQueue.remove()).payload;
        } catch (Exception ex){
            return null;
        }
    }

    /**
     * Returns the entire packet including metadata
     * @return The entire packet from the inbox
     */
    protected DataPacket.Payload getInboxHead(){
        try{
            return DataPacket.deserializeObject(inQueue.remove());
        } catch (Exception ex){
            return null;
        }
    }

    /**
     * Gets the head of the queue for sending
     * @return The head of the OutQueue
     */
    public byte[] getHead(){return outQueue.remove();}

    /**
     * Adds a new message into the in queue
     * @param inMessage
     */
    public void addInMessage(byte[] inMessage){
        inQueue.add(inMessage);
    }
}
