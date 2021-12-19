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
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniel.bele
 */
public class ClientThread extends Thread {

    private Paddle PADDLE;

    public ClientThread(Paddle paddle) {
        this.PADDLE = new Paddle(paddle);
    }
    
    @Override
    public void run() {
        while (true) {
            try (DatagramSocket clientSocket = new DatagramSocket()) {
                //Send data
                byte[] buffer=new byte[MIN_PRIORITY];
                try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                    //Send data
                    oos.writeObject(PADDLE);
                    buffer = baos.toByteArray();
                    
                    //Client info
                    InetAddress serverAddress = InetAddress.getByName(ServerThread.HOST);
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, ServerThread.SERVER_PORT);
                    int serverPort = packet.getPort();
                    System.out.println("Client sent message from " + serverAddress + ":" + serverPort);
                    
                    clientSocket.send(packet);
                    
//                    System.err.println("Client listening on port: " + clientSocket.getLocalPort());
//                    buffer = new byte[ServerThread.BUFSIZE];
//                    packet = new DatagramPacket(buffer, buffer.length);
//                    clientSocket.receive(packet);
                } catch (Exception e) {
                    System.out.println("Client wasnt able to send packet to server!");
                }

                //Display response
//                try(ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
//                    ObjectInputStream ois = new ObjectInputStream(bais)) {
//                    
//                    Object readObject = ois.readObject();
//                    if (readObject instanceof Paddle) {
//                        Paddle pad = (Paddle) readObject;
//                        PADDLE.setY(pad.getY());
//                    } else {
//                        System.err.println("The received object is not of type Paddle!");
//                    }
//                } catch (ClassNotFoundException e) {
//                    System.err.println("No object could be read from the received UDP datagram.");
//                }

                Thread.sleep(25);
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
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
