package com.ravat.hanzalah.securechat.ui;

import com.jfoenix.controls.JFXButton;
import com.ravat.hanzalah.securechat.se.SEMain;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainActivity extends Application {
    private Scene mScene;
    @FXML private AnchorPane contentAnchor;
    @FXML private JFXButton newChatButton;
    @FXML private AnchorPane appBar;
    @FXML private ImageView encryptionStatusView;
    public static void main(String[] args){
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        try{
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/layout/MainActivity.fxml"));
            Parent content = loader.load();
            primaryStage.setResizable(false);
            mScene = new Scene(content);
            primaryStage.setScene(mScene);
            mScene.getStylesheets().add("https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&display=swap");
            //primaryStage.setWidth(750);
            //primaryStage.setHeight(690);
            primaryStage.show();
            System.out.println(primaryStage.getWidth() + "+ " + primaryStage.getHeight());

        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
    public void initialize(){
        try{
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/layout/NewChat.fxml"));
            AnchorPane content = loader.load();
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

    public void changeContext(AnchorPane contentAnchor){
        this.contentAnchor.getChildren().addAll(contentAnchor);
    }

    public void changeAppTitle(String newTitle){
        Label appBarLabel = (Label) appBar.getChildren().get(0);
        appBarLabel.setText(newTitle);
        reflectSEStatus();
    }

    public void reflectSEStatus(){
        if(SEMain.isSEModeRunning()) {
            encryptionStatusView.setImage(new Image(getClass().getResource("/mipmap/encryption_black_18dp.png").toString()));
        } else{
            encryptionStatusView.setImage(new Image(getClass().getResource("/mipmap/no_encryption_black_18dp.png").toString()));
        }
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
