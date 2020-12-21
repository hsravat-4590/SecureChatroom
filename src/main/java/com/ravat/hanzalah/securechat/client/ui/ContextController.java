package com.ravat.hanzalah.securechat.client.ui;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.ravat.hanzalah.securechat.Main;
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


import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContextController{
    @FXML
    ScrollPane scrollPane;
    @FXML
    JFXTextField searchField;
    List<AnchorPane> chats;
    private MainActivity mainActivity;
    public ContextController(MainActivity mainActivity){
        chats = new ArrayList<>();
        this.mainActivity = mainActivity;
    }

    @FXML
    public void initialize(){
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
        scrollPane = new ScrollPane();
        scrollPane.setContent(root);
    }

    public class ChatCardController implements ChatListener.MessageRecievedListener, ChatListener.MessageSentListener {
        @FXML
        JFXButton chatButton;
        @FXML
        Label chatTitle;
        @FXML
        Label chatDT;
        @FXML
        Label chatBody;

        private ChatContext context;

        public ChatCardController(ChatContext context) {
            this.context = context;
            context.addListener(this);
        }

        public void initialize() {
            chatTitle.setText(context.chatName);

        }

        @FXML
        public void onChatClicked() {
            GlobalContext.getInstance().setCurrentContext(context);
            mainActivity.loadChatView(context);
        }

        private void onChatActivity() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            chatDT.setText(context.getDateTimeOfLastMessage().toLocalDateTime().format(formatter));
            String lastChat = context.getLastChat();
            if (lastChat.length() > 100) {
                chatBody.setText(lastChat.substring(0, 100));
            } else {
                chatBody.setText(lastChat);
            }
        }

        @Override
        public void onMessageRecieved() {
            onChatActivity();
        }

        @Override
        public void onMessageSent() {
            onChatActivity();
        }
    }
}
