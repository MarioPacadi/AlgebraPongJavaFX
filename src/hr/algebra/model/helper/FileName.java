/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model.helper;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 *
 * @author Mario
 */


public enum FileName {
    BALL("SaveFiles/ball.ser"),
    GAMESTAT("SaveFiles/gameStat.ser"),
    LEFT_RECTANGLE("SaveFiles/left_rectangle.ser"),
    RIGHT_RECTANGLE("SaveFiles/right_rectangle.ser");

    private final String name;

    private FileName(String name) {
        this.name = name;
    }

    public static Optional<FileName> from(String name) {
        for (FileName value : values()) {
            if (value.name.equals(name)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
    
    public static boolean CheckFileExistance() {
        //All files must exist
        Boolean files_existance = true;
        for (FileName file_name : FileName.values()) {
            File dir = new File(file_name.name);
            if (!dir.getParentFile().exists()) {
                dir.mkdirs(); //Create dir, if not exist
            }
            files_existance=dir.exists();
        }
        return files_existance;
    }

    @Override
    public String toString() {
        return name;
    }

}
