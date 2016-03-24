/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author marcelocaj
 */
@Named(value = "gestorUsuario")
@SessionScoped
public class GestorUsuario implements Serializable {

    /**
     * Creates a new instance of GestorUsuario
     */
    public GestorUsuario() {
    }
    
}
