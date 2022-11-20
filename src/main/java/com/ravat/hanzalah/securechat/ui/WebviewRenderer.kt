/* 
 *  /* 
 *   *  MIT License
 *   *  
 *   *  Copyright (c) 2021-2021 Hanzalah Ravat
 *   *  
 *   *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *   *  of this software and associated documentation files (the "Software"), to deal
 *   *  in the Software without restriction, including without limitation the rights
 *   *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   *  copies of the Software, and to permit persons to whom the Software is
 *   *  furnished to do so, subject to the following conditions:
 *   *  
 *   *  The above copyright notice and this permission notice shall be included in all
 *   *  copies or substantial portions of the Software.
 *   *  
 *   *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   *  SOFTWARE.
 *   */
 */


package com.ravat.hanzalah.securechat.ui

import java.time.format.DateTimeFormatter
import com.github.rjeschke.txtmark.*
import com.ravat.hanzalah.securechat.GlobalContext
import com.ravat.hanzalah.securechat.net.ChatPayload
import com.ravat.hanzalah.securechat.net.Client
import com.ravat.hanzalah.securechat.net.Packet
import com.ravat.hanzalah.securechat.net.server.ServerChatPayload
import java.util.ArrayList
import java.util.stream.Stream


class WebviewRenderer(chatClient: Client) {
    @Volatile private var HTMLBody = Array<String>(1) { "<body>" }
    @Volatile private var nextBodyIndex = 0
    private val HTMLHeaders: String = "<head>\n" +
            "    <title>TestWebpage</title>\n" +
            "    <link rel=\"stylesheet\" href=\"webview.css\">\n" +
            "<link rel=\"preconnect\" href=\"https://fonts.gstatic.com\">\n" +
            "<link href=\"https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&display=swap\" rel=\"stylesheet\">" +
            "</head>"
    private val client: Client = chatClient
    init{
        println("Initialised Webview Renderer")
    }

    fun addMessageToWebview(payload: Packet.Payload):String{
        //println("Renderer Invoked")
        when (payload.payload) {
            is ServerChatPayload -> {
                // It's a server message (right now that just means that someone has entered or left)
                val divClass = "message_stamp"
                val messageCause = payload.payload.messageTypes
                val message = when(messageCause){
                    ServerChatPayload.MessageTypes.USER_LEFT -> "${payload.payload.userName} has left the chat"
                    ServerChatPayload.MessageTypes.NEW_USER -> "${payload.payload.userName} has entered the chat"
                }
                val divTag=  """<div class= "message_stamp"> $message </div>"""
                addToBody(divTag)
            }
            is ChatPayload -> {
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
                    """.trimIndent()
                // Append to current HTML
                addToBody(divTag)
                //return HTMLHeaders.plus(HTMLBody).plus(divTag).plus("</body>")
            }
        }
        //Generate new HTML body to return
        //println("Generated: ${HTMLHeaders.plus(HTMLBody).plus("</body>")}")

        return HTMLHeaders.plus(HTMLBody.joinToString()).plus("</body>")
    }

    private fun addToBody(body:String){
        val index = nextBodyIndex++
        val list: MutableList<String> = HTMLBody.toMutableList()
        list.add(body)
        HTMLBody = list.toTypedArray()

    }

}