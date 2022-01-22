/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.udp.unicast;

import hr.algebra.controller.MultiplayerController;
import hr.algebra.model.Paddle;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Atlas Comic
 */
public class ServerThread extends Thread {

    private String HOST;
    private int PORT;
    public static final int BUFSIZE = 1024 * 4;
    
    private Paddle PADDLE;
    private static boolean PAUSE=false;
    private volatile boolean running = true;

    public ServerThread(Paddle paddle) {
        this.PADDLE = new Paddle(paddle);
    }
    
    public ServerThread(String host, int port, Paddle paddle) {
        this.HOST = host;
        this.PORT = port;
        this.PADDLE = paddle;
    }
    
    @Override
    public void run() {
        while (running) {
            try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
                //Receive response
                //System.err.println("Server listening on port: " + serverSocket.getLocalPort());
                byte[] buffer = new byte[BUFSIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(packet);

                //Client info
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                //System.out.println("Server received message from " + clientAddress + ":" + clientPort);

                //Display response
                try(ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                    ObjectInputStream ois = new ObjectInputStream(bais)) {
                    
                    Object readObject = ois.readObject();
                    if (readObject instanceof Paddle) {
                        Paddle pad = (Paddle) readObject;
                        PADDLE.setY(pad.getY());
                    } else {
                        System.err.println("The received object is not of type Paddle!");
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("No object could be read from the received UDP datagram.");
                }
              
                Thread.sleep(MultiplayerController.TIMELINE_DURATION/2);

            } catch (SocketException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }
    
    public void terminate() {
        running = false;
    }

    //<editor-fold defaultstate="collapsed" desc="Get and Set">
    public double getY() {
        return PADDLE.getY();
    }
    
    public Paddle getPADDLE() {
        return PADDLE;
    }

    public void setPADDLE(Paddle PADDLE) {
        this.PADDLE = PADDLE;
    }   

    public void setPauseGame(boolean pause_game) {
        ServerThread.PAUSE = pause_game;
    }

    public boolean isPauseGame() {
        return PAUSE;
    }
    // </editor-fold>    
}
