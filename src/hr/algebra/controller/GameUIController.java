/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.controller;

import hr.algebra.resources.BallPane;
import hr.algebra.handler.MovementHandler;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
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
    
    private static final String SINGLEPLAYER_PATH="/hr/algebra/view/Singleplayer.fxml";
    private static final String MULTIPLAYER_PATH="/hr/algebra/view/Multiplayer.fxml";
    
    @FXML
    private AnchorPane mainPane;
    
    @FXML
    private BallPane ball;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //ball.pause();
    }
    
    @FXML
    private void btnSinglePlayerClick()
    {
        OpenWindow(SINGLEPLAYER_PATH);
    } 
    
    @FXML
    private void btnMultiPlayerClick() {
        OpenWindow(MULTIPLAYER_PATH);
    }
    
    @FXML
    private void btnExitClick()
    {
        Platform.exit();
    }

    private void OpenWindow(String path) {
        Stage stage = (Stage) mainPane.getScene().getWindow();
        try {
            start(stage, path);
        } catch (Exception ex) {
            Logger.getLogger(GameUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Yellow");
    }
    
    public void start(Stage window, String path) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(path));
        Scene scene = new Scene(root);
        scene.setOnKeyPressed(MovementHandler.getMove());
        scene.setOnKeyReleased(MovementHandler.getStand());
        window.setScene(scene);
        //window.setResizable(true);
        window.show();
    }
    
}


