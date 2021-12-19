/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.udp.multicast;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Atlas Comic
 */
//https://stackoverflow.com/questions/41305781/coding-udp-communication-in-javafx
public class ServerPaddleThread extends Thread {

    public static final int CLIENT_PORT = 4446;
    // 224.0.0.0 to 239.255.255.255
    public static final String GROUP = "230.0.0.1";
    
    private Paddle PADDLE;

    public ServerPaddleThread(Paddle paddle) {
        this.PADDLE=new Paddle(paddle);
    }     

    @Override
    public void run() {
        try (DatagramSocket serverSocket = new DatagramSocket()) {
                                      
            //Prepare Data
            InetAddress groupAddress = InetAddress.getByName(GROUP);  
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(PADDLE);
            byte[] buffer = baos.toByteArray();

            //Send data
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, groupAddress, CLIENT_PORT);
            serverSocket.send(packet);

            // Get response
            packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Waiting to receive response from server." + new Date());
            serverSocket.receive(packet);
            
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = new ObjectInputStream(bais);
            System.out.println("Got the response back from server." + new Date());

            // Display response
            try {
                Object readObject = ois.readObject();
                if (readObject instanceof Paddle) {
                    Paddle pad = (Paddle) readObject;
                    PADDLE.setY(pad.getY());
                    //System.out.println("Message is: " + pad);
                } else {
                    System.out.println("The received object is not of type Paddle!");
                }
            } catch (ClassNotFoundException e) {
                System.out.println("No object could be read from the received UDP datagram.");
            }
            
            //String received = new String(packet.getData());
            //System.out.println("Quote of the Moment: " + received);            

            //Thread.sleep(6000);

        } catch (SocketException ex) {
            Logger.getLogger(ServerPaddleThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerPaddleThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Paddle getPaddle() {
        return PADDLE;
    }      
    
    

}
