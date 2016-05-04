/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.entity.Institucion;
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
@Named(value = "gestorDialogListaInstituciones")
@SessionScoped
public class GestorDialogListaInstituciones implements Serializable {

    private List<Institucion> listaSeleccionados = new ArrayList<>();
    private String modoSeleccion = "multiple";// single - multiple

    public GestorDialogListaInstituciones() {
    }
    
    @PostConstruct
    public void init() {        
    }
    
    public void clearListaSeleccionados() {
        listaSeleccionados.clear();
    }
    
    public static GestorDialogListaInstituciones getInstance()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorDialogListaInstituciones}",GestorDialogListaInstituciones.class);
        return (GestorDialogListaInstituciones)ex.getValue(context);
    }

    public void aceptarSeleccion() {
        RequestContext.getCurrentInstance().closeDialog(listaSeleccionados);
    }
    public void cancelarSeleccion() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }
    
    public String getModoSeleccion() {
        return modoSeleccion;
    }

    public void setModoSeleccion(String modoSeleccion) {
        this.modoSeleccion = modoSeleccion;
    }

    public List<Institucion> getListaSeleccionados() {
        return listaSeleccionados;
    }

    public void setListaSeleccionados(List<Institucion> listaSeleccionados) {
        this.listaSeleccionados = listaSeleccionados;
    }
        
}
