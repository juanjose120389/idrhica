/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.entity.Persona;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author marcelocaj
 */
@Named(value = "gestorDialogListaTesistas")
@SessionScoped
public class GestorDialogListaTesista implements Serializable {

    private List<Persona> listaTesistasSel = new ArrayList<>();
    
    /**
     * Creates a new instance of GestorDialogListaPersonas
     */
    public GestorDialogListaTesista() {
    }
    @PostConstruct
    public void init() {        
    }
    
    public void clearListaPersonasSel() {
        listaTesistasSel.clear();
    }
    
    public static GestorDialogListaTesista getInstance()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorDialogListaTesistas}",GestorDialogListaTesista.class);
        return (GestorDialogListaTesista)ex.getValue(context);
    }

    public void aceptarSeleccionTesistas() {
        RequestContext.getCurrentInstance().closeDialog(listaTesistasSel);
    }
    public void cancelarSeleccionTesistas() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }
    
    public List<Persona> getListaTesistasSel() {
        return listaTesistasSel;
    }

    public void setListaTesistasSel(List<Persona> listaTesistasSel) {
        this.listaTesistasSel = listaTesistasSel;
    }
    
    
}
