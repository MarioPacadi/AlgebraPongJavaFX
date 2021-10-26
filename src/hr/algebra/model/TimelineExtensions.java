/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;

/**
 *
 * @author Mario
 */
public final class TimelineExtensions{
    
    public static void increaseSpeed(Timeline timeline,double ACCELERATION) {
        timeline.setRate(timeline.getRate() + ACCELERATION);
    }

    public static void decreaseSpeed(Timeline timeline) {
        timeline.setRate(timeline.getRate() > 0 ? timeline.getRate() - 0.1 : 0);
    }

    public static DoubleProperty rateProperty(Timeline timeline) { return timeline.rateProperty();}
    
}
