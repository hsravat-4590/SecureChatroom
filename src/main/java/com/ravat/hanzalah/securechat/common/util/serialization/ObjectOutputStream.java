package com.ravat.hanzalah.securechat.common.util.serialization;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

public class ObjectOutputStream extends java.io.ObjectOutputStream{
    public ObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    public void writeWithAction(Serializable object){
        if(object instanceof  ActionSerializable){
            ((ActionSerializable) object).onSerialize();
        }
        //TODO Serialise the Object
    }
}
