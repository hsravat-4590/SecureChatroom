package com.ravat.hanzalah.securechat.client.ui;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.ravat.hanzalah.securechat.client.context.ChatContext;
import com.ravat.hanzalah.securechat.client.context.ChatListener;
import com.ravat.hanzalah.securechat.client.context.GlobalContext;
import com.ravat.hanzalah.securechat.common.packets.DataPacket;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContextController{
    @FXML
    ScrollPane scrollPane;
    @FXML
    JFXTextField searchField;
    List<AnchorPane> chats;

    public ContextController(){
        chats = new ArrayList<>();
    }

    @FXML
    public void initialize(){
        addNewChat(
                new ChatContext("LocalHost","LocalHost",3020)
        );
    }

    public void addNewChat(ChatContext context){
        try{
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/layout/context_card.fxml"));
            loader.setController(new ChatCardController(context));
            AnchorPane loaded = loader.load();
            chats.add(loaded);
            renderContexts();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void renderContexts(){
        VBox root = new VBox();
        for (AnchorPane pane:
             chats) {
            root.getChildren().add(pane);
            root.setSpacing(10);
            root.setPadding(new Insets(10));
        }
        scrollPane.setContent(root);
    }

    public class ChatCardController implements ChatListener.MessageRecievedListener, ChatListener.MessageSentListener{
        @FXML
        JFXButton chatButton;
        @FXML
        Label chatTitle;
        @FXML Label chatDT;
        @FXML Label chatBody;

        private ChatContext context;
        public ChatCardController(ChatContext context){
            this.context = context;
            context.addListener(this);
        }
        public void initialize(){
            chatTitle.setText(context.chatName);

        }

        @FXML
        public void onChatClicked(){
            GlobalContext.getInstance().setCurrentContext(context);
        }

        @Override
        public void onMessageRecieved() {
            chatDT.setText(context.getDateTimeOfLastMessage());
            String lastChat = context.getLastChat();
            if(lastChat.length() > 100){
                chatBody.setText(lastChat.substring(0,100));
            } else{
                chatBody.setText(lastChat);
            }
        }

        @Override
        public void onMessageSent() {
            chatDT.setText(context.getDateTimeOfLastMessage());
            String lastChat = context.getLastChat();
            if(lastChat.length() > 100){
                chatBody.setText(lastChat.substring(0,100));
            } else{
                chatBody.setText(lastChat);
            }
        }
    }
}
