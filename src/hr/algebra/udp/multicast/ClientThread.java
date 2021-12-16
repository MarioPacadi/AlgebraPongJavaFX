/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.udp.multicast;

import hr.algebra.model.Paddle;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Atlas Comic
 */
public class ClientThread extends Thread {

    private final int bufferSize = 1024 * 4;
    private static Double Y;
    
    public ClientThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        System.out.println("Wating for datagram to be received...");
        
        try (MulticastSocket clientSocket = new MulticastSocket(ServerThread.CLIENT_PORT)) {

            InetAddress groupAddress = InetAddress.getByName(ServerThread.GROUP);
            System.err.println(getName() + " joining group");
            clientSocket.joinGroup(groupAddress);

            //Create buffer
            byte[] buffer = new byte[bufferSize];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            clientSocket.receive(packet);
            System.out.println("Datagram received!");

            //Deserialze object
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = new ObjectInputStream(bais);
            try {
                Object readObject = ois.readObject();
                if (readObject instanceof Paddle) {
                    Paddle pad = (Paddle) readObject;
                    Y=pad.getY();
                    System.out.println("Message is: " + pad);
                } else {
                    System.out.println("The received object is not of type Paddle!");
                }
            } catch (ClassNotFoundException e) {
                System.out.println("No object could be read from the received UDP datagram.");
            }                   

            System.err.println(getName() + " leaving group");
            clientSocket.leaveGroup(groupAddress);

        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Double getY() {
        return Y;
    }
    
}
