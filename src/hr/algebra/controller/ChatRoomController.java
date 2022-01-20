/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.controller;

import hr.algebra.contract.ChatMessage;
import hr.algebra.contract.MessengerService;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Atlas Comic
 */
public class ChatRoomController implements Initializable {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox chatBox;
    @FXML
    private TextArea txtMsg;
    @FXML
    private ListView listMessages;
    
    private String Username="Null user";
    
    private ObservableList<ChatMessage> messages=FXCollections.observableArrayList();

    private MessengerService server;  

    private Timeline refresher;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Registry registry = LocateRegistry.getRegistry();
            System.out.println("Registry retrieved!");
            server = (MessengerService) registry.lookup("MessengerService");
            System.out.println("Service retrieved!");
            listMessages.setItems(messages);
            initRefresher();
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(ChatRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }   

    private void initMessages() {
        try {
            messages.clear();
            server.getChatHistory().forEach(m -> messages.add(m));
        } catch (RemoteException ex) {
            Logger.getLogger(ChatRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private synchronized void initRefresher() {
        refresher = new Timeline(new KeyFrame(Duration.millis(1000), e -> {
            initMessages();
        }));
        refresher.setCycleCount(Timeline.INDEFINITE);
        refresher.play();
    }  

    @FXML
    private void clickSend(ActionEvent event) {
        try {
            String message=txtMsg.getText().trim();
            if (!message.isEmpty()) {
                ChatMessage newMessage = new ChatMessage(Username, message, LocalDateTime.now());
                server.sendMessage(newMessage);
                System.out.println(newMessage);
                messages.add(newMessage);
                refresh();
            }

        } catch (RemoteException ex) {
            Logger.getLogger(ChatRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }   

    private void refresh() {
        txtMsg.clear();
    }


    
}
