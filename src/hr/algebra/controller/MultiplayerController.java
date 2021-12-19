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
import hr.algebra.serializable.GameStat;
import hr.algebra.udp.multicast.ClientPaddleThread;
import hr.algebra.udp.multicast.ServerPaddleThread;
import hr.algebra.udp.unicast.ClientThread;
import hr.algebra.udp.unicast.ServerThread;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
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
 * @author Atlas Comic
 */
public class MultiplayerController implements Initializable {

    // <editor-fold defaultstate="collapsed" desc="Variables">
    public int POSITION=-1;
    
    private GameStat game;
    private Timeline timeline;
    private static final int MAX_SCORE=5;
    
    private ServerThread serverR;
    private ClientThread clientR;
    
    private ServerThread serverL;
    private ClientThread clientL;

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

    //Sloziti ServerProjekt
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       game=new GameStat(ball.getCenterX(),ball.getCenterY());
        
       serverR=new ServerThread(padL);
       clientR=new ClientThread(padR);
       serverL = new ServerThread(padR);
       clientL = new ClientThread(padL);
       Platform.runLater(()->StartThreads());
       
        timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> {
                //Input
                checkInput();
                //physics updates
                padL.updatePosition();
                padR.updatePosition();                
                ball.moveBall(PlayingField);
                //check boundaries
                checkGameBounds();
        }));
        
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
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

    //<editor-fold defaultstate="collapsed" desc="Inputs and bot">
    private void checkInput() {
        switch (POSITION) {
            case 0:
                enabledLeft();  
                //server.setPADDLE(padL);
                //serverInput();
                networkInput(padR, serverR);
                break;
            case 1:
                enabledRight();
                clientR.setPADDLE(padR);
                
                //networkInput(padL, client);
                //clientInput();                
                break;
            default:
                lbPause.setText("Non-existant \n position!");
                pauseGame();
                break;
        }
    }
    
    private void enabledLeft() {
        if (MovementHandler.isDownPressedL() && !MovementHandler.isUpPressedL()) {
            padL.moveDownward();
        } else if (MovementHandler.isUpPressedL() && !MovementHandler.isDownPressedL()) {
            padL.moveUpward();
        } else {
            padL.slowDown();
        }
        //new ServerPaddleThread(padL,POSITION,"230.0.0.2").start();  
    }

    private void enabledRight() {
        if (MovementHandler.isDownPressedR() && !MovementHandler.isUpPressedR()) {
            padR.moveDownward();
        } else if (MovementHandler.isUpPressedR() && !MovementHandler.isDownPressedR()) {
            padR.moveUpward();
        } else {
            padR.slowDown();
        }
        //new ServerPaddleThread(padR,POSITION,"230.0.0.1").start();  
    }
    
    private void clientInput(Paddle padSend, Paddle padChange) {
        ClientPaddleThread cpt = new ClientPaddleThread("Client " + padSend.getId());
        cpt.start();
        if (cpt.getY() != null) {
            padChange.setY(cpt.getY());
        }
    }
    
    private void clientInput(Paddle padSend, Paddle padChange, int PORT) {
        ClientPaddleThread cpt = new ClientPaddleThread("Client " + padSend.getId());
        cpt.start();
        if (cpt.getY() != null) {
            padChange.setY(cpt.getY());
        }
    }
    
    private void StartThreads(){
        System.out.println(POSITION);
        switch (POSITION) {
            case 0:
                serverR.start();
                break;
            case 1:
                clientR.start();
                break;
            default:
                lbPause.setText("Non-existant \n position!");
                pauseGame();
                break;
        }
    }
    
    private void serverInput() {
        padR.setY(serverR.getY());
    }
    
    private void clientInput() {
        padL.setY(clientR.getY());     
    }
    
    private void networkInput(Paddle changepad,Thread net) {
        switch (net.getClass().getSimpleName()) {
            case "ServerThread":
                changepad.setY(((ServerThread)net).getY());
                break;
            case "ClientThread":
                changepad.setY(((ClientThread)net).getY());
                break;
            default:
                System.out.println("Network thread isnt instanceof scope");
        }
//        if (net instanceof ServerThread || net instanceof ClientThread) {
//            pad.setY(net.getY());
//        }     
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
            TimelineExtensions.increaseSpeed(timeline, ball.getBALL_ACCELERATION());
        }
    }

    // </editor-fold>   

    //<editor-fold defaultstate="collapsed" desc="Score and Documentation">
    private void ChangeScore() {
        int isHit = ball.getHit();
        if (isHit != 0 && !(isHit > 1) && !(isHit < -1)) {
            //return to start
            ball.setCenterX(game.getStart_posX());
            ball.setCenterY(game.getStart_posY());

            if (isHit > 0) { SetScore(lbLeft, 1); } 
            else { SetScore(lbRight, 1); }
            
            if (checkScore(lbLeft)) {
                lbPause.setText("Left \n Won");
                pauseGame();
            }
            else if (checkScore(lbRight)) {
                lbPause.setText("Right \n Won");
                pauseGame();
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
    private void SetupDefaultBall() {
          ball.setDx(-1);
          ball.setDy(-1);
//        ball.setDx(GenRandomDirection());
//        ball.setDy(GenRandomDirection());

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
                pauseGame();
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
  
}
