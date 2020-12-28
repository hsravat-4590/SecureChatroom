package com.ravat.hanzalah.securechat.tests.net;

import com.ravat.hanzalah.securechat.net.ChatPayload;
import com.ravat.hanzalah.securechat.net.Packet;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PacketTests {

    @Test
    public void testSerialization() throws IOException,ClassNotFoundException{
        // Use ChatPayloads as they are the simplest
        ChatPayload chatPayload = new ChatPayload("This is a Test!");
        Packet.Payload payload = new Packet.Payload(chatPayload);

        byte[] serializedPayload = Packet.serialiseObject(payload);
        Packet.Payload deserializedPayload = Packet.deserializeObject(serializedPayload);
        assertEquals(payload.metaData.author, deserializedPayload.metaData.author);
    }
}
