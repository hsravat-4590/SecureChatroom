package com.ravat.hanzalah.securechat.client.ui;

import com.jfoenix.controls.JFXButton;
import com.ravat.hanzalah.securechat.client.context.GlobalContext;
import com.ravat.hanzalah.securechat.client.ui.webview.WebViewController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainActivity implements Surface {
    private Stage mStage;

    @FXML
    Label contextLabel;
    @FXML
    JFXButton settingsButton;
    @FXML AnchorPane contextViewer;
    @FXML AnchorPane mainActivity;

    public MainActivity(Stage stage){
        mStage = stage;
    }

    @FXML
    public void initialize(){
        // Load ContextViewer and WebView for now
        //contextLabel.setText("Secure Chat");
        loadContextViewer();
        loadChatView();
        SurfaceFlinger.getInstance().addSurface(this);
    }

    private void loadContextViewer() {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/layout/chats.fxml"));
            fxmlLoader.setController(new ContextController());
            AnchorPane loaded = fxmlLoader.load();
            contextViewer.getChildren().setAll(loaded);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    private void loadChatView(){
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/layout/chat_view.fxml"));
            fxmlLoader.setController(new WebViewController());
            AnchorPane loaded = fxmlLoader.load();
            mainActivity.getChildren().setAll(loaded);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void render(){
        if(GlobalContext.getInstance().getCurrentChat() != null) {
            contextLabel.setText(GlobalContext.getInstance().getCurrentChat().chatName);
        }
    }
}
