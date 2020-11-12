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
public class CustomerDoesNotExistException extends Exception {


    /**
     * Constructs an instance of <code>CustomerDoesNotExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CustomerDoesNotExistException(String msg) {
        super(msg);
    }
}
