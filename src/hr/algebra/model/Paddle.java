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

    public static double speed = 20;
    public static double stopSpeed = .001;
    public static double accel = 5;
    public static double width = 20;
    public static double height = 100;

    public Paddle(){}
    
    public Paddle(double startX, double startY){
        this.setX(startX);
        this.setY(startY);
        this.setWidth(width);
        this.setHeight(height);
    }

    public void updatePosition(){
        this.setY(this.getY() + vy);
    }

    public void moveUpward(){
        if (vy > -speed) {
            vy = vy - accel;
        }
        if (vy < -speed) {
            vy = -speed;
        }
    }

    public void moveDownward(){
        if (vy < speed) {
            vy  = vy + accel;
        }
        if (vy > speed) {
            vy = speed;
        }
    }

    public void slowDown(){
        if (vy > 0) {
            vy -= accel;
        } else if (vy < 0) {
            vy += accel;
        }

        if (Math.abs(vy) < stopSpeed) {
            vy = 0;
        }
    }
}
