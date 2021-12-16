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
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 *
 * @author Atlas Comic
 */
public class Sender {
 
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
 
        //Address
        String multiCastAddress = "224.0.0.1";
        final int multiCastPort = 52684;
 
        //Create Socket
        System.out.println("Create socket on address " + multiCastAddress + " and port " + multiCastPort + ".");
        InetAddress group = InetAddress.getByName(multiCastAddress);
        MulticastSocket s = new MulticastSocket(multiCastPort);
        s.joinGroup(group);
 
        //Prepare Data
        Paddle paddle=new Paddle();
        paddle.setX(200);
        paddle.setY(50);        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(paddle);
        byte[] data = baos.toByteArray();
 
        //Send data
        s.send(new DatagramPacket(data, data.length, group, multiCastPort));
    }
}