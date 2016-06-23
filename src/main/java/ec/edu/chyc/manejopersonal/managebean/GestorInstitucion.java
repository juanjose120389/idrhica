/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.InstitucionJpaController;
import ec.edu.chyc.manejopersonal.entity.Financiamiento;
import ec.edu.chyc.manejopersonal.entity.Institucion;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

/**
 *
 * @author marcelocaj
 */
@Named(value = "gestorInstitucion")
@SessionScoped
public class GestorInstitucion implements Serializable {
    
    private final InstitucionJpaController institucionController = new InstitucionJpaController();
    private List<Institucion> listaInstituciones = new ArrayList<>();
    
    private Institucion institucion = null;

    private List<Institucion> listaInstitucionesAgregadas = new ArrayList<>();
    //private Long idInstitucionGen = -1L;
    
    public GestorInstitucion() {
    }
    
    @PostConstruct
    public void init() {        
    }
    
    public String initListarInstituciones() {
        actualizarListaInstituciones();

        // listaAutores.clear();
        return "listaInstituciones";
    }

    private void inicializarManejoInstitucion() {
        institucion = null;
        
        actualizarListaInstituciones();
        listaInstitucionesAgregadas.clear();
    }
    
    private void cargarDatosInstitucion(Long id){
        institucion = institucionController.findInstitucion(id);
        
        Double total = 0.0;
        for (Financiamiento financiamiento : institucion.getFinanciamientosCollection()) {
            total += financiamiento.getMonto();
        }        
        institucion.setTotalFinanciamientos(total);
    }
    
    public String initVerInstitucion(Long id) {
        inicializarManejoInstitucion();
        
        cargarDatosInstitucion(id);
        
        return "verInstitucion";
    }    
    
    public void actualizarListaInstituciones() {
        try {
            listaInstituciones = institucionController.listInstituciones();
        } catch (Exception ex) {
            Logger.getLogger(GestorProyecto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    public static GestorInstitucion getInstance() {
        javax.faces.context.FacesContext facesContext = javax.faces.context.FacesContext.getCurrentInstance();
        javax.el.ELContext context = facesContext.getELContext();
        javax.el.ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorInstitucion}", GestorInstitucion.class);
        return (GestorInstitucion) ex.getValue(context);
    }      

    public List<Institucion> getListaInstituciones() {
        return listaInstituciones;
    }

    public void setListaInstituciones(List<Institucion> listaInstituciones) {
        this.listaInstituciones = listaInstituciones;
    }

    public List<Institucion> getListaInstitucionesAgregadas() {
        return listaInstitucionesAgregadas;
    }

    public void setListaInstitucionesAgregadas(List<Institucion> listaInstitucionesAgregadas) {
        this.listaInstitucionesAgregadas = listaInstitucionesAgregadas;
    }

    public Institucion getInstitucion() {
        return institucion;
    }

    public void setInstitucion(Institucion institucion) {
        this.institucion = institucion;
    }
    
}
