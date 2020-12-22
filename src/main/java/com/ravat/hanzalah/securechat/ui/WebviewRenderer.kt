package com.ravat.hanzalah.securechat.ui

import java.time.format.DateTimeFormatter
import com.github.rjeschke.txtmark.*;
import com.ravat.hanzalah.securechat.GlobalContext
import com.ravat.hanzalah.securechat.net.ChatPayload
import com.ravat.hanzalah.securechat.net.Client
import com.ravat.hanzalah.securechat.net.server.ServerChatPayload


class WebviewRenderer(chatClient: Client){
    private val HTMLBody: String = "<body>"
    private val HTMLHeaders: String = "<head>\n" +
            "    <title>TestWebpage</title>\n" +
            "    <link rel=\"stylesheet\" href=\"webview.css\">\n" +
            "</head>"
    private val client: Client = chatClient

    fun addMessageToWebview():String{
        val payload = client.lastPayload
        if(payload.payload is ServerChatPayload){
            // It's a server message (right now that just means that someone has entered or left)
            val divClass = "message_stamp"
            val messageCause = payload.payload.messageTypes
            val message = when(messageCause){
                ServerChatPayload.MessageTypes.USER_LEFT -> "${payload.payload.userName} has left the chat"
                ServerChatPayload.MessageTypes.NEW_USER -> "${payload.payload.userName} has entered the chat"
            }
            return """<div class= "message_stamp"> ${message} </div>"""
        } else if(payload.payload is ChatPayload){
            // Its a chat!
            val author = payload.metaData.author
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val date = payload.metaData.time.toLocalDateTime().format(formatter)
            val body = Processor.process(payload.payload.chatMessage)
            val divClass = if(author == GlobalContext.getInstance().mUserName){
                "local_message_card"
            } else{
                "external_message_card"
            }
            val divTag =
                """
                            <div class="$divClass">
                            <div class="container">
                              <h4><b>${author}</b></h4>
                              $body
                              <h6><b>${date}</b></h6>
                            </div>
                          </div>
                          <div class = "spacing"></div>
                    """.trimIndent()
            // Append to current HTML body
            HTMLBody.plus(divTag)
        }
        //Generate new HTML body to return
        return HTMLHeaders.plus(HTMLBody).plus("</body>")
    }
}