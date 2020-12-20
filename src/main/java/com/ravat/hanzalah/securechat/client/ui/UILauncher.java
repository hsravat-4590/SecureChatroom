package com.ravat.hanzalah.securechat.client.ui;

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class UILauncher extends Application{

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage){
        try{
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/layout/skeleton.fxml"));
            loader.setController(new MainActivity(stage));
            Parent content = (Parent) loader.load();
            stage.setResizable(false);
            Scene scene = new Scene(content);
            stage.setScene(scene);
            stage.show();
        } catch(IOException ex){
            ex.printStackTrace();
        }
    }

}
