/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.tcp;

import hr.algebra.model.Ball;
import hr.algebra.model.Paddle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Atlas Comic
 */
public class PaddleThread extends Thread {

    public Paddle paddle;   
    
    @Override
    public void run() {
        sendRequest();      
    }

    public PaddleThread(Paddle paddle, String name) {
        super(name);
        this.paddle = paddle;
    }
    
    private void sendRequest() {
        try (Socket clientSocket = new Socket(Server.HOST, Server.PORT)) {
            System.err.println("Client is connecting to: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
            sendSerializableRequest(clientSocket);
            //readSerializableRequest(clientSocket);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(PaddleThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendSerializableRequest(Socket clientSocket) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());              
        
        oos.writeObject(paddle); // Salje serveru
        //System.out.println("(Client) "+this.getName() +" Moved to: " + ois.readObject()); // Cita sa servera
        setPaddle((Paddle) ois.readObject());
        System.out.println("Changed client "+this.getName()+" paddle: "+paddle);
    }   

    public Paddle getPaddle() {
        return paddle;
    }

    public void setPaddle(Paddle ball) {
        this.paddle = ball;
    }  
    
    
}
