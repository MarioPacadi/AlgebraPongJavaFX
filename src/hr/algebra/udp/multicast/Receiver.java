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

/**
 *
 * @author Atlas Comic
 */
public class Receiver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        //Address
        String multiCastAddress = "224.0.0.1";
        final int multiCastPort = 52684;
        final int bufferSize = 1024 * 4; //Maximum size of transfer object

        //Create Socket
        System.out.println("Create socket on address " + multiCastAddress + " and port " + multiCastPort + ".");
        InetAddress group = InetAddress.getByName(multiCastAddress);
        MulticastSocket s = new MulticastSocket(multiCastPort);
        s.joinGroup(group);

        //Receive data
        while (true) {
            System.out.println("Wating for datagram to be received...");

            //Create buffer
            byte[] buffer = new byte[bufferSize];
            s.receive(new DatagramPacket(buffer, bufferSize, group, multiCastPort));
            System.out.println("Datagram received!");

            //Deserialze object
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = new ObjectInputStream(bais);
            try {
                Object readObject = ois.readObject();
                if (readObject instanceof Paddle) {
                    Paddle paddle = (Paddle) readObject;
                    System.out.println("Message is: " + paddle);
                } else {
                    System.out.println("The received object is not of type Paddle!");
                }
            } catch (Exception e) {
                System.out.println("No object could be read from the received UDP datagram.");
            }

        }
    }
}
