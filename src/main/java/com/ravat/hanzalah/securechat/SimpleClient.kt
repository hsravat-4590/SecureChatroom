package com.ravat.hanzalah.securechat

import com.ravat.hanzalah.securechat.net.ChatPayload
import com.ravat.hanzalah.securechat.net.Packet
import java.io.DataOutputStream
import java.net.Socket
import javax.net.ssl.SSLSocketFactory

fun main(args: Array<String>) {
    GlobalContext.ContextFactory.createNewContext("Dave")
    System.setProperty("javax.net.ssl.trustStore", {}.javaClass.getResource("/keys/ClientKeyStore.jks").getFile())
    val host = "localhost"
    val port = 2828
    val socket = SSLSocketFactory.getDefault().createSocket(host,port)
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
