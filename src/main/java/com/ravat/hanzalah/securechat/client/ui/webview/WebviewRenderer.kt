package com.ravat.hanzalah.securechat.client.ui.webview

import com.ravat.hanzalah.securechat.common.packets.DataPacket
import java.time.format.DateTimeFormatter
import com.github.rjeschke.txtmark.*;
import com.ravat.hanzalah.securechat.client.context.ChatContext
import com.ravat.hanzalah.securechat.client.context.GlobalContext

/**
 * This class generates the text for the Webview. It also holds the current HTML body for a chatContext
 */
class WebviewRenderer(context: ChatContext){
    val HTMLBody: String
    val HTMLHeaders: String
    val chatContext: ChatContext
    init{
        chatContext = context
        HTMLHeaders = "<head>\n" +
                "    <title>TestWebpage</title>\n" +
                "    <link rel=\"stylesheet\" href=\"card_view.css\">\n" +
                "</head>"
        HTMLBody = "<body>"
    }

    fun addMessage(payload: DataPacket.Payload): String{
        if(payload is ChatContext) {
            val author = payload.metaData.author
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val date = payload.metaData.time.toLocalDateTime().format(formatter)
            // Render the markdown message into HTML
            val messageBody = Processor.process(payload.lastChat)
            // Render in Div Tags
            val divClass: String
            if(author == GlobalContext.getInstance().mUserName){
                divClass = "local_message_card"
            } else{
                divClass = "external_message_card"
            }
            val divTag =
                    """
                            <div class="${divClass}">
                            <div class="container">
                              <h4><b>${author}</b></h4>
                              ${messageBody}
                              <h6><b>${date}</b></h6>
                            </div>
                          </div>
                          <div class = "spacing"></div>
                    """.trimIndent()
            // Append to current HTML body
            HTMLBody.plus(divClass)
            //Generate new HTML body to return
        }
        return HTMLHeaders.plus(HTMLBody).plus("</body>")
    }

    fun addMessageToWebview():String{
        val author = chatContext.chatName
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val date = chatContext.dateTimeOfLastMessage.toLocalDateTime().format(formatter)
        val body = Processor.process(chatContext.lastChat)
        val divClass: String
        if(author == GlobalContext.getInstance().mUserName){
            divClass = "local_message_card"
        } else{
            divClass = "external_message_card"
        }
        val divTag =
                """
                            <div class="${divClass}">
                            <div class="container">
                              <h4><b>${author}</b></h4>
                              ${body}
                              <h6><b>${date}</b></h6>
                            </div>
                          </div>
                          <div class = "spacing"></div>
                    """.trimIndent()
        // Append to current HTML body
        HTMLBody.plus(divClass)
        //Generate new HTML body to return
        return HTMLHeaders.plus(HTMLBody).plus("</body>")
    }
}