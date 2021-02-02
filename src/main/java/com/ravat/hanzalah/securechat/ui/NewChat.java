/* 
 *  MIT License
 *  
 *  Copyright (c) 2020-2021 Hanzalah Ravat
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.ravat.hanzalah.securechat.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import com.ravat.hanzalah.securechat.GlobalContext;
import com.ravat.hanzalah.securechat.Main;
import com.ravat.hanzalah.securechat.net.AddressInfo;
import com.ravat.hanzalah.securechat.net.Client;
import com.ravat.hanzalah.securechat.net.server.ServerController;
import com.ravat.hanzalah.securechat.se.SEMain;
import com.ravat.hanzalah.securechat.se.net.server.SEServerController;
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
    private volatile ServerController serverController;
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
        //Check if self-host is active. if so, create a chat server daemon process
        if(selfHost){
            final Thread serverDaemonThread = new Thread(() -> {
                // Loopback to main w/port number
                try {
                    if(SEMain.isSEModeRunning()){
                        serverController = new SEServerController(port);
                    } else {
                        serverController = new ServerController(port);
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
            serverDaemonThread.start();
        }
        AtomicBoolean connectionSuccessful = new AtomicBoolean(true);
        Platform.runLater(() ->{
           // Try connecting here
            GlobalContext.ContextFactory.createNewContext(usernameField.getText());
                try {
                    GlobalContext.getInstance().createChatClient(new AddressInfo(hostnameField.getText(),port),chatName);
                    System.out.println("Set the ChatClient in the Global Context");
                } catch (IOException exception) {
                    errorLabel.setText("ERROR: Unable to connect to host on the specified port");
                    exception.printStackTrace();
                    connectionSuccessful.set(false);
                }
                if(connectionSuccessful.get()){
                    // Create a chat window to replace this one
                    try{
                        final FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/layout/ChatView.fxml"));
                        AnchorPane content = loader.load();
                        mContextSwitcher.changeContext(content);
                        String appBarText = chatName;
                        if(selfHost){ appBarText = chatName + "@"+serverController.getServerAddress()+":"+portField.getText();}
                        else{appBarText = chatName + "@"+hostnameField.getText()+":"+portField.getText();}
                        appBarNameSwitcher.changeAppTitle(appBarText);
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
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
