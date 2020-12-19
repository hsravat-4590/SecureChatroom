package com.ravat.hanzalah.securechat.common.util.serialization;

import java.io.Serializable;

public interface ActionSerializable extends Serializable {
    /**
     * Action to be performed before serialization
     */
    public void onSerialize();

    /**
     * Action to be performed after deserialization
     */
    public void onDeserialize();
}
