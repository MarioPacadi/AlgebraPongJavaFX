/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.resources;

/**
 *
 * @author Atlas Comic
 */
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class pongInspiration extends Application {

    private static final int width = 1000; // Width of the window
    private static final int height = 800; // height of the window

    private static final int height_player = 100;
    private static final int width_player = 15;
    private static final double ball_radius = 15;
    //BALL SPEED
    private int ballYSpeed = 1;
    private int ballXSpeed = 1;
    // X POSITION FOR BOTH THE PLAYERS
    private int player1X = 0;
    private double player2X = width - width_player;
    // Y POSITION FOR BOTH THE PLAYERS
    private double player1Y = height / 2;
    private double player2Y = height / 2;
    //LOACATION OF BALL
    private double ballX = width / 2;
    private double ballY = height / 2;
    // SCORES
    private int scoreP1 = 0;
    private int scoreP2 = 0;
    private boolean gameStarted;

    public void start(Stage stage) throws Exception {
        stage.setTitle("P O N G");
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // For Animations 
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), e -> run(gc)));
        //number of cycles in animation INDEFINITE = repeat indefinitely
        tl.setCycleCount(Timeline.INDEFINITE);

        //mouse control (move and click)
        canvas.setOnMouseMoved(e -> player1Y = e.getY());
        canvas.setOnMouseClicked(e -> gameStarted = true);
        stage.setScene(new Scene(new StackPane(canvas)));
        stage.show();
        tl.play();
    }

    private void run(GraphicsContext gc) {

        //set background color
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        // //SET TEXT COLOR
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(25));

        if (gameStarted) {
            //SET BALL MOVEMENT FOR PLAYER
            ballX += ballXSpeed;
            ballY += ballYSpeed;

            //COMPUTER(OPPONENT) FOLLOWING BALL
            if (ballX < width - width / 4) {                // if ball x-position is less than 3/4 of width
                player2Y = ballY - height_player / 2;  //then player2 will move to it
            } else {
                player2Y = ballY > player2Y + height_player / 2 ? player2Y += 1 : player2Y - 1;
            } // if ball y position is greater than player2 y+ player height then inc player2 y by 1
            // if not then decrease it by 1

            gc.fillOval(ballX, ballY, ball_radius, ball_radius);   //draw the ball

        } else {
            //set the start text if game is not Running
            gc.setStroke(Color.WHITE);
            gc.setFill(Color.AQUA);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.strokeText("Click", width / 2, height / 2);

            //reset the ball start position 
            ballX = width / 2;
            ballY = height / 2;

            //reset the ball speed and the direction
            ballXSpeed = 1;
            ballYSpeed = 1;
        }

        //makes sure the ball stays in the canvas
        if (ballY > height || ballY < 0) {
            ballYSpeed *= -1;
        }

        //if you miss the ball, computer gets a point
        if (ballX < player1X - width_player) {
            scoreP2++;
            gameStarted = false;
        }

        //if the computer misses the ball, you get a point
        if (ballX > player2X + width_player) {
            scoreP1++;
            gameStarted = false;
        }

        //increase the speed after the ball hits the player
        // ballX is from center so checks by adding radius of ball
        //        ||||  this is checking X part of Ball and racket       |||||||||||  these two for upper and lower part of racket with ball          
        if (((ballX + ball_radius > player2X) && ballY >= player2Y && ballY <= player2Y + height_player)
                || // condition for hitting the ball for player 2
                ((ballX < player1X + width_player) && ballY >= player1Y && ballY <= player1Y + height_player)) { // condition for hitting the ball for player 1
            // Rebounds the ball after collision
            ballXSpeed *= -1;
            ballYSpeed *= -1;
            // Increase the speed of the ball
            ballYSpeed += 1 * Math.signum(ballYSpeed);       // in opposite sign 
            ballXSpeed += 1 * Math.signum(ballXSpeed);       //math.signum gives +1 for +ve value and -1 for -ve value 
        }

        //draw score
        gc.fillText(scoreP1 + "\t\t\t\t\t\t\t\t" + scoreP2, width / 2, 100);
        //draw player 1 & 2
        gc.fillRect(player2X, player2Y, width_player, height_player);
        gc.fillRect(player1X, player1Y, width_player, height_player);
    }

    // start the application
    public static void main(String[] args) {
        launch(args);
    }
}
