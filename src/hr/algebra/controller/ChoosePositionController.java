/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.controller;

import hr.algebra.handler.MovementHandler;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void leftPlayer(ActionEvent event) {
        pauseGame();
        //ChangeCurrentWindow(MULTIPLAYER_PATH, 0);
    }

    @FXML
    private void rightPlayer(ActionEvent event) {
        pauseGame();
        //ChangeCurrentWindow(MULTIPLAYER_PATH, 1);
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
            //window.show();            
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
