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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Atlas Comic
 */
public class Server extends Thread {

    //public static final String HOST = "127.0.0.1";
    public static final String HOST = "localhost";
    public static final int PORT = 1989;

    public static void main(String[] args) {
        acceptRequests();
    }

    @Override
    public void run() {
        acceptRequests();
    }  

    private static void acceptRequests() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.err.println("Server listening on port: " + serverSocket.getLocalPort());
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());
                processSerializableClient(clientSocket);
                Thread.sleep(6000);
            }
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())) {
            
            Paddle paddle = (Paddle) ois.readObject();
            System.out.println("(Server) The client has paddle: " + paddle);  
            //change
            oos.writeObject(paddle);
            
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
