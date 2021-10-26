/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

import javafx.scene.shape.Rectangle;

//DODAJ implements Externalizable 
public class Paddle extends Rectangle {
    public double vy;

    private static final double SPEED = 20;
    private static final double STOP_SPEED = .001;
    private static final double ACCELERATION = 5;

    public Paddle(){}   

    public void updatePosition(){
        this.setY(this.getY() + vy);
    }

    public void moveUpward(){
        if (vy > -SPEED) {
            vy = vy - ACCELERATION;
        }
        else if (vy < -SPEED) {
            vy = -SPEED;
        }
    }

    public void moveDownward(){
        if (vy < SPEED) {
            vy  = vy + ACCELERATION;
        }
        else if (vy > SPEED) {
            vy = SPEED;
        }
    }

    public void slowDown(){
        if (vy > 0) {
            vy -= ACCELERATION;
        } else if (vy < 0) {
            vy += ACCELERATION;
        }

        if (Math.abs(vy) < STOP_SPEED) {
            vy = 0;
        }
    }
}
