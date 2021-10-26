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

    public int hit=0;
    public final double BALL_ACCELERATION=0.8;
    
    public final double radius = 20;
    public double x, y;
    public int dx = 1, dy = 1;   

    public Ball(){}

    public void moveBall(Pane PlayingField) {
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

    public void setHit(int hit) {
        this.hit = hit;
    }
    
}
