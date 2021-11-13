/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

/**
 *
 * @author Mario
 */
public class Ball extends Circle implements Serializable  {

    private static final long serialVersionUID = 1L;
    
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
    
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeUTF(Double.toString(this.getCenterX()));
        oos.writeUTF(Double.toString(this.getCenterY()));
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.setCenterX(Double.parseDouble(ois.readUTF()));
        this.setCenterY(Double.parseDouble(ois.readUTF()));
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
        sb.append("CentarX = ").append(this.getCenterX()).append("\n");
        sb.append("CentarY = ").append(this.getCenterY()).append("\n");
        sb.append("dx = ").append(this.dx).append("\n");
        sb.append("dy = ").append(this.dy).append("\n");
        
        return sb.toString();
    }
    
    
    
}
