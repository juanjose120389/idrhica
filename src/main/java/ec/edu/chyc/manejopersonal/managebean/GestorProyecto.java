/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.ProyectoJpaController;
import ec.edu.chyc.manejopersonal.entity.Pasantia;
import ec.edu.chyc.manejopersonal.entity.Proyecto;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

/**
 *
 * @author Juan José
 */
@Named(value = "gestorProyecto")
@SessionScoped
public class GestorProyecto implements Serializable {

    private final ProyectoJpaController proyectoController = new ProyectoJpaController();
    private Proyecto proyecto;
    private List<Proyecto> listaProyecto = new ArrayList<>();
    
   

    public GestorProyecto() {
        
    }

    @PostConstruct
    public void init() {

    }

    public static GestorProyecto getInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorProyecto}", GestorProyecto.class);
        return (GestorProyecto) ex.getValue(context);
    }

    public void actualizarListaProyecto() {
        try {
            listaProyecto = proyectoController.listProyecto();
        } catch (Exception ex) {
            Logger.getLogger(GestorProyecto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public String initCrearProyecto() {
        proyecto = new Proyecto();
       

        return "manejoProyecto";
    }

    public String initListarProyectos() {
        actualizarListaProyecto();
        return "listaProyecto";
    }

    public List<Proyecto> getListaProyecto() {
        return listaProyecto;
    }

    public void setListaProyecto(List<Proyecto> listaProyecto) {
        this.listaProyecto = listaProyecto;
    }
    
       public String guardar() {
        try {
            proyectoController.create(proyecto);
            return "index";
        } catch (Exception ex) {
            Logger.getLogger(GestorProyecto.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

}
