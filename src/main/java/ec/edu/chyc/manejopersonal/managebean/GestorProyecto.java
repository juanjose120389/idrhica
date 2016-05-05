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
import ec.edu.chyc.manejopersonal.entity.Financiamiento;
import ec.edu.chyc.manejopersonal.entity.Institucion;
import ec.edu.chyc.manejopersonal.entity.Proyecto;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
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
    private List<Financiamiento> listaFinanciamientos = new ArrayList<>();
    private List<Institucion> listaInstitucionesAgregadas = new ArrayList<>();

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

    public void abrirNuevaInstitucionDlg(Financiamiento financiamientoActual) {
        
    }
    
    public void quitarFinanciamiento(Financiamiento financiamientoQuitar) {
        listaFinanciamientos.remove(financiamientoQuitar);
    }
    
    public void agregarFinanciamiento() {
        Financiamiento nuevoFinan = new Financiamiento();
        listaFinanciamientos.add(nuevoFinan);
        List<Institucion> listaInstituciones = GestorInstitucion.getInstance().getListaInstituciones();
        
        if (!listaInstituciones.isEmpty()) {
            nuevoFinan.setInstitucion(listaInstituciones.get(0));            
        }
        nuevoFinan.setMonto(0.0);
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

    public void cargarInformacionProyecto(Long id) {
        proyecto = proyectoController.findEntity(id);
    }
    
    public String initVerProyecto() {
        
        return "verProyecto";
    }
    
    public String initModificarProyecto(Long id) {
        return "manejoProyecto";
    }
    
    public String initCrearProyecto() {
        proyecto = new Proyecto();
        GestorContrato.getInstance().actualizarListaContrato();
        GestorPersona.getInstance().actualizarListaPersonasConContrato();
        GestorInstitucion.getInstance().actualizarListaInstituciones();        

        listaFinanciamientos.clear();
        listaInstitucionesAgregadas.clear();
        
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

    public List<Financiamiento> getListaFinanciamientos() {
        return listaFinanciamientos;
    }

    public void setListaFinanciamientos(List<Financiamiento> listaFinanciamientos) {
        this.listaFinanciamientos = listaFinanciamientos;
    }

    public List<Institucion> getListaInstitucionesAgregadas() {
        return listaInstitucionesAgregadas;
    }

    public void setListaInstitucionesAgregadas(List<Institucion> listaInstitucionesAgregadas) {
        this.listaInstitucionesAgregadas = listaInstitucionesAgregadas;
    }

}
