/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.controller;

import hr.algebra.handler.MovementHandler;
import hr.algebra.model.Ball;
import hr.algebra.model.Paddle;
import hr.algebra.model.helper.TimelineExtensions;
import hr.algebra.resources.Configurations;
import hr.algebra.serializable.GameStat;
import hr.algebra.udp.unicast.ClientThread;
import hr.algebra.udp.unicast.ServerThread;
import hr.algebra.utilities.AlertUtils;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * FXML Controller class
 *
 * @author Atlas Comic
 */
//https://stackoverflow.com/questions/15964832/javafx-1-fxml-file-with-multiple-different-controllers
public class MultiplayerController implements Initializable {

    // <editor-fold defaultstate="collapsed" desc="Variables">
    public int POSITION=-1;
    private Document xmlDocument;
    private Element rootElement;
    
    private GameStat game;
    private Timeline timeline;
    private Timeline pauseTime;
    private static final int MAX_SCORE=2;
    public static final int TIMELINE_DURATION = 50;
    
    
    // <editor-fold defaultstate="collapsed" desc="Network var">
    private ServerThread serverL;
    private ClientThread clientL;
    private ServerThread serverR;
    private ClientThread clientR;   
   
    private final String HOST_L = "clienthost";
    private final int PORT_L = 12355;
    private final String HOST_R = "localhost";
    private final int PORT_R = 12345;
    
    //incase of windows_update: 
    // Control Panel\Network and Internet\Network Connections
    // %WINDIR%\System32\Drivers\Etc\Hosts notepad
    // </editor-fold>
    
    @FXML
    private Pane PlayingField;
    @FXML
    private Ball ball;
    @FXML
    private Paddle padL;
    @FXML
    private Paddle padR;
    @FXML
    private Label lbPause;
    @FXML
    private Label lbLeft, lbRight;

    // </editor-fold>

    @Override
    public void initialize(URL url, ResourceBundle rb) {
       game=new GameStat(ball.getCenterX(),ball.getCenterY());
       
       initXmlDoc();
       initNetworkThreads();
       Platform.runLater(()->StartThreads());
       
        timeline = new Timeline(new KeyFrame(Duration.millis(TIMELINE_DURATION), e -> {
                //Input
                checkInput();
                //physics updates
                padL.updatePosition();
                padR.updatePosition();                
                ball.moveBall(PlayingField);
                //check boundaries
                checkGameBounds();
        }));
        
        pauseTime = new Timeline(new KeyFrame(Duration.millis(TIMELINE_DURATION), e -> {pauseClickCheck(false);}));
        
        timeline.setCycleCount(Timeline.INDEFINITE);
        pauseTime.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        pauseTime.play();
        SetGameplaySpeed(GameStat.GAME_SPEED);
        SetListeners(); 
    }    

    //<editor-fold defaultstate="collapsed" desc="GameBounds">
    private void checkGameBounds() {
        //Paddles can't go beyond PlayingField bounds
        PaddleBoundary(padL);
        PaddleBoundary(padR);
        //Paddle and ball contact
        checkPaddle(padL);
        checkPaddle(padR);
        //If ball hit
        ChangeScore();
        XMLRecordInput();
    }

    private void checkPaddle(Paddle pad) {
        if (PaddelContact(pad)) {
            ball.setDx(ball.getDx() * -1);
            TimelineExtensions.increaseSpeed(timeline, ball.getBALL_ACCELERATION());
            //System.out.println("BOING");
        }

    }

    private boolean PaddelContact(Paddle pad) {
        double ballLeft = ball.getCenterX() - ball.getRadius(); // 47 - 20 = 27
        double ballRight = ball.getCenterX() + ball.getRadius(); // 754 + 20 = 774
        double ballTop = ball.getCenterY() - ball.getRadius(); // 215 - 20 = 195
        double ballBottom = ball.getCenterY() + ball.getRadius(); // 55 + 20 = 75

        double padLeft = pad.getX(); // =774
        double padRight = pad.getX() + pad.getWidth(); // 2 + 20 = 27
        double padTop = pad.getY(); // =75
        double padBottom = pad.getY() + pad.getHeight(); // 75 + 120 = 195

        boolean TopBottom = padTop <= ballBottom && padBottom >= ballTop;
        boolean ContactLeft = (ballLeft == padRight) && TopBottom;
        boolean ContactRight = (ballRight == padLeft) && TopBottom;

        return ContactLeft || ContactRight;
    }

