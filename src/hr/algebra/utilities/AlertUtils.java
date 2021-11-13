/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.utilities;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Mario
 */
public class AlertUtils {
    //Alerts
    public static boolean infoBox(String infoMessage, String headerText, String title) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(infoMessage);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.getButtonTypes();

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK; 
        // ... user chose OK button
        // ... user chose CANCEL or closed the dialog
    }
}
