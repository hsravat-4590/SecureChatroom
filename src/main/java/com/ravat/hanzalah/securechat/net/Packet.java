package com.ravat.hanzalah.securechat.net;

import com.ravat.hanzalah.securechat.GlobalContext;
import com.ravat.hanzalah.securechat.net.server.ServerChatPayload;

import java.io.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface Packet  {

    /**
     * A structure which holds relevant metadata regarding the message
     */
    class MetaData {
        /**
         * The user that authored the message
         */
        public final String author;
        /**
         * The Time of the message in UTC Format. The messages are converted into local time when parsed by the reciever
         */
        public final ZonedDateTime time;
        /**
         * The Canonical Name of the class
         */
        public final String payloadINF;

        /**
         * Constructs the payload metadata
         *
         * @param payload The payload
         */
        public MetaData(DataPayload payload) {
            time = ZonedDateTime.now(ZoneId.of("UTC"));
            if(payload instanceof ServerChatPayload){
                author = "Server";
            } else {
                author = GlobalContext.getInstance().mUserName;
            }
            payloadINF = payload.getClass().getCanonicalName();
        }
    }

        /**
         * Supertype for any form of Payload
         */
        public interface DataPayload extends Serializable{}

        public class Payload implements Serializable{
            public final MetaData metaData;

            public final DataPayload payload;

            public Payload(DataPayload payload){
                this.metaData = new MetaData(payload);
                this.payload = payload;
            }
        }

        /**
         * Serialises a packet to allow for stable sending over socket connection
         * @param packet An implementation of DataPacket
         * @return The supplied DataPacket in byte form
         */
        static byte[] serialiseObject(Payload packet){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream out = null;
            byte[] objectArray = null;
            try{
                out = new ObjectOutputStream(byteArrayOutputStream);
                out.writeObject(packet);
                out.flush();
                objectArray = byteArrayOutputStream.toByteArray();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            return objectArray;
        }

        /**
         * Converts a byte array back into a DataPacket Object
         * @param data The expected DataPacket in Byte array format
         * @return A datapacket type which holds the information. You are able to obtain packet metadata using this return but will need to cast to an object to make use of specific functionality
         * @throws IOException This method will throw an IOException if it is unable to parse the byte array
         * @throws ClassNotFoundException A ClassNotFound Exception is thrown when the class obtained is not an instance of Payload
         */
        static Payload deserializeObject(byte[] data) throws IOException, ClassNotFoundException {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            if(is.readObject() instanceof Payload) {
                return (Payload)is.readObject();
            } else {
                throw new InvalidObjectException("Error: Expected a Payload Type");
            }
        }
}
