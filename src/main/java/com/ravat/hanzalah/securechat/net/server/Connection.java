package com.ravat.hanzalah.securechat.net.server;

import com.ravat.hanzalah.securechat.net.Packet;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

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

    protected void send(Object toSend){
        try {
            objectOutputStream.writeObject(toSend);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
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

    public void sendPacket(Packet.Payload payload){
        if(!(payload.metaData.author.equals(userName)))
            send(payload);
    }

    public Packet.Payload readPayload() throws SocketTimeoutException,SocketException {
        Object recv = read();
        if(recv instanceof  Packet.Payload){
            return (Packet.Payload) recv;
        } else{
            return null;
        }
    }
}
