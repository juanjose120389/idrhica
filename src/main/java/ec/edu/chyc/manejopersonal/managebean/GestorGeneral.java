/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.UniversidadJpaController;
import ec.edu.chyc.manejopersonal.entity.Universidad;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

/**
 *
 * @author marcelocaj
 */
@Named(value = "gestorGeneral")
@SessionScoped
public class GestorGeneral implements Serializable {
    private List<Universidad> listaUniversidades = null;
    private final UniversidadJpaController universidadController = new UniversidadJpaController();

    public static GestorGeneral getInstance()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorGeneral}",GestorGeneral.class);
        return (GestorGeneral)ex.getValue(context);
    }
    /**
     * Creates a new instance of GestorGeneral
     */
    public GestorGeneral() {
    }
    @PostConstruct
    public void init() {
        actualizarListaUniversidades();
    }
    
    public void actualizarListaUniversidades() {
        try {
            listaUniversidades = universidadController.listUniversidades();
        } catch (Exception ex) {
            Logger.getLogger(GestorGeneral.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public List<Universidad> getListaUniversidades() {
        return listaUniversidades;
    }

    public void setListaUniversidades(List<Universidad> listaUniversidades) {
        this.listaUniversidades = listaUniversidades;
    }
    
}
