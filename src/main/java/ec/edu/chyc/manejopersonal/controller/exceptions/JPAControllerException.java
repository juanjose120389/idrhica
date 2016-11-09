/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller.exceptions;

/**
 *
 * @author marcelocaj
 */
public class JPAControllerException extends Exception {
    public JPAControllerException(String message, Throwable cause) {
        super(message, cause);
    }
    public JPAControllerException(String message) {
        super(message);
    }
    public JPAControllerException(Throwable cause) {
        super(cause);
    }
}
