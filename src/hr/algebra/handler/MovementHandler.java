/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.handler;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Mario
 */
public final class MovementHandler {
    
    private static boolean downPressedL, upPressedL;
    private static boolean downPressedR, upPressedR;
    
    private static final EventHandler<KeyEvent> MOVE = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            switch (event.getCode()) {
                case W:
                    upPressedL = true;
                    break;
                case S:
                    downPressedL = true;
                    break;
                case DOWN:
                    downPressedR = true;
                    break;
                case UP:
                    upPressedR = true;
                    break;
            }
        }
    };
    
    private static final EventHandler<KeyEvent> STAND = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            switch (event.getCode()) {
                case W:
                    upPressedL = false;
                    break;
                case S:
                    downPressedL = false;
                    break;
                case DOWN:
                    downPressedR = false;
                    break;
                case UP:
                    upPressedR = false;
                    break;
            }
        }
    };
    
    //---Get---
    public static EventHandler<KeyEvent> getMove() {
        return MOVE;
    }

    public static EventHandler<KeyEvent> getStand() {
        return STAND;
    }
     
    public static boolean isDownPressedL() {
        return downPressedL;
    }

    public static boolean isUpPressedL() {
        return upPressedL;
    }

    public static boolean isDownPressedR() {
        return downPressedR;
    }

    public static boolean isUpPressedR() {
        return upPressedR;
    }
    
}
