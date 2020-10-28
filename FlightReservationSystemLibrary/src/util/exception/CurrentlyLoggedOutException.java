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
public class CurrentlyLoggedOutException extends Exception {


    /**
     * Constructs an instance of <code>CurrentlyLoggedOutException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CurrentlyLoggedOutException(String msg) {
        super(msg);
    }
}
