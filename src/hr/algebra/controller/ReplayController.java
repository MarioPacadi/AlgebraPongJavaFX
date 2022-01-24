/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.controller;

import hr.algebra.model.Ball;
import hr.algebra.model.Paddle;
import hr.algebra.model.helper.TimelineExtensions;
import hr.algebra.resources.Configurations;
import hr.algebra.serializable.GameStat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * FXML Controller class
 *
 * @author Atlas Comic
 */
public class ReplayController implements Initializable {

    //<editor-fold defaultstate="collapsed" desc="Variables">
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
    
    private Timeline timeline;
    private String ReturnUsername;
    
    private List<Double> padLs;
    private List<Double> padRs;
    private List<Ball> balls;
    private List<GameStat> gamestats;
    // </editor-fold> 

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        initLists();
        if (detectXMLData()) readDataFromXML();
        else return;        
        
        timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            updateBallPosition(balls);
            updatePaddlePosition(padL,padLs);
            updatePaddlePosition(padR,padRs);
            updateScore(gamestats);
            checkIfReplayDone();
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        SetGameplaySpeed(GameStat.GAME_SPEED);
    } 
    
    private void initLists() {
        padLs = new ArrayList<>();
        padRs = new ArrayList<>();
        balls = new ArrayList<>();
    }
    
    private boolean detectXMLData() {
        File dir = new File(Configurations.XML_PADDLE);
        if (!dir.getParentFile().exists()) {
            dir.getParentFile().mkdirs(); //Create dir, if not exist
        }
        return dir.exists() && !dir.isDirectory();
    }
    
    //<editor-fold defaultstate="collapsed" desc="ReadDataFromXML">
    
    private void readDataFromXML() {
        try {
            InputStream inputStream = new FileInputStream(Configurations.XML_PADDLE);
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document xmlDocument = parser.parse(inputStream);

            String parentNodeName = xmlDocument.getDocumentElement().getNodeName();

            System.out.println("Parent node name: " + parentNodeName);
            
            fillLists(xmlDocument);
            
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            Logger.getLogger(ReplayController.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("File cannot be loaded");
        }
    }
    
    private void fillLists(Document xmlDocument){
        balls = fillListOfBalls(xmlDocument, ball);
        padLs = fillListOfPaddles(xmlDocument, padL);
        padRs = fillListOfPaddles(xmlDocument, padR);
        gamestats = fillListOfGameStats(xmlDocument);
    }
    
    private List<Ball> fillListOfBalls(Document xmlDocument, Ball ball) {
        List<Ball> listBalls = new ArrayList<>();
        NodeList ballNodes = xmlDocument.getElementsByTagName(ball.getId());
        for (int i = 0; i < ballNodes.getLength(); i++) {
            Node ballNode = ballNodes.item(i);
            if (ballNode.getNodeType() == Node.ELEMENT_NODE) {
                Element nodeElement = (Element) ballNode;

                Double x=readDoubleValue(nodeElement,"x");
                Double y = readDoubleValue(nodeElement, "y");
                Double radius = readDoubleValue(nodeElement, "radius");

                listBalls.add(new Ball(x, y, radius, ball.getFill().toString()));
            }
        }
        return listBalls;
    } 
    
    private List<Double> fillListOfPaddles(Document xmlDocument,Paddle pad) {
        List<Double> pads=new ArrayList<>();
        NodeList padNodes = xmlDocument.getElementsByTagName(pad.getId());
        for (int i = 0; i < padNodes.getLength(); i++) {
            Node padNode = padNodes.item(i);
            if (padNode.getNodeType() == Node.ELEMENT_NODE) {
                Element nodeElement = (Element) padNode;
                Double value=Double.valueOf(nodeElement.getTextContent().trim());
                pads.add(value);
            }
        }
        return pads;
    }
    
    private List<GameStat> fillListOfGameStats(Document xmlDocument) {
        List<GameStat> listStats = new ArrayList<>();
        NodeList scoreNodes = xmlDocument.getElementsByTagName("score");
        for (int i = 0; i < scoreNodes.getLength(); i++) {
            Node scoreNode = scoreNodes.item(i);
            if (scoreNode.getNodeType() == Node.ELEMENT_NODE) {
                Element nodeElement = (Element) scoreNode;

                String left = readStringValue(nodeElement, lbLeft.getId());
                String right = readStringValue(nodeElement, lbRight.getId());

                listStats.add(new GameStat(left, right));
            }
        }
        return listStats;
    }
    
    private Double readDoubleValue(Element elements, String name) {
        return Double.valueOf(readStringValue(elements, name));
    }
    
    private String readStringValue(Element elements, String name) {
        return elements.getElementsByTagName(name)
                .item(0)
                .getTextContent();
    }
    // </editor-fold> 
  
    //<editor-fold defaultstate="collapsed" desc="UpdateData">
    
    private void updatePaddlePosition(Paddle pad, List<Double> padList) {
        if (!padList.isEmpty() && padList.size() > 0) {
            Double temp=padList.get(0);
            SetupPaddle(pad, temp);
            padList.remove(temp);
        }
    }
    
    private void updateBallPosition(List<Ball> listOfBalls) {
        if (!listOfBalls.isEmpty() && listOfBalls.size() > 0) {
            Ball temp = listOfBalls.get(0);
            SetupCustomBall(temp);
            listOfBalls.remove(temp);
        }
    }
    
    private void updateScore(List<GameStat> gameStats) {
        if (!gameStats.isEmpty() && gameStats.size() > 0) {
            GameStat temp = gameStats.get(0);
            SetupGameStats(temp);
            gameStats.remove(temp);
        }
    }
    
    private void checkIfReplayDone() {
        if (areListsEmpty()) {
            timeline.stop();
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event -> changeToChatRoom());
            delay.play();
        }
    }
    
    private boolean areListsEmpty() {
        return balls.isEmpty() && padLs.isEmpty() && padRs.isEmpty() && gamestats.isEmpty();
    }
    // </editor-fold> 
    
    //<editor-fold defaultstate="collapsed" desc="Setup Components">

    private void SetupCustomBall(Ball customBall) {
        ball.setCenterX(customBall.getCenterX());
        ball.setCenterY(customBall.getCenterY());
    }
    
    private void SetupPaddle(Paddle paddle,Double y) {
        paddle.setY(y);
    }

    private void SetupGameStats(GameStat gameStat) {
        SetScore(lbLeft, gameStat.getLeftScore());
        SetScore(lbRight, gameStat.getRightScore());
    }
    
    private void SetScore(Label label, String num) {
        label.setText(num);
    }
    
    private void SetGameplaySpeed(int i) {
        for (int j = 0; j < i; j++) {
            TimelineExtensions.increaseSpeed(timeline, ball.getBALL_ACCELERATION());
        }
    }    
    // </editor-fold> 
    
    //<editor-fold defaultstate="collapsed" desc="Return to ChatRoom">
    private void changeToChatRoom() {
        Stage stage = (Stage) PlayingField.getScene().getWindow();
        try {
            startChange(stage, Configurations.CHAT_PATH, ReturnUsername);
        } catch (Exception ex) {
            Logger.getLogger(GameUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Yellow");
    }

    public void startChange(Stage window, String path, String username) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(path));
        Parent root = (Parent) fxmlLoader.load();
        ChatRoomController controller = fxmlLoader.<ChatRoomController>getController();
        System.out.println(controller.toString());

        Scene scene = new Scene(root);
        System.out.println(scene);

        controller.setUsername(username);

        window.setScene(scene);
        window.setOnCloseRequest(e -> System.exit(0));
        window.show();
    }
    
    public void setReturnUsername(String ReturnUsername) {
        this.ReturnUsername = ReturnUsername;
    }   
    // </editor-fold> 
   
}
