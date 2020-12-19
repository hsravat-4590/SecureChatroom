package com.ravat.hanzalah.securechat;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.logging.Logger;

/**
 * Main Entry Point of the Application in Client Mode and ServerMode, Albeit the server mode will not produce a GUI
 */
public class Main extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {

    }

    public static void main(String[] args){
        if(args.length == 0){
            launch(args);
        } else {
            // Launch CLI Tool For Server application access
        }
    }
}
