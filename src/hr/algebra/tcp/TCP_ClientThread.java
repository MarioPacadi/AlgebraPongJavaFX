/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.tcp;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Atlas Comic
 */
public class TCP_ClientThread extends Thread {

    public static boolean connection=false;
    
    public TCP_ClientThread() {}
    
    @Override
    public void run() {
        sendRequest();      
    }
    
    private void sendRequest() {
        try (Socket clientSocket = new Socket(TCP_ServerThread.HOST, TCP_ServerThread.PORT)) {
            System.err.println("Client is connecting to: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
            connection=clientSocket.isConnected();
        } catch (IOException ex) {
            Logger.getLogger(TCP_ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public static boolean isConnection() {
        return connection;
    }
    
    
    
}
