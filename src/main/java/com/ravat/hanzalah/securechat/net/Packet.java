/* 
 *  MIT License
 *  
 *  Copyright (c) 2021-2021 Hanzalah Ravat
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.ravat.hanzalah.securechat.net;

import com.ravat.hanzalah.securechat.GlobalContext;
import com.ravat.hanzalah.securechat.net.server.ServerChatPayload;

import java.io.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface Packet  {
    /**
     * The maximum size of a packet
     */
    int MAX_PACKET_LENGTH = 4096;
    /**
     * A structure which holds relevant metadata regarding the message
     */
    class MetaData implements Serializable {
        /**
         * The user that authored the message
         */
        public final String author;
        /**
         * The Time of the message in UTC Format. The messages are converted into local time when parsed by the reciever to ensure that the chat works smoothly across time-zones
         */
        public final ZonedDateTime time;
        /**
         * The Canonical Name of the class
         */
        public final String payloadINF;

        /**
         * Constructs the payload metadata
         *
         * @param payload The payload you wish to get the metadata for
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
        interface DataPayload extends Serializable{}

    /**
     * Main Payload class which is used to send DataPayloads over the network with metadata.
     */
    class Payload implements Serializable{
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
        static byte[] serialiseObject(Payload packet)  {
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
            } finally {
                try {
                    out.close();
                    byteArrayOutputStream.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
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
            Payload output = (Payload) is.readObject();
            if(output != null) {
                return output;
            } else {
                throw new InvalidObjectException("Error: Expected a Payload Type");
            }
        }
}
