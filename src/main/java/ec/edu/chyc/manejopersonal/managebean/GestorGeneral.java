/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author marcelocaj
 */
@Named(value = "gestorGeneral")
@SessionScoped
public class GestorGeneral implements Serializable {
    
    /**
     * Creates a new instance of GestorGeneral
     */
    public GestorGeneral() {
    }
    
}
