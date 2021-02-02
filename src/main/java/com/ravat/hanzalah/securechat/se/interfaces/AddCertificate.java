/* 
 *  MIT License
 *  
 *  Copyright (c) 2021-2021 Hanzalah Ravat
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

package com.ravat.hanzalah.securechat.se.interfaces;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;

public class AddCertificate {


    public interface CertificateAcceptor{
        void onAccept();
        void onDecline();
        void onJustOnce();
    }

    public static void addCertDiag(CertificateAcceptor handler){
        JFXDialogLayout content=  new JFXDialogLayout();
        content.setHeading(new Text("SecureChat: Add Certificates"));
        content.setBody(new Text("Do you wish to add this server's TLS Certificate to the TrustedKeyStore for future connections?"));
        StackPane stackPane = new StackPane();
        JFXDialog diag = new JFXDialog(stackPane,content,JFXDialog.DialogTransition.CENTER);
        JFXButton acceptButton = new JFXButton("Yes");
        JFXButton declineButton = new JFXButton("No");
        JFXButton justOnceButton = new JFXButton("Just Once");

        acceptButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handler.onAccept();
                diag.close();
            }
        });
        declineButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handler.onDecline();
                diag.close();
            }
        });
        justOnceButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handler.onJustOnce();
                diag.close();
            }
        });
        content.setActions(Arrays.asList(acceptButton,declineButton,justOnceButton));

        Scene scene = new Scene(stackPane,300,250);
        Stage diagStage =new Stage();
        diagStage.setScene(scene);
        diagStage.show();
        diag.show();
    }
}
