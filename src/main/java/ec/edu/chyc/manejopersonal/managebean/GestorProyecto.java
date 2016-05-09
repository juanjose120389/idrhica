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
import ec.edu.chyc.manejopersonal.managebean.util.BeanUtils;
import ec.edu.chyc.manejopersonal.util.FechaUtils;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

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
    private Long idFinanciamientoGen = -1L;
    private Long idInstitucionGen = -1L;
    
    private Financiamiento financiamientoActual = null;
    
    private boolean mostrarDlgInstitucion = false;
    private boolean hayExtensionFinalizacion = false;
    private boolean modoModificar = false;
    
    private Institucion institucion = null;

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
        this.institucion = new Institucion();
        
        this.financiamientoActual = financiamientoActual;
        mostrarDlgInstitucion = true;
        RequestContext.getCurrentInstance().update("formContenido:divDialogs");
        BeanUtils.ejecutarJS("PF('dlgInstitucion').show()");
    }
    public void guardarInstitucion() {
        institucion.setId(idInstitucionGen);
        financiamientoActual.setInstitucion(institucion);
        idInstitucionGen--;
        listaInstitucionesAgregadas.add(institucion);
        
        institucion = new Institucion();
        mostrarDlgInstitucion = false;
        BeanUtils.ejecutarJS("PF('dlgInstitucion').hide()");
    }
    
    
    public void onCloseDlgInstitucion() {
        mostrarDlgInstitucion = false;
    }

    public void quitarFinanciamiento(Financiamiento financiamientoQuitar) {
        listaFinanciamientos.remove(financiamientoQuitar);
    }

    public void agregarFinanciamiento() {
        Financiamiento nuevoFinan = new Financiamiento();
        listaFinanciamientos.add(nuevoFinan);
        nuevoFinan.setId(idFinanciamientoGen);
        idFinanciamientoGen--;
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

    public void cargarInformacionProyecto(Long id) {
        proyecto = proyectoController.findEntity(id);
    }

    public String initVerProyecto(Long id) {
        inicializarManejoProyecto();
        proyecto = proyectoController.findProyecto(id, true, true, true, true, true);
        listaFinanciamientos = new ArrayList<>(proyecto.getFinanciamientosCollection());
        
        modoModificar = false;

        return "verProyecto";
    }
    
    public String initModificarProyecto(Long id) {
        inicializarManejoProyecto();
        proyecto = proyectoController.findProyecto(id, true, false, false, false, false);        
        listaFinanciamientos = new ArrayList<>(proyecto.getFinanciamientosCollection());
        modoModificar = true;
        
        hayExtensionFinalizacion = !proyecto.getFechaFinEnDocumento().equals(proyecto.getFechaFin());
        
        return "manejoProyecto";
    }
    
    private void inicializarManejoProyecto() {
        proyecto = new Proyecto();
        GestorContrato.getInstance().actualizarListaContrato();
        GestorPersona.getInstance().actualizarListaPersonasConContrato();
        GestorInstitucion.getInstance().actualizarListaInstituciones();

        institucion = null;
        mostrarDlgInstitucion = false;
        idFinanciamientoGen = -1L;
        hayExtensionFinalizacion = false;
        
        listaFinanciamientos.clear();
        listaInstitucionesAgregadas.clear();        
    }

    public String initCrearProyecto() {
        inicializarManejoProyecto();
        modoModificar = false;

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
        LocalDate fechaInicio = FechaUtils.asLocalDate(proyecto.getFechaInicio());
        LocalDate fechaFinDoc = FechaUtils.asLocalDate(proyecto.getFechaFinEnDocumento());
        
        proyecto.setFinanciamientosCollection(listaFinanciamientos);
        if (!hayExtensionFinalizacion) {
            proyecto.setFechaFin(proyecto.getFechaFinEnDocumento());
        }
        LocalDate fechaFin = FechaUtils.asLocalDate(proyecto.getFechaFin());
        
        if (fechaFinDoc.isBefore(fechaInicio) || fechaFin.isBefore(fechaInicio)) {
            GestorMensajes.getInstance().mostrarMensajeWarn("La fecha de finalización del proyecto no puede ser menor al inicio del mismo.");
            return "";
        }
        if (fechaFin.isBefore(fechaFinDoc)) {
            GestorMensajes.getInstance().mostrarMensajeWarn("La fecha extendida de finalización del proyecto no puede ser menor a la fecha original de finalización.");
            return "";
        }
        
        try {
            if (modoModificar) {
                proyectoController.edit(proyecto);
            } else {
                proyectoController.create(proyecto);
            }
            
            listaInstitucionesAgregadas.clear();
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

    public boolean isMostrarDlgInstitucion() {
        return mostrarDlgInstitucion;
    }

    public void setMostrarDlgInstitucion(boolean mostrarDlgInstitucion) {
        this.mostrarDlgInstitucion = mostrarDlgInstitucion;
    }
    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public Institucion getInstitucion() {
        return institucion;
    }

    public void setInstitucion(Institucion institucion) {
        this.institucion = institucion;
    }

    public boolean isHayExtensionFinalizacion() {
        return hayExtensionFinalizacion;
    }

    public void setHayExtensionFinalizacion(boolean hayExtensionFinalizacion) {
        this.hayExtensionFinalizacion = hayExtensionFinalizacion;
    }

    public boolean isModoModificar() {
        return modoModificar;
    }

    public void setModoModificar(boolean modoModificar) {
        this.modoModificar = modoModificar;
    }
    
}
