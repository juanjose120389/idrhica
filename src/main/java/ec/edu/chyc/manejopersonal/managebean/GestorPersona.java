/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.PersonaJpaController;
import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.entity.Titulo;
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
import javax.faces.bean.ManagedProperty;

/**
 *
 * @author Marcelo
 */
@Named(value = "gestorPersona")
@SessionScoped
public class GestorPersona implements Serializable {

    @ManagedProperty(value="#{gestorGeneral}") 
    private GestorGeneral gestorGeneral;
    
    private final PersonaJpaController personaController = new PersonaJpaController();
    
    private Persona persona = new Persona();
    
    private Date fechaActual = new Date();
    
    private Date fechaMinima = new Date();
    
    private List<Titulo> listaTitulos = null;
    
    private List<Persona> listaPersonas = null;

    /**
     * Creates a new instance of GestorPersona
     */
    public GestorPersona() {
        
    }
    @PostConstruct
    public void init() {
        try {
            listaPersonas = personaController.listPersonas();
        } catch (Exception ex) {
            Logger.getLogger(GestorPersona.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    public String initCrearPersona() {
        persona = new Persona();
        fechaActual = new Date();
        fechaMinima = new Date();
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaMinima);        
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        calendar.add(Calendar.YEAR, -50);
        fechaMinima = calendar.getTime();
        
        persona.setFechaVinculacion(fechaActual);
        persona.setFechaNacimiento(fechaActual);
        
        listaTitulos = new ArrayList<>();
        
        return "manejoPersona";
    }
    
    public String agregarTitulo() {
        Titulo nuevoTitulo = new Titulo();
        nuevoTitulo.setNivel(3);
        nuevoTitulo.setNombre("Nuevo título");
        nuevoTitulo.setUniversidad(null);
        listaTitulos.add(nuevoTitulo);
        return "";
    }
    
    public String actualizarListado() {
        try {
            listaPersonas = personaController.listPersonas();            
        } catch (Exception ex) {
            Logger.getLogger(GestorPersona.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public static GestorPersona getInstance() {
        javax.faces.context.FacesContext facesContext = javax.faces.context.FacesContext.getCurrentInstance();
        javax.el.ELContext context = facesContext.getELContext();
        javax.el.ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorPersona}", GestorPersona.class);
        return (GestorPersona) ex.getValue(context);
    }    
    
    public String guardar() {
        try {
            personaController.create(persona);
            return "index";
        } catch (Exception ex) {
            Logger.getLogger(GestorPersona.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public List<Persona> getListaPersonas() {
        return listaPersonas;
    }

    public void setListaPersonas(List<Persona> listaPersonas) {
        this.listaPersonas = listaPersonas;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Date getFechaMinima() {
        return fechaMinima;
    }

    public Date getFechaActual() {
        return fechaActual;
    }

    public List<Titulo> getListaTitulos() {
        return listaTitulos;
    }

    public void setListaTitulos(List<Titulo> listaTitulos) {
        this.listaTitulos = listaTitulos;
    }

}
