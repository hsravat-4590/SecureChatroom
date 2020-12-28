package com.ravat.hanzalah.securechat.ui;

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainActivity extends Application {
    private Scene mScene;
    @FXML private AnchorPane contentAnchor;
    @FXML private JFXButton newChatButton;
    @FXML private AnchorPane appBar;
    public static void main(String args[]){
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        try{
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/layout/MainActivity.fxml"));
            Parent content = (Parent) loader.load();
            primaryStage.setResizable(false);
            mScene = new Scene(content);
            primaryStage.setScene(mScene);
            primaryStage.show();
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
    public void initialize(){
        try{
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/layout/NewChat.fxml"));
            AnchorPane content = (AnchorPane) loader.load();
            NewChat newChat = loader.getController();
            newChat.setContextSwitcher(this::changeContext);
            newChat.setAppBarNameSwitcher(this::changeAppTitle);
            contentAnchor.getChildren().setAll(content);
            Label appBarLabel = (Label) appBar.getChildren().get(0);
            appBarLabel.setText("New Chat");
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void onNewChatButtonClicked(){

    }
    public void changeContext(AnchorPane contentAnchor){
        this.contentAnchor.getChildren().addAll(contentAnchor);
    }

    public void changeAppTitle(String newTitle){
        Label appBarLabel = (Label) appBar.getChildren().get(0);
        appBarLabel.setText(newTitle);
    }
    @FunctionalInterface
    public interface ContextSwitcher {
        void changeContext(AnchorPane newContext);
    }

    @FunctionalInterface
    public interface AppBarNameSwitcher{
        void changeAppTitle(String newTitle);
    }
}
