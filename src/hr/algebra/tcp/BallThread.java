/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.tcp;

import hr.algebra.model.Ball;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Atlas Comic
 */
public class BallThread extends Thread {

    public Ball ball;   
    
    @Override
    public void run() {
        sendRequest();      
    }

    public BallThread(Ball ball, String name) {
        super(name);
        this.ball = ball;
    }
    
    private void sendRequest() {
        try (Socket clientSocket = new Socket(Server.HOST, Server.PORT)) {
            System.err.println("Client is connecting to: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
            sendSerializableRequest(clientSocket);
            //readSerializableRequest(clientSocket);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(BallThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendSerializableRequest(Socket clientSocket) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());              
        
        oos.writeObject(ball); // Salje serveru
        //System.out.println("(Client) "+this.getName() +" Moved to: " + ois.readObject()); // Cita sa servera
        setBall((Ball) ois.readObject());
        System.out.println("Changed client "+this.getName()+" ball: "+ball);
    }   

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }  
    
    
}
