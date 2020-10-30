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
public class FlightRouteDoesNotExistException extends Exception {

    public FlightRouteDoesNotExistException() {
        super();
    }

    public FlightRouteDoesNotExistException(String msg) {
        super(msg);
    }
}
