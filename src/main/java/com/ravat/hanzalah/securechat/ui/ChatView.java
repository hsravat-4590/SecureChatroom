/* 
 *  MIT License
 *  
 *  Copyright (c) 2021-2021 Hanzalah Ravat
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.ravat.hanzalah.securechat.ui;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.ravat.hanzalah.securechat.GlobalContext;
import com.ravat.hanzalah.securechat.net.Client;
import com.ravat.hanzalah.securechat.net.Packet;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class ChatView implements Client.ChatListener.InboundListener,Client.ChatListener.OutboundListener{
    @FXML private WebView chatWebView;
    private WebEngine webEngine;
    @FXML private JFXTextArea chatTextArea;
    @FXML private JFXButton sendButton;
    private Client chatClient;
    private WebviewRenderer webviewRenderer;
    //private WebviewRenderer renderer;

    @FXML
    private void initialize(){
        webEngine = chatWebView.getEngine();
        //chatWebView.setContextMenuEnabled(false);
        webEngine.setUserStyleSheetLocation(getClass().getResource("/styles/webview.css").toString());
        webEngine.loadContent("<p>Placeholder Message... This message will dissapear once a chat is sent or recieved</p>");
        chatTextArea.setText("Write your Message Here");
        chatClient = GlobalContext.getInstance().getChatClient();
        if(chatClient !=null){
            chatClient.addListener(this);
            webviewRenderer = new WebviewRenderer(chatClient);
        } else {
            System.err.println("Error: ChatClient has not been set in the Global Context");
        }
    }
    @FXML
    private void onSendAction(){
        // Do Send Stuff
        if(!(chatTextArea.getText().equals(""))) {
            chatClient.sendMessage(chatTextArea.getText());
            chatTextArea.setText("");
        }
    }

    private void onMessageActions(Packet.Payload payload){
        Platform.runLater(() -> {
            webEngine.setJavaScriptEnabled(true);
            int scrollVal = getVScrollValue();
            webEngine.loadContent(webviewRenderer.addMessageToWebview(payload));
            webEngine.executeScript("window.scrollTo(" + scrollVal + ", " + scrollVal + ")");
            webEngine.setJavaScriptEnabled(false);
        });

    }
    @Override
    public void onMessageRecieved(Packet.Payload payload) {
        onMessageActions(payload);
    }

    @Override
    public void onMessageSent(Packet.Payload payload) {
        onMessageActions(payload);
    }

    /**
     * Returns the vertical scroll value, i.e. thumb position.
     * This is equivalent to {@link javafx.scene.control.ScrollBar#getValue().
     * @param view
     * @return vertical scroll value
     */
    private int getVScrollValue() {
        return (Integer) webEngine.executeScript("window.scrollY");
    }
}
