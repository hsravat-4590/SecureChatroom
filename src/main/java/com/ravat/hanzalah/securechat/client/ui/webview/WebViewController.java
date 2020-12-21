package com.ravat.hanzalah.securechat.client.ui.webview;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.ravat.hanzalah.securechat.client.context.ChatContext;
import com.ravat.hanzalah.securechat.client.context.ChatListener;
import com.ravat.hanzalah.securechat.client.context.GlobalContext;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;


/**
 * Class that controls the Webview for the ChatEngine
 */
public class WebViewController implements ChatListener.MessageRecievedListener,ChatListener.MessageSentListener {
    @FXML
    WebView chatWebView;
    @FXML
    JFXButton chatSend;
    @FXML
    JFXTextArea chatField;
    private WebEngine engine;
    private WebviewRenderer mRenderer;
    private ChatContext context;
    public WebViewController(ChatContext context){
        mRenderer = new WebviewRenderer(context);
        this.context = context;
    }
    @FXML
    private void initialize(){
        System.out.println("Iniit");
        engine = chatWebView.getEngine();
        chatWebView.setContextMenuEnabled(false);
        engine.setUserStyleSheetLocation(getClass().getResource("/styles/css/card-view.css").toString());
        engine.loadContent("<p>Chats Loading... This Message will dissapear once a chat is sent</p>");
    }

    @FXML
    public void sendClicked(){
        // Do Nothing
        GlobalContext.getInstance().getCurrentChat().updateChatBuffer(chatField.getText(),false);
    }

    private void onMessageAction(){
        String newHTML = mRenderer.addMessageToWebview();
        engine.loadContent(newHTML);
    }
    @Override
    public void onMessageRecieved() {
        onMessageAction();
    }

    @Override
    public void onMessageSent() {
        onMessageAction();
    }
}
