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
import ec.edu.chyc.manejopersonal.entity.GrupoInvestigacion;
import ec.edu.chyc.manejopersonal.entity.Institucion;
import ec.edu.chyc.manejopersonal.entity.LineaInvestigacion;
import ec.edu.chyc.manejopersonal.entity.Lugar;
import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.entity.Proyecto;
import ec.edu.chyc.manejopersonal.managebean.util.BeansUtils;
import ec.edu.chyc.manejopersonal.util.FechaUtils;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

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
    //private List<Institucion> listaInstitucionesAgregadas = new ArrayList<>();
    private List<Lugar> listaLugaresSeleccionados = new ArrayList<>();
    private List<Lugar> listaLugares = new ArrayList<>();
    private List<Lugar> listaLugaresAgregados = new ArrayList<>();
    private List<GrupoInvestigacion> listaGruposInvestigacion = new ArrayList<>();
    private List<GrupoInvestigacion> listaGruposInvestigacionAgregados = new ArrayList<>();
    private List<LineaInvestigacion> listaLineasInvestigacion = new ArrayList<>();
    private List<LineaInvestigacion> listaLineasInvestigacionAgregados = new ArrayList<>();
    private Long idFinanciamientoGen = -1L;
    private Long idInstitucionGen = -1L;
    private Long idLugarGen = -1L;
    private Long idGrupoInvestigacionGen = -1L;
    private Long idLineaInvestigacionGen = -1L;
    private String idTabSel = "";
    private int  indexTabSel = 0;

    private GrupoInvestigacion grupoInvestigacion = null;
    private LineaInvestigacion lineaInvestigacion = null;
    private Financiamiento financiamientoActual = null;
    
    private boolean mostrarDlgInstitucion = false;
    private boolean mostrarDlgLugar = false;
    private boolean mostrarDlgGrupoInvestigacion = false;
    private boolean mostrarDlgLineaInvestigacion = false;
    private boolean hayExtensionFinalizacion = false;
    private boolean modoModificar = false;
    
    private Institucion institucion = null;
    
    private Lugar lugar = null;
    private Lugar lugarNuevo = null;

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
        BeansUtils.ejecutarJS("PF('dlgInstitucion').show()");
    }
    public void onTabChange(TabChangeEvent event) {
        String id = event.getTab().getId();
        
        idTabSel = id;
        if (id.equals("tabLugarExistente")) {
            indexTabSel = 0;
        } else {
            indexTabSel = 1;
        }
        //RequestContext.getCurrentInstance().update("formContenido:tabsLugar");
    }    
    public void abrirNuevoLugarDlg() {
        this.lugarNuevo = new Lugar();
        idTabSel = "tabLugarExistente";
        indexTabSel = 0;
        mostrarDlgLugar = true;
        RequestContext.getCurrentInstance().update("formContenido:divDialogs");
        BeansUtils.ejecutarJS("PF('dlgLugar').show()");
    }
    public void guardarLugar() {

        if (idTabSel.equals("tabLugarExistente")) {
            listaLugaresSeleccionados.add(lugar);
        }
        else {
            lugarNuevo.setId(idLugarGen);
            listaLugaresSeleccionados.add(lugarNuevo);
            idLugarGen--;
            listaLugaresAgregados.add(lugarNuevo);
        }
        
        //proyecto.setLugar(lugar);
        //proyecto.getLugaresCollection().add(lugar);
        
        lugarNuevo = new Lugar();
        mostrarDlgLugar = false;
        BeansUtils.ejecutarJS("PF('dlgLugar').hide()");
    }    
    public void guardarGrupoInvestigacion() {
        grupoInvestigacion.setId(idGrupoInvestigacionGen);
        idGrupoInvestigacionGen--;
        listaGruposInvestigacionAgregados.add(grupoInvestigacion);
        proyecto.setGrupoInvestigacion(grupoInvestigacion);
        
        grupoInvestigacion = new GrupoInvestigacion();
        mostrarDlgGrupoInvestigacion = false;
        BeansUtils.ejecutarJS("PF('dlgGrupoInvestigacion').hide()");        
    }
    public void guardarLineaInvestigacion() {
        lineaInvestigacion.setId(idLineaInvestigacionGen);
        idLineaInvestigacionGen--;
        listaLineasInvestigacionAgregados.add(lineaInvestigacion);
        proyecto.setLineaInvestigacion(lineaInvestigacion);
        
        lineaInvestigacion = new LineaInvestigacion();
        mostrarDlgLineaInvestigacion = false;
        BeansUtils.ejecutarJS("PF('dlgLineaInvestigacion').hide()");        
    }
    public void guardarInstitucion() {
        institucion.setId(idInstitucionGen);
        financiamientoActual.setInstitucion(institucion);
        idInstitucionGen--;
        GestorInstitucion.getInstance().getListaInstitucionesAgregadas().add(institucion);
        
        institucion = new Institucion();
        mostrarDlgInstitucion = false;
        BeansUtils.ejecutarJS("PF('dlgInstitucion').hide()");
    }
    public void onPersonaNuevaChosen(SelectEvent event) {
        List<Persona> listaResultao = (List<Persona>) event.getObject();
        //agregarPersonaALista(listaPersonasSel, listaCodirectores);
    }    
    
    public void onCloseDlgInstitucion() {
        mostrarDlgInstitucion = false;
    }
    public void onCloseDlgLugar() {
        mostrarDlgLugar = false;
    }
    public void onCloseDlgGrupoInvestigacion() {
        mostrarDlgGrupoInvestigacion = false;
    }

    public void onCloseDlgLineaInvestigacion() {
        mostrarDlgLineaInvestigacion = false;
    }
    
    public void quitarObservatorio(Lugar observatorioQuitar) {
        listaLugaresSeleccionados.remove(observatorioQuitar);
    }
    
    public void quitarFinanciamiento(Financiamiento financiamientoQuitar) {
        listaFinanciamientos.remove(financiamientoQuitar);
    }

    public void agregarLugar() {
        if (!listaLugares.isEmpty()) {
            listaLugaresSeleccionados.add(listaLugares.get(0));
        }
        else if (!listaLugaresAgregados.isEmpty()) {
            listaLugaresSeleccionados.add(listaLugaresAgregados.get(0));
        }
        else {
            Lugar nuevoLugar = new Lugar();
            nuevoLugar.setNombre("Nuevo lugar");
            nuevoLugar.setId(idLugarGen);
            idLugarGen--;
        }
    }

    public void agregarGrupoInvestigacion() {
        
        
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

    public void actualizarListaLugares() {
        try {
            listaLugares = proyectoController.listLugares();
        } catch (Exception ex) {
            Logger.getLogger(GestorProyecto.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public void actualizarListaGruposInvestigacion() {
        try {
            listaGruposInvestigacion = proyectoController.listGruposInvestigacion();
        } catch (Exception ex) {
            Logger.getLogger(GestorProyecto.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
    }
    
    public void actualizarListaLineasInvestigacion() {
        try {
            listaLineasInvestigacion = proyectoController.listLineasInvestigacion();
        } catch (Exception ex) {
            Logger.getLogger(GestorProyecto.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
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
    
    public String initImprimirProyecto() {
        if (proyecto == null || proyecto.getId() == null || proyecto.getId() < 0) {
            return "";
        }
        return "imprimirProyecto?faces-redirect=true";
    }

    public String initVerProyecto(Long id) {
        inicializarManejoProyecto();
        proyecto = proyectoController.findProyecto(id, ProyectoJpaController.Flag.ALL_OPTS);
        listaFinanciamientos = new ArrayList<>(proyecto.getFinanciamientosCollection());
        
        Double total = 0.0;
        for (Financiamiento financiamiento : proyecto.getFinanciamientosCollection()) {
            total += financiamiento.getMonto();
        }
        proyecto.setTotalFinanciamientos(total);
        
        colocarDuracion(proyecto);

        modoModificar = false;

        return "verProyecto";
    }
    
    public String initModificarProyecto(Long id) {
        inicializarManejoProyecto();
        proyecto = proyectoController.findProyecto(id, EnumSet.of(ProyectoJpaController.Flag.INC_FINANCIAMIENTOS, ProyectoJpaController.Flag.INC_LUGARES));
        listaFinanciamientos = new ArrayList<>(proyecto.getFinanciamientosCollection());
        modoModificar = true;
        
        listaLugaresSeleccionados = new ArrayList(proyecto.getLugaresCollection());
        hayExtensionFinalizacion = proyecto.getFechaFinEnDocumento() != null && !proyecto.getFechaFinEnDocumento().equals(proyecto.getFechaFin());
        
        //hayExtensionFinalizacion = !proyecto.getFechaFinEnDocumento().equals(proyecto.getFechaFin());
        
        return "manejoProyecto";
    }
    
    private void inicializarManejoProyecto() {
        GestorDialogListaPersonas.getInstance().resetearDialog();
        proyecto = new Proyecto();
        GestorContrato.getInstance().actualizarListaContrato();
        GestorPersona.getInstance().actualizarListado();
        GestorInstitucion.getInstance().actualizarListaInstituciones();
        GestorInstitucion.getInstance().getListaInstitucionesAgregadas().clear();
        listaGruposInvestigacionAgregados.clear();
        listaLineasInvestigacionAgregados.clear();
        actualizarListaLugares();
        actualizarListaGruposInvestigacion();
        actualizarListaLineasInvestigacion();

        institucion = null;
        mostrarDlgInstitucion = false;
        mostrarDlgLugar = false;
        idFinanciamientoGen = -1L;
        hayExtensionFinalizacion = false;

        listaLugaresSeleccionados.clear();        
        listaFinanciamientos.clear();        
        listaLugaresAgregados.clear();
    }

    public String initCrearProyecto() {
        inicializarManejoProyecto();
        modoModificar = false;

        return "manejoProyecto";
    }
    
    public String convertirListaProyectos(Collection<Proyecto> listaConvertir) {
        String r = "";
        int c = 0;
        for (Proyecto proy : listaConvertir) {
            r += proy.getTitulo();
            if (c < listaConvertir.size() - 1) {
                r += ", ";
            }
            c++;
        }
        return r;
    }
    public void abrirNuevoGrupoInvestigacionDlg() {
        this.grupoInvestigacion = new GrupoInvestigacion();

        mostrarDlgGrupoInvestigacion = true;
        RequestContext.getCurrentInstance().update("formContenido:divDialogs");
        BeansUtils.ejecutarJS("PF('dlgGrupoInvestigacion').show()");
    }
    public void abrirNuevaLineaInvestigacionDlg() {
        this.lineaInvestigacion = new LineaInvestigacion();

        mostrarDlgLineaInvestigacion = true;
        RequestContext.getCurrentInstance().update("formContenido:divDialogs");
        BeansUtils.ejecutarJS("PF('dlgLineaInvestigacion').show()");
    }
    
    public void abrirDialogNuevaPersona() {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", true);
        options.put("draggable", true);
        options.put("width", "75%");
        options.put("modal", true);
        options.put("contentWidth", "100%");
        GestorDialogListaPersonas.getInstance().prepararApertura();
        RequestContext.getCurrentInstance().openDialog("dialogListaPersonas", options, null);
    }
    
    public String acortarTitulo(Proyecto proyecto) {
        final int maxLongitud = 80;
        if (proyecto.getTitulo().length() > maxLongitud) {
            return proyecto.getTitulo().substring(0, maxLongitud) + "...";
        }
        return proyecto.getTitulo();
    }

    public void colocarDuracion(Proyecto proy) {
        if (proy.getFechaInicio() != null && (proy.getFechaFin() != null || proy.getFechaFinEnDocumento() != null)) {
            LocalDateTime fechaInicio = FechaUtils.asLocalDateTime(proy.getFechaInicio());
            LocalDateTime fechaFin;

            if (proy.getFechaFin() != null) {
                fechaFin = FechaUtils.asLocalDateTime(proy.getFechaFin());
            } else {
                fechaFin = FechaUtils.asLocalDateTime(proy.getFechaFinEnDocumento());
            }

            long meses = ChronoUnit.MONTHS.between(fechaInicio, fechaFin);
            proy.setDuracion((int) meses);
        } else {
            proy.setDuracion(null);
        }
    }
    
    public String initListarProyectos() {
        actualizarListaProyecto();
        for (Proyecto proy : listaProyecto) {
            //para cada proyecto, calcular el numero de meses
            colocarDuracion(proy);
        }
        return "listaProyecto";
    }

    public List<Proyecto> getListaProyecto() {
        return listaProyecto;
    }

    public void setListaProyecto(List<Proyecto> listaProyecto) {
        this.listaProyecto = listaProyecto;
    }

    public String guardar() {        
        proyecto.setFinanciamientosCollection(listaFinanciamientos);
        if (!hayExtensionFinalizacion) {
            proyecto.setFechaFin(proyecto.getFechaFinEnDocumento());
        }
        
        if (proyecto.getFechaInicio() != null && proyecto.getFechaFinEnDocumento() != null) {
            LocalDate fechaInicio = FechaUtils.asLocalDate(proyecto.getFechaInicio());
            LocalDate fechaFinDoc = FechaUtils.asLocalDate(proyecto.getFechaFinEnDocumento());
            LocalDate fechaFin = FechaUtils.asLocalDate(proyecto.getFechaFin());
        
            if (fechaFinDoc.isBefore(fechaInicio) || fechaFin.isBefore(fechaInicio)) {
                GestorMensajes.getInstance().mostrarMensajeWarn("La fecha de finalización del proyecto no puede ser menor al inicio del mismo.");
                return "";
            }
            if (fechaFin.isBefore(fechaFinDoc)) {
                GestorMensajes.getInstance().mostrarMensajeWarn("La fecha extendida de finalización del proyecto no puede ser menor a la fecha original de finalización.");
                return "";
            }
        }
        
        proyecto.setLugaresCollection(new HashSet(listaLugaresSeleccionados));
        
        try {
            if (modoModificar) {
                proyectoController.edit(proyecto);
            } else {
                proyectoController.create(proyecto);
            }
            
            GestorInstitucion.getInstance().getListaInstitucionesAgregadas().clear();
            return initListarProyectos();
            //return "index";
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

    public List<Lugar> getListaLugares() {
        return listaLugares;
    }

    public void setListaLugares(List<Lugar> listaLugares) {
        this.listaLugares = listaLugares;
    }

    public List<Lugar> getListaLugaresAgregados() {
        return listaLugaresAgregados;
    }

    public void setListaLugaresAgregados(List<Lugar> listaLugaresAgregados) {
        this.listaLugaresAgregados = listaLugaresAgregados;
    }

    public Lugar getLugar() {
        return lugar;
    }

    public void setLugar(Lugar lugar) {
        this.lugar = lugar;
    }

    public boolean isMostrarDlgLugar() {
        return mostrarDlgLugar;
    }

    public void setMostrarDlgLugar(boolean mostrarDlgLugar) {
        this.mostrarDlgLugar = mostrarDlgLugar;
    }

    public List<Lugar> getListaLugaresSeleccionados() {
        return listaLugaresSeleccionados;
    }

    public void setListaLugaresSeleccionados(List<Lugar> listaLugaresSeleccionados) {
        this.listaLugaresSeleccionados = listaLugaresSeleccionados;
    }

    public Lugar getLugarNuevo() {
        return lugarNuevo;
    }

    public void setLugarNuevo(Lugar lugarNuevo) {
        this.lugarNuevo = lugarNuevo;
    }

    public String getIdTabSel() {
        return idTabSel;
    }

    public void setIdTabSel(String idTabSel) {
        this.idTabSel = idTabSel;
    }

    public List<GrupoInvestigacion> getListaGruposInvestigacion() {
        return listaGruposInvestigacion;
    }

    public void setListaGruposInvestigacion(List<GrupoInvestigacion> listaGruposInvestigacion) {
        this.listaGruposInvestigacion = listaGruposInvestigacion;
    }

    public List<GrupoInvestigacion> getListaGruposInvestigacionAgregados() {
        return listaGruposInvestigacionAgregados;
    }

    public void setListaGruposInvestigacionAgregados(List<GrupoInvestigacion> listaGruposInvestigacionAgregados) {
        this.listaGruposInvestigacionAgregados = listaGruposInvestigacionAgregados;
    }

    public List<LineaInvestigacion> getListaLineasInvestigacion() {
        return listaLineasInvestigacion;
    }

    public void setListaLineasInvestigacion(List<LineaInvestigacion> listaLineasInvestigacion) {
        this.listaLineasInvestigacion = listaLineasInvestigacion;
    }

    public List<LineaInvestigacion> getListaLineasInvestigacionAgregados() {
        return listaLineasInvestigacionAgregados;
    }

    public void setListaLineasInvestigacionAgregados(List<LineaInvestigacion> listaLineasInvestigacionAgregados) {
        this.listaLineasInvestigacionAgregados = listaLineasInvestigacionAgregados;
    }

    public GrupoInvestigacion getGrupoInvestigacion() {
        return grupoInvestigacion;
    }

    public void setGrupoInvestigacion(GrupoInvestigacion grupoInvestigacion) {
        this.grupoInvestigacion = grupoInvestigacion;
    }

    public LineaInvestigacion getLineaInvestigacion() {
        return lineaInvestigacion;
    }

    public void setLineaInvestigacion(LineaInvestigacion lineaInvestigacion) {
        this.lineaInvestigacion = lineaInvestigacion;
    }

    public boolean isMostrarDlgGrupoInvestigacion() {
        return mostrarDlgGrupoInvestigacion;
    }

    public void setMostrarDlgGrupoInvestigacion(boolean mostrarDlgGrupoInvestigacion) {
        this.mostrarDlgGrupoInvestigacion = mostrarDlgGrupoInvestigacion;
    }

    public boolean isMostrarDlgLineaInvestigacion() {
        return mostrarDlgLineaInvestigacion;
    }

    public void setMostrarDlgLineaInvestigacion(boolean mostrarDlgLineaInvestigacion) {
        this.mostrarDlgLineaInvestigacion = mostrarDlgLineaInvestigacion;
    }

    public int getIndexTabSel() {
        return indexTabSel;
    }

    public void setIndexTabSel(int indexTabSel) {
        this.indexTabSel = indexTabSel;
    }

    
}
