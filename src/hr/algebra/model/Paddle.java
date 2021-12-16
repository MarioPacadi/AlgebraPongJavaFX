/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javafx.scene.shape.Rectangle;


public class Paddle extends Rectangle implements Externalizable   {
    
    private static final long serialVersionUID = 2L;
    
    public double vy;
    private static final double SPEED = 20;
    private static final double STOP_SPEED = .001;
    private static final double ACCELERATION = 5;

    public Paddle(){}   

    public Paddle(Paddle paddle) {
        this.setX(paddle.getX());
        this.setY(paddle.getY());
        this.setWidth(paddle.getWidth());
        this.setHeight(paddle.getHeight());
        this.setFill(paddle.getFill());
        this.vy=paddle.vy;
    }

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

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(this.getX());
        out.writeDouble(this.getY());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setX(in.readDouble());
        this.setY(in.readDouble());
    }
    
}
