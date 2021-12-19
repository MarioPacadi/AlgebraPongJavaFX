/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.udp.unicast;

import hr.algebra.model.Paddle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniel.bele
 */
public class ServerThread extends Thread {

    public static final String HOST = "localhost";
    public static final int SERVER_PORT = 12345;
    public static final int BUFSIZE = 1024 * 4;
    
    private Paddle PADDLE;

    public ServerThread(Paddle paddle) {
        this.PADDLE = new Paddle(paddle);
    }
    
    @Override
    public void run() {
        while (true) {
            try (DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT)) {
                //Receive response
                System.err.println("Server listening on port: " + serverSocket.getLocalPort());
                byte[] buffer = new byte[BUFSIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(packet);

                //Client info
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                System.out.println("Server received message from " + clientAddress + ":" + clientPort);

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

                //Send data
                try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                    
                    oos.writeObject(PADDLE);
                    buffer = baos.toByteArray();
                    packet = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                    serverSocket.send(packet);
                } catch (IOException e) {
                    System.err.println("No object could be writen from the received UDP datagram.");
                }
                
                Thread.sleep(50);

            } catch (SocketException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }
    
    public Paddle getPADDLE() {
        return PADDLE;
    }

    public double getY() {
        return PADDLE.getY();
    }

    public void setPADDLE(Paddle PADDLE) {
        this.PADDLE = PADDLE;
    }   

}
