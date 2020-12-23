package com.ravat.hanzalah.securechat.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import com.ravat.hanzalah.securechat.GlobalContext;
import com.ravat.hanzalah.securechat.net.AddressInfo;
import com.ravat.hanzalah.securechat.net.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;


public class NewChat {
    @FXML JFXTextField hostnameField;
    @FXML JFXTextField portField;
    @FXML JFXTextField chatNameField;
    @FXML JFXTextField usernameField;
    @FXML JFXButton connectButton;
    @FXML JFXProgressBar connectingProgressBar;
    @FXML Label errorLabel;
    @FXML GridPane formGridPane;
    @FXML JFXCheckBox selfHostCheck;
    private MainActivity.ContextSwitcher mContextSwitcher;
    private MainActivity.AppBarNameSwitcher appBarNameSwitcher;
    private boolean selfHost;
    @FXML
    private void initialize(){
        chatNameField = (JFXTextField) formGridPane.getChildren().get(0);
        usernameField = (JFXTextField) formGridPane.getChildren().get(1);
        hostnameField = (JFXTextField) formGridPane.getChildren().get(2);
        portField = (JFXTextField) formGridPane.getChildren().get(3);
    }
    @FXML
    public void onConnectAction(){
        errorLabel.setText("");
        connectingProgressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        String chatName;
        if(chatNameField.getText().equals("")){
            chatName = hostnameField.getText();
        } else{
            chatName = chatNameField.getText();
        }
        if(usernameField.getText().equals("")){
            errorLabel.setText("ERROR: Please enter a username!");
            connectingProgressBar.setProgress(0);
            return;
        }
        if(!selfHost && (hostnameField.getText().equals("") || portField.getText().equals(""))){
            errorLabel.setText("ERROR: HostName or Port field is Empty");
            connectingProgressBar.setProgress(0);
            return;
        }
        if(selfHost && portField.getText().equals("")){
            errorLabel.setText("ERROR: Port field is Empty");
            connectingProgressBar.setProgress(0);
            return;
        }
        int port;
        try {
            port = Integer.parseInt(portField.getText());
        } catch (NumberFormatException ex){
            errorLabel.setText("Error: Port Value must be a number");
            connectingProgressBar.setProgress(0);
            return;
        }
        if(port < 0){
            errorLabel.setText("ERROR: Port value must be greater than zero");
            connectingProgressBar.setProgress(0);
            return;
        }
        AtomicBoolean connectionSuccessful = new AtomicBoolean(true);
        Platform.runLater(() ->{
           // Try connecting here
            GlobalContext.ContextFactory.createNewContext(usernameField.getText());
            if(!selfHost){
                try {
                    Client newClient = new Client(new AddressInfo(hostnameField.getText(),port));
                    GlobalContext.getInstance().setChatClient(newClient);
                    System.out.println("Set the ChatClient in the Global Context");
                } catch (IOException exception) {
                    errorLabel.setText("ERROR: Unable to connect to host on the specified port");
                    connectionSuccessful.set(false);
                }
                if(connectionSuccessful.get()){
                    // Create a chat window to replace this one
                    try{
                        final FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/layout/ChatView.fxml"));
                        AnchorPane content = (AnchorPane) loader.load();
                        mContextSwitcher.changeContext(content);
                        appBarNameSwitcher.changeAppTitle(chatName);
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
            } //Nothing yet for server stuff :-(
            connectingProgressBar.setProgress(0);
        });


    }

    public void onSelfHostCheckAction(){
        selfHost = selfHostCheck.isSelected();
        hostnameField.setDisable(selfHost);
    }

    public void setContextSwitcher(MainActivity.ContextSwitcher switcher){
        mContextSwitcher = switcher;
    }
    public void setAppBarNameSwitcher(MainActivity.AppBarNameSwitcher switcher){
        this.appBarNameSwitcher = switcher;
    }
}
