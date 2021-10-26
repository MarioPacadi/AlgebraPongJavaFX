/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

/**
 *
 * @author Mario
 */
public class Ball extends Circle {

    private final double BALL_ACCELERATION=0.8;   
  
    private int hit = 0;
    private int dx = 1, dy = 1;

    public Ball(){}

    public void moveBall(Pane PlayingField) {
        double x=this.getCenterX();
        double y=this.getCenterY();
        double radius=this.getRadius();
        
        if (x < radius || x > PlayingField.getWidth() - radius) {
            setHit(dx);
            dx *= -1;
        }
        if (y < radius || y > PlayingField.getHeight() - radius) {
            dy *= -1;
            //TimelineExtensions.increaseSpeed(timeline,BALL_ACCELERATION);
        }
        
        x += dx;
        y += dy;

        this.setCenterX(x);
        this.setCenterY(y);
    }
    
    //Getter and setter
    public int getHit() {
        return hit;
    }
    
    public double getBALL_ACCELERATION() {
        return BALL_ACCELERATION;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }
    
    public void setHit(int hit) {
        this.hit = hit;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }
    
    
    
}
