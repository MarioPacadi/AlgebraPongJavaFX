/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.controller;

import hr.algebra.PongApplication;
import hr.algebra.contract.MessengerService;
import hr.algebra.contract.MessengerServiceImpl;
import hr.algebra.resources.BallPane;
import hr.algebra.handler.MovementHandler;
import hr.algebra.resources.Configurations;
import hr.algebra.server.ChatServer;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Mario
 */
public class GameUIController implements Initializable {
    
//    private static final String SINGLEPLAYER_PATH="/hr/algebra/view/Singleplayer.fxml";
//    private static final String MULTIPLAYER_PATH="/hr/algebra/view/Multiplayer.fxml";
//    private static final String POSITION_PATH = "/hr/algebra/view/ChoosePosition.fxml"; 
    
    @FXML
    private AnchorPane mainPane;
    
    @FXML
    private BallPane ball;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //ball.pause();
    }
    
    @FXML
    private void btnSinglePlayerClick() {
        ChangeCurrentWindow(Configurations.SINGLEPLAYER_PATH);
    } 
    
    @FXML
    private void btnMultiPlayerClick() {
        //Choose left or right
        ChangeCurrentWindow(Configurations.POSITION_PATH);
        try {
            startNewWindow(Configurations.POSITION_PATH);
        } catch (Exception ex) {
            System.out.println("New Window couldn't be started!");
        }
        startChatServer();
    }
    
    @FXML
    private void btnResponsiveClick(ActionEvent event) {
        ChangeCurrentWindow(Configurations.RESPONSIVE_PATH);
    }
    
    @FXML
    private void btnExitClick() {
        Platform.exit();
    }

    private void ChangeCurrentWindow(String path) {
        Stage stage = (Stage) mainPane.getScene().getWindow();
        try {
            startChange(stage, path);
        } catch (Exception ex) {
            Logger.getLogger(GameUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Yellow");
    }
    
    public void startChange(Stage window, String path) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(path));
        Scene scene = new Scene(root);
        scene.setOnKeyPressed(MovementHandler.getMove());
        scene.setOnKeyReleased(MovementHandler.getStand());
        window.setScene(scene);
        //window.setResizable(true);
        window.show();
    }
    
    public void startNewWindow(String path) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));

        Scene scene=new Scene(loader.load());
        scene.setOnKeyPressed(MovementHandler.getMove());
        scene.setOnKeyReleased(MovementHandler.getStand());
        
        Stage newWindow = new Stage();
        newWindow.setTitle("Client App");
        newWindow.setScene(scene);
        newWindow.getIcons().add((new PongApplication().getIcon()));
        newWindow.setOnCloseRequest(e -> System.exit(0));
        //window.setResizable(true);
        newWindow.show();
    }
    
    public void startChatServer(){
        System.out.println("Server started...");
        MessengerService server = new MessengerServiceImpl();
        try {
            MessengerService stub = (MessengerService) UnicastRemoteObject
                    .exportObject((MessengerService) server, 0);

            Registry registry = LocateRegistry.createRegistry(1099);

            System.out.println("RMI Registry created!");

            registry.rebind("MessengerService", stub);

            System.out.println("Service binded...");

        } catch (RemoteException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}


