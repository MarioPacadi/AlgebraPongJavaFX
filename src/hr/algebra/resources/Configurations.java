/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.resources;

import hr.algebra.model.Paddle;

/**
 *
 * @author Atlas Comic
 */
public class Configurations {
    public static final String TITLE = "Algebra Pong";
    public static final int WIDTH = 800, HEIGHT = 574;
    public static final int PAD_WIDTH=25,PAD_HEIGHT=120;
    public static final double RADIUS=20;
    public static final String ICON_PATH = "/hr/algebra/resources/Pong.png";
    public static final String START_SCENE_PATH = "/hr/algebra/view/GameUI.fxml";
    public static final String SINGLEPLAYER_PATH = "/hr/algebra/view/Singleplayer.fxml";
    public static final String MULTIPLAYER_PATH = "/hr/algebra/view/Multiplayer.fxml";
    public static final String POSITION_PATH = "/hr/algebra/view/ChoosePosition.fxml";  
    public static final String RESPONSIVE_PATH= "/hr/algebra/view/Responsive.fxml";
    public static final String CHAT_PATH = "/hr/algebra/view/ChatRoom.fxml";
    public static final String REPLAY_PATH = "/hr/algebra/view/Replay.fxml";
    
    public static boolean isUpdateInProcess;
    public static boolean isPowerOn;
    public static final int WAIT_TIME=50;
    
    public static Paddle padL;
    public static Paddle padR;
    
    static {
        double padCenter=HEIGHT/2-PAD_HEIGHT/2;
        padL=new Paddle(0, padCenter, "DARKBLUE", PAD_WIDTH, PAD_HEIGHT);
        padR=new Paddle(WIDTH-PAD_WIDTH,padCenter,"RED",PAD_WIDTH,PAD_HEIGHT);
        
        isUpdateInProcess = false;
        isPowerOn=true;
    }
    
    public static synchronized void PhysicsUpdate(){
        padL.updatePosition();
        padR.updatePosition();
    }
}
