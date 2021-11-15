/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.controller;

import hr.algebra.model.Ball;
import hr.algebra.model.Paddle;
import hr.algebra.model.helper.TimelineExtensions;
import hr.algebra.handler.MovementHandler;
import hr.algebra.model.helper.FileName;
import hr.algebra.serializable.GameStat;
import hr.algebra.utilities.AlertUtils;
import hr.algebra.utilities.ReflectionUtils;
import hr.algebra.utilities.SerializationUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Mario
 */
public class SingleplayerController implements Initializable,Serializable {

    private static final String DOCUMENTATION_FILENAME = "documentation.txt";
    private static final String CLASSES_PATH = "src/hr/algebra/model";
    private static final String CLASSES_PACKAGE
            = CLASSES_PATH.substring(CLASSES_PATH.indexOf("/") + 1).replace("/", ".").concat(".");
    
    // <editor-fold defaultstate="collapsed" desc="Variables">   
    
    private GameStat game;
    private Timeline timeline;
    
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
    private Label lbLeft,lbRight;
     
    // </editor-fold>

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        game=new GameStat(ball.getCenterX(),ball.getCenterY());

        timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> {
                //Input
                checkInput();
                botFollowBall(padR);
                //botFollowBall(padL);
                //physics updates
                padL.updatePosition();
                padR.updatePosition();                
                ball.moveBall(PlayingField);
                //check boundaries
                checkGameBounds();
                //ReadStats();
        }));
        
        DetectSaveDataFile();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        SetGameplaySpeed(GameStat.GAME_SPEED);
        SetListeners(); 
        
        CreateDocumentation();
    }
    
    //<editor-fold defaultstate="collapsed" desc="GameBounds">
     private void checkGameBounds(){
        //Paddles can't go beyond PlayingField bounds
        PaddleBoundary(padL);
        PaddleBoundary(padR);
        //Paddle and ball contact
        checkPaddle(padL);
        checkPaddle(padR);
        //If ball hit
        ChangeScore();
    }
    
    private void checkPaddle(Paddle pad){        
        if (PaddelContact(pad)) {
            ball.setDx(ball.getDx()*-1);
            TimelineExtensions.increaseSpeed(timeline,ball.getBALL_ACCELERATION());
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
        
        boolean TopBottom = padTop<=ballBottom && padBottom>=ballTop;
        boolean ContactLeft = (ballLeft==padRight) && TopBottom;
        boolean ContactRight= (ballRight==padLeft) && TopBottom;

        return ContactLeft || ContactRight;
    }
      
    private void PaddleBoundary(Paddle pad) {
        double bounceStrength=1;
        if (pad.getY() < 0) {
            pad.setY(0);
        } else if (pad.getY() > PlayingField.getHeight() - pad.getHeight()) {
            pad.setY(PlayingField.getHeight() - pad.getHeight());
        }
        pad.vy = pad.vy * -bounceStrength;
    }
     // </editor-fold> 

    //<editor-fold defaultstate="collapsed" desc="Inputs and bot">
    private void checkInput() {
        // left paddle
        if (MovementHandler.isDownPressedL() && !MovementHandler.isUpPressedL()) {
            padL.moveDownward();
        } else if (MovementHandler.isUpPressedL() && !MovementHandler.isDownPressedL()) {
            padL.moveUpward();
        } else {
            padL.slowDown();
        }

        // right paddle
        if (MovementHandler.isDownPressedR() && !MovementHandler.isUpPressedR()) {
            padR.moveDownward();
        } else if (MovementHandler.isUpPressedR() && !MovementHandler.isDownPressedR()) {
            padR.moveUpward();
        } else {
            padR.slowDown();
        }
    }

    //Ova funkcija se konstantno delete-a 
    private int GenRandomDirection() {
        int num;
        int min = -1, max = 1;
        do {
            num = ThreadLocalRandom.current().nextInt(min, max + 1);
        } while (num == 0);
        return num;
    }
    
    private void SetGameplaySpeed(int i) {
        for (int j = 0; j < i; j++) {
            TimelineExtensions.increaseSpeed(timeline,ball.getBALL_ACCELERATION());
        }
    }

    private void botFollowBall(Paddle pad) {
        double errorMargin=0.85; // 0.85
        
        if (ball.getCenterX() > PlayingField.getWidth() - PlayingField.getWidth() / 4) {
            pad.setY(ball.getCenterY()>pad.getY()+pad.getHeight()/2 ? pad.getY()+errorMargin : pad.getY()-errorMargin);
        } 
        //activate for auto-aim
//        else {         
//            pad.setY(ball.getCenterY()-pad.getHeight()/2);            
//        }
        
    }
    // </editor-fold>   
    
    //<editor-fold defaultstate="collapsed" desc="Score and Documentation">
    private void ChangeScore() {
        int isHit = ball.getHit();
        if (isHit != 0 && !(isHit > 1) && !(isHit < -1)) {
            //return to start
            ball.setCenterX(game.getStart_posX());
            ball.setCenterY(game.getStart_posY());

            if (isHit > 0) {SetScore(lbLeft,1);} 
            else {SetScore(lbRight,1);}
            ball.setHit(0);
        }
    }
    
    private String GetScore(Label label){
        return label.getText();
    }

    private void SetScore(Label label,Integer num) {
        int point=(Integer.parseInt(label.getText())+num);       
        label.setText(Integer.toString(point));
    }

    private void ReadStats() {
        StringBuilder sb=new StringBuilder();
        sb.append("LeftPaddle position: ");
        sb.append("[").append(padL.getX()).append(",").append(padL.getY()).append("]");
        sb.append(", RightPaddle position: ");
        sb.append("[").append(padR.getX()).append(",").append(padR.getY()).append("]");
        sb.append(", Ball position: ");
        sb.append("[").append(ball.getCenterX()).append(",").append(ball.getCenterY()).append("]");
        sb.append(", Field: ");
        sb.append("[").append(PlayingField.getWidth()).append(",").append(PlayingField.getHeight()).append("]");
        sb.append("\n");
        sb.append("Score: ");
        sb.append("[").append(lbLeft.getText()).append("|").append(lbRight.getText()).append("]");
        
        System.out.println(sb.toString());
    }
    
    private void CreateDocumentation() {

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(DOCUMENTATION_FILENAME))) {
            DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(CLASSES_PATH));

            StringBuilder classAndMembersInfo = new StringBuilder();
            stream.forEach(file -> {
                String filename = file.getFileName().toString();
                if (!filename.contains(".")) {
                    return; //model ima package helper, stoga treba ignorirati ga
                }
                String className = filename.substring(0, filename.indexOf("."));

                classAndMembersInfo
                        .append(className)
                        .append(System.lineSeparator());

                try {
                    Class<?> clazz = Class.forName(CLASSES_PACKAGE.concat(className));
                    ReflectionUtils.readClassAndMembersInfo(clazz, classAndMembersInfo);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(SingleplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }

                classAndMembersInfo
                        .append(System.lineSeparator())
                        .append(System.lineSeparator());

            });

            writer.write(classAndMembersInfo.toString());

        } catch (IOException ex) {
            Logger.getLogger(SingleplayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // </editor-fold> 

    //<editor-fold defaultstate="collapsed" desc="Setup Components">
    private void SetupDefaultBall() {
        ball.setDx(GenRandomDirection());
        ball.setDy(GenRandomDirection());
    }

    private void SetupCustomBall(Ball customBall) {
        ball.setCenterX(customBall.getCenterX());
        ball.setCenterY(customBall.getCenterY());
        ball.setDx(customBall.getDx());
        ball.setDy(customBall.getDy());
    }

    private void SetupPaddle(Paddle paddle, Paddle custom) {
        paddle.setX(custom.getX());
        paddle.setY(custom.getY());
        paddle.vy = custom.vy;
    }

    private void SetupGameStats(GameStat gameStat) {
        System.out.println(gameStat);
        timeline.setRate(gameStat.getGameSpeedRate());
        SetScore(lbLeft, Integer.parseInt(gameStat.getLeftScore()));
        SetScore(lbRight, Integer.parseInt(gameStat.getRightScore()));
    }
    // </editor-fold> 
    
    //<editor-fold defaultstate="collapsed" desc="Serialization">
    private void DetectSaveDataFile() {     
        if (FileName.CheckFileExistance() && AlertUtils.infoBox("Info", "Would you like to load data?", "Save file detected")) {
            LoadFile(ball,FileName.BALL.toString());
            LoadFile(padL,FileName.LEFT_RECTANGLE.toString());
            LoadFile(padR,FileName.RIGHT_RECTANGLE.toString());
            LoadFile(game,FileName.GAMESTAT.toString());
        } else {
            SetupDefaultBall();
        }
    }   
        
    private void LoadFile(Object object,String file_name) {
        try {
            Class<?> clazz = Class.forName(object.getClass().getName());
            Object ser_obj = (Object) SerializationUtils.read(file_name);          

            switch (ser_obj.getClass().getSimpleName()) {
                case "Ball": SetupCustomBall((Ball)ser_obj); break;
                case "Paddle": SetupPaddle((Paddle) object, (Paddle)ser_obj); break;
                case "GameStat": SetupGameStats((GameStat)ser_obj); break;
                default: throw new AssertionError();
            }
            
            //Ball.class.getSimpleName()
//            switch (ser_obj) {
//                case Ball ball -> SetupCustomBall(ball);
//                case Paddle paddle -> ChangePaddle(padL,paddle);
//                default -> throw new AssertionError();
//            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(SingleplayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void SaveFiles(){
        try {
            timeline.stop();
            if (AlertUtils.infoBox("Info", "Would you like to save your game?", "Save game data")) {
                SerializationUtils.write(ball, FileName.BALL.toString());
                SerializationUtils.write(padL, FileName.LEFT_RECTANGLE.toString());
                SerializationUtils.write(padR, FileName.RIGHT_RECTANGLE.toString());
                FillGameStats();
                SerializationUtils.write(game, FileName.GAMESTAT.toString());
            }
        } catch (IOException ex) {
            Logger.getLogger(SingleplayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void FillGameStats() {
        game.setGameSpeedRate(timeline.getRate());
        game.setLeftScore(GetScore(lbLeft));
        game.setRightScore(GetScore(lbRight));
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
                            SaveFiles();
                            Platform.exit();
                        });
                        stage.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event)->onKeyPressed(event));
                    }
                });
            }
        });               
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        //Pause game
        switch (keyEvent.getCode()) {
            case ESCAPE:
                {
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
                break;
            default:
            {
            }
        }
    }
    // </editor-fold> 
}

