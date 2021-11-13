/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.serialization;

import java.io.Serializable;

/**
 *
 * @author Mario
 */
public class ComponentCoordinates implements Serializable{
    private final String componentID;
    private final String x_coord;
    private final String y_coord;

    public ComponentCoordinates(String componentID, String x_coord, String y_coord) {
        this.componentID = componentID;
        this.x_coord = x_coord;
        this.y_coord = y_coord;
    }

    //Getters
    public String getComponentID() {
        return componentID;
    }

    public String getX_coord() {
        return x_coord;
    }

    public String getY_coord() {
        return y_coord;
    }
    
    //Setters

    @Override
    public String toString() {
        return "Coordinates{" + "componentID = " + componentID + ", x = " + x_coord + ", y = " + y_coord + '}';
    }
    
}
