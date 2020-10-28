/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author nickg
 */
public class FlightRecordIsEmptyException extends Exception {
    /**
     * Constructs an instance of <code>FlightRecordIsEmptyException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public FlightRecordIsEmptyException(String msg) {
        super(msg);
    }
}
