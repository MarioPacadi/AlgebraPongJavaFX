/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.udp.multicast;

import hr.algebra.model.Paddle;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Atlas Comic
 */
public class ServerThread extends Thread {

    public static final int CLIENT_PORT = 4446;
    // 224.0.0.0 to 239.255.255.255
    public static final String GROUP = "230.0.0.1";
    
    private Paddle paddle=new Paddle();    

    public ServerThread(Paddle paddle) {
        this.paddle=new Paddle(paddle);
    }  

    @Override
    public void run() {
        try (DatagramSocket serverSocket = new DatagramSocket()) {
                                      
            //Prepare Data
            InetAddress groupAddress = InetAddress.getByName(GROUP);  
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(paddle);
            byte[] buffer = baos.toByteArray();

            //Send data
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, groupAddress, CLIENT_PORT);
            serverSocket.send(packet);            

            Thread.sleep(6000);

        } catch (SocketException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Paddle getPaddle() {
        return paddle;
    }      

}
