package com.ravat.hanzalah.securechat.common.util.serialization;

import java.io.IOException;
import java.io.InputStream;

public class ObjectInputStream extends java.io.ObjectInputStream {

    public ObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    /**
     * This method attempts to read the Object from the provided InputStream and perform the onDeserialize method before returning the object
     * @return The Object found in the InputStream
     * @throws IOException If the method is unable to read the InputStream
     * @throws ClassNotFoundException If the method is unable to parse the InputStream into a class
     */
    public Object readObjectWithAction() throws ClassNotFoundException, IOException {
        Object readObject  = super.readObject();
        if(readObject instanceof ActionSerializable){
            ((ActionSerializable) readObject).onDeserialize();
        }
        return (Object) readObject;
    }
}
