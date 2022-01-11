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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 *
 * @author Mario
 */
public class Ball extends Circle implements Externalizable {

    private static final long serialVersionUID = 1L;
    
    private final double BALL_ACCELERATION=0.8;   
  
    private int hit = 0;
    private int dx = 1, dy = 1;

    public Ball(){}
    public Ball(double x,double y,double radius,String color){
        this.setCenterX(x);
        this.setCenterY(y);
        this.setRadius(radius);
        this.setFill(Paint.valueOf(color));
    }

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
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(this.getCenterX());
        out.writeDouble(this.getCenterY());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setCenterX(in.readDouble());
        this.setCenterY(in.readDouble());
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

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append("CentarX = ").append(this.getCenterX()).append(" ");
        sb.append("CentarY = ").append(this.getCenterY()).append("\n");
        sb.append("dx = ").append(this.dx).append(" ");
        sb.append("dy = ").append(this.dy).append("\n");
        
        return sb.toString();
    }    
}
