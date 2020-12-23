package com.ravat.hanzalah.securechat

import com.ravat.hanzalah.securechat.net.ChatPayload
import com.ravat.hanzalah.securechat.net.Packet
import java.io.DataOutputStream
import java.net.Socket

    fun main(args: Array<String>) {
        GlobalContext.ContextFactory.createNewContext("Dave")
        val host = "localhost"
        val port = 2000
        val socket = Socket(host, port)
        val dataOutputStream = DataOutputStream(socket.getOutputStream())
        val payload = Packet.Payload(
                ChatPayload("Handshake")
        )
        println(payload.metaData.author)
        val byteArray = Packet.serialiseObject(payload)
        if(byteArray == null){
            println("The Byte Array is Null!")
        } else {
            dataOutputStream.write(byteArray)
        }
        while(true){
            // Do nothing
        }
    }
