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
@Named(value = "gestorDialogListaPersonas")
@SessionScoped
public class GestorDialogListaPersonas implements Serializable {

    private List<Persona> listaPersonasSel = new ArrayList<>();
    
    /**
     * Creates a new instance of GestorDialogListaPersonas
     */
    public GestorDialogListaPersonas() {
    }
    @PostConstruct
    public void init() {        
    }
    
    public void clearListaPersonasSel() {
        listaPersonasSel.clear();
    }
    
    public static GestorDialogListaPersonas getInstance()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorDialogListaPersonas}",GestorDialogListaPersonas.class);
        return (GestorDialogListaPersonas)ex.getValue(context);
    }

    public void aceptarSeleccionPersonas() {
        RequestContext.getCurrentInstance().closeDialog(listaPersonasSel);
    }
    public void cancelarSeleccionPersonas() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }
    
    public List<Persona> getListaPersonasSel() {
        return listaPersonasSel;
    }

    public void setListaPersonasSel(List<Persona> listaPersonasSel) {
        this.listaPersonasSel = listaPersonasSel;
    }
    
    
}