    private void PaddleBoundary(Paddle pad) {
        double bounceStrength = 1;
        if (pad.getY() < 0) {
            pad.setY(0);
        } else if (pad.getY() > PlayingField.getHeight() - pad.getHeight()) {
            pad.setY(PlayingField.getHeight() - pad.getHeight());
        }
        pad.vy = pad.vy * -bounceStrength;
    }
    // </editor-fold> 

    //<editor-fold defaultstate="collapsed" desc="Inputs and Network">
    private void checkInput() {
        switch (POSITION) {
            case 0:
                enabledLeft();  
                clientL.setPADDLE(padL);
                networkInput(padR, serverR);
                break;
            case 1:
                enabledRight();
                clientR.setPADDLE(padR);
                networkInput(padL, serverL);                
                break;
            default:
                lbPause.setText("Non-existant \n position!");
                pauseClickCheck(true);
                break;
        }
        //pauseClickCheck(false);
    }
    
    private void enabledLeft() {
        if (MovementHandler.isDownPressedL() && !MovementHandler.isUpPressedL()) {
            padL.moveDownward();
        } else if (MovementHandler.isUpPressedL() && !MovementHandler.isDownPressedL()) {
            padL.moveUpward();
        } else {
            padL.slowDown();
        } 
    }

    private void enabledRight() {
        if (MovementHandler.isDownPressedR() && !MovementHandler.isUpPressedR()) {
            padR.moveDownward();
        } else if (MovementHandler.isUpPressedR() && !MovementHandler.isDownPressedR()) {
            padR.moveUpward();
        } else {
            padR.slowDown();
        }
    }
    
    private void initNetworkThreads(){  
        serverR = new ServerThread(HOST_R, PORT_R, padR);
        clientR = new ClientThread(HOST_R, PORT_R, padR);
        serverL = new ServerThread(HOST_L, PORT_L, padL);
        clientL = new ClientThread(HOST_L, PORT_L, padL);
     
        serverL.setName("left");
        serverR.setName("right");
    }
    
    private void StartThreads(){

        switch (POSITION) {
            case 0:
                clientL.start();
                serverR.start();  
                break;
            case 1:
                clientR.start();
                serverL.start();
                break;
            default:
                lbPause.setText("Non-existant \n position!");
                pauseClickCheck(true);
                break;
        }
    }
    
    private void StopThreads() {

        switch (POSITION) {
            case 0:
                serverR.terminate();
                clientL.terminate();
                break;
            case 1:
                clientR.terminate();
                serverL.terminate();
                break;
            default:
                lbPause.setText("Non-existant \n position!");
                pauseClickCheck(true);
                break;
        }
        System.out.println("Threads have been terminated");
    }
      
    private void networkInput(Paddle changepad,Thread netThread) {
        double Y=0;
        switch (netThread.getClass().getSimpleName()) {
            case "ServerThread":
                Y=((ServerThread) netThread).getY();
                break;
            case "ClientThread":
                Y=((ClientThread) netThread).getY();
                break;
            default:
                System.out.println("Network thread isnt instanceof ServerThread or ClientThread");
        }
        changepad.setY(Y);  
        //System.out.println(changepad);
    } 

    private void SetGameplaySpeed(int i) {
        for (int j = 0; j < i; j++) {
            TimelineExtensions.increaseSpeed(timeline, ball.getBALL_ACCELERATION());
        }
    }

    // </editor-fold>   

    //<editor-fold defaultstate="collapsed" desc="Score">
    private void ChangeScore() {
        int isHit = ball.getHit();
        if (isHit != 0 && !(isHit > 1) && !(isHit < -1)) {
            //return to start
            ball.setCenterX(game.getStart_posX());
            ball.setCenterY(game.getStart_posY());

            if (isHit > 0) { SetScore(lbLeft, 1); } 
            else { SetScore(lbRight, 1); }
            
            if (checkScore(lbLeft)) {
                endGame("Left \n Won",5);
            }
            else if (checkScore(lbRight)) {
                endGame("Right \n Won", 5);
            }
            ball.setHit(0);
        }
    }
    
    private boolean checkScore(Label label) 
    { 
        return Integer.parseInt(GetScore(label))>=MAX_SCORE;    
    }

    private String GetScore(Label label) {
        return label.getText();
    }

    private void SetScore(Label label, Integer num) {
        int point = (Integer.parseInt(label.getText()) + num);
        label.setText(Integer.toString(point));
    }
   
    // </editor-fold> 

    //<editor-fold defaultstate="collapsed" desc="Setup Components">
    public void setPOSITION(int POSITION) {
        this.POSITION = POSITION;
    }
    // </editor-fold>  

