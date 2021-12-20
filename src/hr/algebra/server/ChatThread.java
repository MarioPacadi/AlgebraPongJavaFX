/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.server;

import hr.algebra.contract.MessengerService;
import hr.algebra.contract.MessengerServiceImpl;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Atlas Comic
 */
public class ChatThread extends Thread{

    @Override
    public void run() {
        System.out.println("Server started...");

        MessengerService server = new MessengerServiceImpl();
        try {
            MessengerService stub = (MessengerService) UnicastRemoteObject
                    .exportObject((MessengerService) server, 0);

            Registry registry = LocateRegistry.createRegistry(1099);

            System.out.println("RMI Registry created!");

            registry.rebind("MessengerService", stub);

            System.out.println("Service binded...");

        } catch (RemoteException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
}
