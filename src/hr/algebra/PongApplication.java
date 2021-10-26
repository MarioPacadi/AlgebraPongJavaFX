/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Mario
 */
public class PongApplication extends Application {

    public static final String START_SCENE = "view/GameUI.fxml";
    public static final String TITLE = "Algebra Pong";
    public static final String ICON="resources/Pong.png";
    public static final int WIDTH = 600, HEIGHT=400;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(START_SCENE));
        
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        
        stage.setTitle(TITLE);
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream(ICON)));
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> System.exit(0));
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
