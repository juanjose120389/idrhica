/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.entity.Pasantia;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author marcelocaj
 */
@Named
@SessionScoped
public class GestorPasantia implements Serializable {
    private Pasantia pasantia = new Pasantia();
    /**
     * Creates a new instance of GestorProyecto
     */
    public GestorPasantia() {
    }
    
    
    public String guardar() {
        return "";
    }
    
    public String initCrearPasantia() {
        pasantia = new Pasantia();
        return "manejoPasantia";
    }

    public Pasantia getPasantia() {
        return pasantia;
    }

    public void setProyecto(Pasantia pasantia) {
        this.pasantia = pasantia;
    }
    
}
