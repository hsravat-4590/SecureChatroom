package com.ravat.hanzalah.securechat;
import com.ravat.hanzalah.securechat.client.ui.UILauncher;
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
public class Main{

    public static void main(String[] args){
        if(args.length == 0){
            UILauncher.main(args);
        } else {
            // Launch CLI Tool For Server application access
        }
    }
}
