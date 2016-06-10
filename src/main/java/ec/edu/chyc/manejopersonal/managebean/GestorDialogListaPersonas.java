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

    private List<Persona> listaPersonas = new ArrayList<>();
    private List<Persona> listaPersonasSel = new ArrayList<>();
    //private List<Persona> listaPersonasNuevas = new ArrayList<>();
    private String modoSeleccion = "multiple";// single - multiple
    private Persona personaSel = null;
    private Persona nuevaPersona = null;
    private long idGenPersona = -1L;
    private boolean soloNuevaPersona = false;
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

    /**
     * Vacía cualquier rastro de personas agregadas nuevas y usa el listado de personas que está en GestorPersona
     */    
    public void resetearDialog() {
        soloNuevaPersona = false;
        idGenPersona = -1L;
        listaPersonas = new ArrayList<>(GestorPersona.getInstance().getListaPersonasConExternos());
        //listaPersonasNuevas.clear();
        prepararApertura();
    }
    
    /**
     * Vacía los listados de personas seleccionadas y resetea la variable que se usa para ingresar una nueva persona
     */
    public void prepararApertura() {
        listaPersonasSel.clear();        
        personaSel = null;
        nuevaPersona = new Persona();        
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
    
    public void guardarNuevaPersona() {
        listaPersonasSel.clear();
        
        nuevaPersona.setId(idGenPersona);
        //listaPersonasNuevas.add(nuevaPersona);
        listaPersonas.add(nuevaPersona);
        
        listaPersonasSel.add(nuevaPersona);
        
        idGenPersona--;
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

    public Persona getPersonaSel() {
        return personaSel;
    }

    public void setPersonaSel(Persona personaSel) {
        this.personaSel = personaSel;
    }

    public String getModoSeleccion() {
        return modoSeleccion;
    }

    public void setModoSeleccion(String modoSeleccion) {
        this.modoSeleccion = modoSeleccion;
    }

    public Persona getNuevaPersona() {
        return nuevaPersona;
    }

    public void setNuevaPersona(Persona nuevaPersona) {
        this.nuevaPersona = nuevaPersona;
    }

    public List<Persona> getListaPersonas() {
        return listaPersonas;
    }

    public void setListaPersonas(List<Persona> listaPersonas) {
        this.listaPersonas = listaPersonas;
    }

    public boolean isSoloNuevaPersona() {
        return soloNuevaPersona;
    }

    public void setSoloNuevaPersona(boolean soloNuevaPersona) {
        this.soloNuevaPersona = soloNuevaPersona;
    }
        
}
