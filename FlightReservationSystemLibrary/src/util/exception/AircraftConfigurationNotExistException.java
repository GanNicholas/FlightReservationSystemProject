/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author sohqi
 */
public class AircraftConfigurationNotExistException extends Exception {

    public AircraftConfigurationNotExistException() {

    }

    public AircraftConfigurationNotExistException(String msg) {
        super(msg);
    }
}
