/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.udp.unicast;

import hr.algebra.controller.MultiplayerController;
import hr.algebra.model.Paddle;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniel.bele
 */
public class ClientThread extends Thread {

    private String HOST = "clienthost1";
    private int PORT = 12350;
    
    private Paddle PADDLE;
    private volatile boolean running = true;

    public ClientThread(Paddle paddle) {
        this.PADDLE = new Paddle(paddle);
    }
    
    public ClientThread(String host, int port, Paddle paddle) {
        this.HOST = host;
        this.PORT = port;
        this.PADDLE = paddle;
    }
    
    @Override
    public void run() {
        while (running) {
            try (DatagramSocket clientSocket = new DatagramSocket()) {

                try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                    //Send data
                    oos.writeObject(PADDLE);
                    byte[] buffer = baos.toByteArray();
                    
                    //Server info
                    InetAddress serverAddress = InetAddress.getByName(HOST);
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, PORT);
                    //System.out.println("Client sent message from " + serverAddress + ":" + clientSocket.getLocalPort());
                    
                    clientSocket.send(packet);
                    
                } catch (Exception e) {
                    System.out.println("Client wasnt able to send packet to server!");
                }

                Thread.sleep(MultiplayerController.TIMELINE_DURATION / 2);
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }       
    }
    
    public void terminate() {
        running = false;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Get and Set">
    public Paddle getPADDLE() {
        return PADDLE;
    }
    
    public double getY() {
        return PADDLE.getY();
    }
    
    public void setPADDLE(Paddle PADDLE) {
        this.PADDLE = PADDLE;
    }
    // </editor-fold> 
}
