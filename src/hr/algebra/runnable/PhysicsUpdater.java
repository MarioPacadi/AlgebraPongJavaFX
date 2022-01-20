/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.runnable;

import hr.algebra.resources.Configurations;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Atlas Comic
 */
public class PhysicsUpdater implements Runnable {

    @Override
    public void run() {
        while (Configurations.isPowerOn) {

            updatePhysics();

            try {
                Thread.sleep(Configurations.WAIT_TIME);
            } catch (InterruptedException ex) {
                Logger.getLogger(PhysicsUpdater.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }
    }
    
    public synchronized void updatePhysics() {

        while (Configurations.isUpdateInProcess == true) {
            try {
                System.out.println("Thread tried to update physics");
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(PhysicsUpdater.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }

        Configurations.isUpdateInProcess = true;

        Configurations.PhysicsUpdate();

        Configurations.isUpdateInProcess = false;

        notifyAll();
    }
}
