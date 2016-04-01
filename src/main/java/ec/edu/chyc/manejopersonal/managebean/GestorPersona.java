/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.PersonaJpaController;
import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.entity.Titulo;
import ec.edu.chyc.manejopersonal.entity.Universidad;
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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;

/**
 *
 * @author Marcelo
 */
@Named(value = "gestorPersona")
@SessionScoped
public class GestorPersona implements Serializable {
    
    private final PersonaJpaController personaController = new PersonaJpaController();
    
    private Persona persona = new Persona();
    
    private Date fechaActual = new Date();
    
    private Date fechaMinima = new Date();
    
    private List<Titulo> listaTitulos = null;
    
    private List<Persona> listaPersonas = null;
    
    private Universidad universidad = null;
    
    private List<Universidad> listaUniversidadesAgregadas = new ArrayList<>();

    private long idTituloGenerado = -1;
    private Titulo tituloDeUniversidad;
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
        universidad = new Universidad();
    }    
    public String abrir() {
        
        ejecutarJS("PF('dlgUniversidad').show()");
        return "";
    }
    public void abrirUniversidadDialog(Titulo tituloDeUniversidad) {
        this.tituloDeUniversidad = tituloDeUniversidad;
        universidad = new Universidad();
        ejecutarJS("PF('dlgUniversidad').show()");
        RequestContext.getCurrentInstance().update("formContenido:dlgUniversidad");
    }
    public String guardarUniversidad() {
        listaUniversidadesAgregadas.add(universidad);
        tituloDeUniversidad.setUniversidad(universidad);
        universidad = new Universidad();
        
        ejecutarJS("PF('dlgUniversidad').hide()");
        return "";
    }
    public void ejecutarJS(String codigo) {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute(codigo);
    }    
    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
         
        if(newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }
    public void quitarTitulo(Titulo tituloQuitar) {
        listaTitulos.remove(tituloQuitar);
    }
    public String initCrearPersona() {
        persona = new Persona();
        fechaActual = new Date();
        fechaMinima = new Date();
        
        idTituloGenerado = -1;
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaMinima);        
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        calendar.add(Calendar.YEAR, -50);
        fechaMinima = calendar.getTime();
        
        persona.setFechaVinculacion(fechaActual);
        persona.setFechaNacimiento(fechaActual);
        
        listaTitulos = new ArrayList<>();
        
        GestorGeneral.getInstance().actualizarListaUniversidades();
        listaUniversidadesAgregadas.clear();
        
        return "manejoPersona";
    }
    
    
    public String agregarTitulo() {
        Titulo nuevoTitulo = new Titulo();
        
        nuevoTitulo.setId(idTituloGenerado);
        nuevoTitulo.setNivel(3);
        nuevoTitulo.setNombre("Nuevo título");
        nuevoTitulo.setUniversidad(null);
        
        List<Universidad> listaUniversidades = GestorGeneral.getInstance().getListaUniversidades();
        if (listaUniversidades.size() > 0) {
            nuevoTitulo.setUniversidad( listaUniversidades.get(0) );
        }
        
        listaTitulos.add(nuevoTitulo);
        universidad = new Universidad();
        
        idTituloGenerado--;
        
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
    public List<Universidad> completarUniversidad(String query) {
        List<Universidad> list = GestorGeneral.getInstance().getListaUniversidades();
        List<Universidad> listResults = new ArrayList<>();
         
        for (Universidad uni : list) {
            if(uni.getNombre().toLowerCase().startsWith(query)) {
                listResults.add(uni);
            }
        }         
        return listResults;
    }    
    public String guardar() {
        try {
            persona.setTitulosCollection(listaTitulos);
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

    public Universidad getUniversidad() {
        return universidad;
    }

    public void setUniversidad(Universidad universidad) {
        this.universidad = universidad;
    }

    public List<Universidad> getListaUniversidadesAgregadas() {
        return listaUniversidadesAgregadas;
    }

    public void setListaUniversidadesAgregadas(List<Universidad> listaUniversidadesAgregadas) {
        this.listaUniversidadesAgregadas = listaUniversidadesAgregadas;
    }

}
