package com.ravat.hanzalah.securechat.ui;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.ravat.hanzalah.securechat.GlobalContext;
import com.ravat.hanzalah.securechat.net.Client;
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
        chatClient.sendMessage(chatTextArea.getText());
    }

    private void onMessageActions(){
        webEngine.loadContent(webviewRenderer.addMessageToWebview());
    }
    @Override
    public void onMessageRecieved() {
        onMessageActions();
    }

    @Override
    public void onMessageSent() {
        onMessageActions();
    }
}