    //<editor-fold defaultstate="collapsed" desc="Listeners">
    private void SetListeners() {

        PlayingField.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                // scene is set for the first time. Now its the time to listen stage changes.
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        // stage is set. now is the right time to do whatever we need to the stage in the controller.
                        Stage stage = (Stage) newWindow;
                        stage.setOnCloseRequest(e -> {                           
                            System.exit(0);
                        });
                        stage.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> onKeyPressed(event));
                    }
                });
            }
        });
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        //Pause game
        switch (keyEvent.getCode()) {
            case ESCAPE: {
                switch (POSITION) {
                    case 0: serverL.setPauseGame(!serverL.isPauseGame()); break;
                    case 1: serverR.setPauseGame(!serverR.isPauseGame()); break;
                }
                pauseGame();
//                System.out.println("ESC Pause is " + serverL.isPauseGame() + POSITION);
            }
            break;
            default: {
            }
        }
    }
    
    private void pauseGame(){
        boolean pauseActive = !lbPause.visibleProperty().get();
        if (pauseActive) {
            PlayingField.setOpacity(0.5);
            timeline.stop();
        } else {
            PlayingField.setOpacity(1);
            timeline.play();
        }
              
        lbPause.setVisible(pauseActive);
    }
    
    // </editor-fold> 

    //<editor-fold defaultstate="collapsed" desc="Change to chatRoom">
    
    private void endGame(String text, int time) {
        lbPause.setText(text);
        pauseGame();
        StopThreads();
        createXmlDoc();
        PauseTransition delay = new PauseTransition(Duration.seconds(time));
        delay.setOnFinished(event->changeToChatRoom());
        delay.play();
    }

    private void pauseClickCheck(boolean pauseAnyway) {
        if (pauseAnyway) {
            pauseGame();
            return;
        }
        
        if (serverL.isPauseGame()) {
            pauseGame();
           // System.out.println("After PauseGame is " + serverL.isPauseGame() + POSITION);
            switch (POSITION) {
                case 0:
                    serverL.setPauseGame(false);
                    break;
                case 1:
                    serverR.setPauseGame(false);
                    break;
            }                   
            //System.out.println("After SetPause is " + serverL.isPauseGame() + POSITION);
            System.out.println("------------------------");
        }
    }   
    
    private void changeToChatRoom() {
        Stage stage = (Stage) PlayingField.getScene().getWindow();
        try {
            startChange(stage, Configurations.CHAT_PATH);
        } catch (Exception ex) {
            Logger.getLogger(GameUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Yellow");
    }

    public void startChange(Stage window, String path) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(path));
        Parent root = (Parent) fxmlLoader.load();
        ChatRoomController controller = fxmlLoader.<ChatRoomController>getController();

        Scene scene = new Scene(root);

        switch (POSITION) {
            case 0:
                controller.setUsername("Player 1");
                break;
            case 1:
                controller.setUsername("Player 2");
                break;
        }
        if (POSITION!=-1) window.setTitle("ChatRoomApp " + (POSITION + 1));
        else window.setTitle("ChatRoomApp " + "Guest");
        
        window.setScene(scene);
        window.setOnCloseRequest(e -> System.exit(0));
        window.show();
    }

    // </editor-fold> 
  
    //<editor-fold defaultstate="collapsed" desc="XML Recorder">
    
    private void initXmlDoc(){
        DocumentBuilderFactory documentBuilderFactory
                = DocumentBuilderFactory.newInstance();
        try {
            xmlDocument = documentBuilderFactory.newDocumentBuilder().newDocument();
            rootElement = xmlDocument.createElement("Pong");
            xmlDocument.appendChild(rootElement);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void XMLRecordInput() {         
        recordPaddleXML(padL);
        recordPaddleXML(padR);
    }
    
    private void recordPaddleXML(Paddle pad){
        String value=String.valueOf(pad.getY());

        Node vyNode = xmlDocument.createTextNode(value);
        Element strengthAmount = xmlDocument.createElement(pad.getId());
        strengthAmount.appendChild(vyNode);
        rootElement.appendChild(strengthAmount);
        System.out.println(vyNode.toString() + " - "+ value);
    }
    
    private void createXmlDoc(){
        try {            
            Transformer transformer
                    = TransformerFactory.newInstance().newTransformer();
            
            Source xmlSource = new DOMSource(xmlDocument);
            Result xmlResult = new StreamResult(new File("gameRecord.xml"));

            transformer.transform(xmlSource, xmlResult);

            System.out.println("Paddle record created successfuly!");
            //AlertUtils.infoBox("Paddle record created successfuly!", "XML created", "Information dialog");
        } catch (TransformerException ex) {
            Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // </editor-fold> 
 
  
}
