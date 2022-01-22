/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.controller;

import hr.algebra.model.Ball;
import hr.algebra.model.Paddle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Atlas Comic
 */
public class ReplayController implements Initializable {

    @FXML
    private AnchorPane mainPane;
    @FXML
    private Pane PlayingField;
    @FXML
    private Paddle padR;
    @FXML
    private Paddle padL;
    @FXML
    private Ball ball;
    @FXML
    private Label lbRight;
    @FXML
    private Label lbLeft;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
