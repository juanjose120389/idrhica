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
@Named(value = "gestorDialogListaProyectos")
@SessionScoped
public class GestorDialogListaProyectos implements Serializable {

    private List<Proyecto> listaProyectosSel = new ArrayList<>();
    private String modoSeleccion = "multiple";// single - multiple
    private Proyecto proyectoSel = null;

    public GestorDialogListaProyectos() {
    }
    
    @PostConstruct
    public void init() {        
    }
    
    public void clearListaProyectosSel() {
        listaProyectosSel.clear();
    }
    
    public static GestorDialogListaProyectos getInstance()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorDialogListaProyectos}",GestorDialogListaProyectos.class);
        return (GestorDialogListaProyectos)ex.getValue(context);
    }

    public void aceptarSeleccion() {
        RequestContext.getCurrentInstance().closeDialog(listaProyectosSel);
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

    public List<Proyecto> getListaProyectosSel() {
        return listaProyectosSel;
    }

    public void setListaProyectosSel(List<Proyecto> listaProyectosSel) {
        this.listaProyectosSel = listaProyectosSel;
    }

    public Proyecto getProyectoSel() {
        return proyectoSel;
    }

    public void setProyectoSel(Proyecto proyectoSel) {
        this.proyectoSel = proyectoSel;
    }
        
}
