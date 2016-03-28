/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.entity.Proyecto;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author marcelocaj
 */
@Named
@SessionScoped
public class GestorProyecto implements Serializable {
    private Proyecto proyecto = new Proyecto();
    /**
     * Creates a new instance of GestorProyecto
     */
    public GestorProyecto() {
    }
    
    
    public String guardar() {
        return "";
    }
    
    public String initCrearProyecto() {
        proyecto = new Proyecto();
        return "manejoProyecto";
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }
    
}
