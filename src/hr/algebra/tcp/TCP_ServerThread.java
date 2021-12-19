/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;

/**
 *
 * @author Atlas Comic
 */
public class TCP_ServerThread extends Thread {

    public static final String HOST = "localhost";
    public static final int PORT = 1700;
    private static boolean connection=false;

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
                if (clientSocket.isConnected()) {
                    connection=true;
                    break;
                }
            }
            System.err.println(TCP_ServerThread.class.getName()+" OUT!");
        } catch (IOException ex) {
            System.out.println("Server already in use!");
            //Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isConnection() {
        return connection;
    }
    
}
