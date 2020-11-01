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
public class IncorrectFormatException extends Exception {


    /**
     * Constructs an instance of <code>IncorrectFormatException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public IncorrectFormatException(String msg) {
        super(msg);
    }
}
