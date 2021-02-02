/* 
 *  MIT License
 *  
 *  Copyright (c) 2020-2021 Hanzalah Ravat
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

package com.ravat.hanzalah.securechat.net.server;

import com.ravat.hanzalah.securechat.net.Packet;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * A Connection instance holds information regarding a server connection and is also the class to interact directly with the socket and socket streams.
 */
public class Connection {
    private final Socket socket;
    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream objectInputStream;
    private volatile String userName;

    public Connection(Socket socket) throws IOException{
        this.socket = socket;
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    public Connection(Socket socket,ObjectOutputStream outputStream, ObjectInputStream inputStream) throws SocketException {
        this.socket = socket;
        this.objectOutputStream = outputStream;
        this.objectInputStream = inputStream;
        this.socket.setSoTimeout(500);
    }

    /**
     * Protected method which allows derived classes to send objects.
     * @param toSend
     */
    protected void send(Object toSend){
        try {
            objectOutputStream.writeObject(toSend);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Protected method which allows derived classes to read objects without type-checking
     * @return
     * @throws SocketTimeoutException
     * @throws SocketException
     */
    protected Object read()throws SocketTimeoutException,SocketException{
        try {
            return objectInputStream.readObject();
        } catch (SocketTimeoutException exception) {
            throw new SocketTimeoutException();
        } catch (SocketException exception){
            throw new SocketException();
        } catch (ClassNotFoundException|IOException ex){
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Method to send a packet to the remote side of the connection
     * @param payload
     */
    public void sendPacket(Packet.Payload payload){
        if(!(payload.metaData.author.equals(userName)))
            send(payload);
    }

    /**
     * Method to read a Packet.Payload from the socket
     * @return A Payload object or null if the object read was not an implementation of Packet.Payload
     * @throws SocketTimeoutException
     * @throws SocketException
     */
    public Packet.Payload readPayload() throws SocketTimeoutException,SocketException {
        Object recv = read();
        if(recv instanceof  Packet.Payload){
            return (Packet.Payload) recv;
        } else{
            return null;
        }
    }
}
