/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.controller;

import hr.algebra.handler.MovementHandler;
import hr.algebra.tcp.TCP_ClientThread;
import hr.algebra.tcp.TCP_ServerThread;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Atlas Comic
 */
public class ChoosePositionController implements Initializable {

    private static final String MULTIPLAYER_PATH="/hr/algebra/view/Multiplayer.fxml";
    
    @FXML
    private Label lbChoice;
    @FXML
    private GridPane positionPane;
    @FXML
    private Label lbPause;
    @FXML
    private Pane waitingPane;

    private TCP_ServerThread server;
    private TCP_ClientThread client;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        server=new TCP_ServerThread();
        client=new TCP_ClientThread();
    }    

    @FXML
    private void leftPlayer(ActionEvent event) {
        pauseGame();
        server.start();
        try {
            server.join(2000);
            ifConnectedStart("GAME START LEFT", 0);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChoosePositionController.class.getName()).log(Level.SEVERE, null, ex);
            pauseGame();
        }        
    }

    @FXML
    private void rightPlayer(ActionEvent event) {
        pauseGame();
        client.start();
        try {
            client.join(2000);
            ifConnectedStart("GAME START RIGHT", 1);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChoosePositionController.class.getName()).log(Level.SEVERE, null, ex);
            pauseGame();
        }
    }
    
    private void ifConnectedStart(String message, int pos) {
        System.out.println(message);
        ChangeCurrentWindow(MULTIPLAYER_PATH, pos);
    }
    
    private void ChangeCurrentWindow(String path, int pos) {
        Stage stage = (Stage) lbChoice.getScene().getWindow();
        try {
            start(stage, path, pos);
        } catch (Exception ex) {
            Logger.getLogger(ChoosePositionController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void start(Stage window, String path, int pos) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(path));
        Parent root = (Parent) fxmlLoader.load();
        MultiplayerController controller = fxmlLoader.<MultiplayerController>getController();
        
        if (pos>-1 && pos<2) {
            controller.setPOSITION(pos);
            Scene scene = new Scene(root);
            scene.setOnKeyPressed(MovementHandler.getMove());
            scene.setOnKeyReleased(MovementHandler.getStand());
            window.setScene(scene);
            window.show();            
        }
        else System.out.println("Error choosen position out of scope");
    }
    
    private void pauseGame() {
        boolean pauseActive = !waitingPane.visibleProperty().get();
        if (pauseActive) {
            positionPane.setOpacity(0.5);
        } else {
            positionPane.setOpacity(1);
        }
        waitingPane.setVisible(pauseActive);
        waitingPane.setDisable(!pauseActive);
    }
    
}
