/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.serializable;

import java.io.Serializable;

/**
 *
 * @author Mario
 */
public class GameStat implements Serializable {
    private static final long serialVersionUID = 3L;
    
    public static final int GAME_SPEED = 5;
    private double gameSpeedRate;
    private double start_posX;
    private double start_posY;

    public GameStat() {
    }
    
    public GameStat(double start_posX, double start_posY) {
        this.start_posX = start_posX;
        this.start_posY = start_posY;
    }

    public double getGameSpeedRate() {
        return gameSpeedRate;
    }

    public double getStart_posX() {
        return start_posX;
    }

    public double getStart_posY() {
        return start_posY;
    }
    
    public void setGameSpeedRate(double gameSpeedRate) {
        this.gameSpeedRate = gameSpeedRate;
    }

    public void setStart_posX(double start_posX) {
        this.start_posX = start_posX;
    }

    public void setStart_posY(double start_posY) {
        this.start_posY = start_posY;
    }
    
    @Override
    public String toString() {
        return "GameStat{" + "gameSpeedRate=" + gameSpeedRate + ", start_posX=" + start_posX + ", start_posY=" + start_posY + '}';
    }
}
