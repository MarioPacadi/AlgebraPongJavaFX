/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.controller;

import hr.algebra.model.Ball;
import hr.algebra.model.Paddle;
import hr.algebra.model.TimelineExtensions;
import hr.algebra.handler.MovementHandler;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Mario
 */
public class SingleplayerController implements Initializable {

    private final int GAME_SPEED=5;
    private static double start_posX;
    private static double start_posY;
     
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
    
    private Timeline timeline;
    
    private final String BOING="Boing";
    
    @FXML
    public void escapeKeyPressed(KeyEvent event) {
        //Pause game
        if (event.getCode() == KeyCode.ESCAPE) {
            lbPause.setVisible(!lbPause.visibleProperty().get());
            PlayingField.setOpacity(0.5);
            timeline.stop();
            System.out.println("escape got called");
        } else {
            PlayingField.setOpacity(1);
            timeline.play();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        SetUpBall();         
        
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
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        SetGameplaySpeed(GAME_SPEED);
    }
    
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
            //System.out.println(BOING);
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

    private int GenRandom() {
        int num;
        int min=-1, max=1;
        do{ num=ThreadLocalRandom.current().nextInt(min, max + 1); }while(num==0);
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

    private void SetUpBall() {
        start_posX=ball.getCenterX();
        start_posY=ball.getCenterY();
        ball.setDx(GenRandom());
        ball.setDy(GenRandom());
    }
    
    private void ChangeScore() {
        int isHit = ball.getHit();
        if (isHit != 0 && !(isHit > 1) && !(isHit < -1)) {
            //return to start
            ball.setCenterX(start_posX);
            ball.setCenterY(start_posY);

            if (isHit > 0) {SetScore(lbLeft);} 
            else {SetScore(lbRight);}
            ball.setHit(0);
        }
    }

    private void SetScore(Label label) {
        int point=(Integer.parseInt(label.getText())+1);       
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
    
}
