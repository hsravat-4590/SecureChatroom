package com.ravat.hanzalah.securechat.client.ui.webview;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;


/**
 * Class that controls the Webview for the ChatEngine
 */
public class WebViewController {
    @FXML
    WebView chatWebView;
    @FXML
    JFXButton chatSend;
    @FXML
    JFXTextArea chatField;

    public WebViewController(){

    }
    @FXML
    public void initalize(){

    }

    @FXML
    public void sendClicked(){
        // Do Nothing
    }
}
