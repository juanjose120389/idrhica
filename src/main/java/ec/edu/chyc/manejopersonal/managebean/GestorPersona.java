/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.PersonaJpaController;
import ec.edu.chyc.manejopersonal.entity.Firma;
import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.entity.Persona.TipoPersona;
import ec.edu.chyc.manejopersonal.entity.PersonaFirma;
import ec.edu.chyc.manejopersonal.entity.PersonaTitulo;
import ec.edu.chyc.manejopersonal.entity.Titulo;
import ec.edu.chyc.manejopersonal.entity.Universidad;
import static ec.edu.chyc.manejopersonal.managebean.util.BeansUtils.ejecutarJS;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author Marcelo
 */
@Named(value = "gestorPersona")
@SessionScoped
public class GestorPersona implements Serializable {
    
    private final PersonaJpaController personaController = new PersonaJpaController();
    
    private enum Modo {
        AGREGAR,
        EDITAR,
        VER
    }
    
    private Persona persona = new Persona();
    
    private Date fechaActual = new Date();
    
    private boolean mostrarDlgUniversidad; //para que no renderice el dlgUniversidad
    private boolean mostrarDlgTitulo; //para que no renderice dlgTitulo
    
    private Date filtroFechaInicio;
    private Date filtroFechaFin;
    
    private List<PersonaTitulo> listaPersonaTitulos = null; //para almacenar los títulos que tiene la persona
    
    private List<Persona> listaPersonas = new ArrayList<>();
    private List<Persona> listaPersonasConExternos = new ArrayList<>(); //Lista de personas includendo contactos externos
    private List<Persona> listaPersonasSoloExternos = new ArrayList(); //Lista de personas solo de tipo externo
    //private List<Persona> listaPersonasSel = new ArrayList();
    
    private int tabActivo = 0;
    
    private Universidad universidad = null;
    private Titulo titulo = null;
    
    //private List<Universidad> listaUniversidadesAgregadas = new ArrayList<>();
    private List<Titulo> listaTitulosAgregados = new ArrayList<>();
    private List<PersonaFirma> listaPersonaFirmas = new ArrayList<>();
    
    private long idTituloGenerado = -1;
    private long idUniversidadGenerada = -1;
    private long idTituloPersonaGenerado = -1;
    private long idFirmaGen = -1L;
    private long idPersonaFirmaGen = -1L;
    private PersonaTitulo tituloDePersona;
    
    //private boolean modoModificar = false;
    private Modo modo;

    /**
     * Creates a new instance of GestorPersona
     */
    public GestorPersona() {
        
    }

    @PostConstruct
    public void init() {
        actualizarListado();
        /*try {
            listaPersonas = personaController.listPersonas();
        } catch (Exception ex) {
            Logger.getLogger(GestorPersona.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        universidad = new Universidad();
        titulo = new Titulo();
        tabActivo = 0;
    }    

    public void abrirTituloDialog(PersonaTitulo tituloDePersona) {
        this.tituloDePersona = tituloDePersona;
        titulo = new Titulo();
        mostrarDlgTitulo = true;
        //RequestContext.getCurrentInstance().update("formContenido:dlgTitulo");
        RequestContext.getCurrentInstance().update("formContenido:divDialogs");
        ejecutarJS("PF('dlgTitulo').show()");        
    }

    public void abrirUniversidadDialog(PersonaTitulo tituloDePersona) {
        this.tituloDePersona = tituloDePersona;
        universidad = new Universidad();
        mostrarDlgUniversidad = true;
        RequestContext.getCurrentInstance().update("formContenido:divDialogs");
        ejecutarJS("PF('dlgUniversidad').show()");        
    }

    public void guardarTitulo() {
        titulo.setId(idTituloGenerado);
        listaTitulosAgregados.add(titulo);
        tituloDePersona.setTitulo(titulo);
        titulo = new Titulo();
        
        idTituloGenerado--;
        ejecutarJS("PF('dlgTitulo').hide()");
        mostrarDlgTitulo = false;
    }

    public String guardarUniversidad() {
        universidad.setId(idUniversidadGenerada);
        GestorGeneral.getInstance().getListaUniversidadesAgregadas().add(universidad);
        tituloDePersona.setUniversidad(universidad);
        universidad = new Universidad();
        
        idUniversidadGenerada--;
        ejecutarJS("PF('dlgUniversidad').hide()");
        return "";
    }
    
    public void onUniversidadChange(javax.faces.event.AjaxBehaviorEvent event) {
        if (universidad == null) {
            
        }
    }

    public void onUniversidadSelect(SelectEvent event) {
        if (universidad == null) {
            
        }
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }
    
    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        
        if (newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void onCloseDlgTitulo() {
        mostrarDlgTitulo = false;
    }

    public void onCloseDlgUniversidad() {
        mostrarDlgUniversidad = false;
    }
    
    public boolean filtrarPorTipo(Object value, Object filter, Locale locale) {        
        if (filter == null) {
            return true;
        }
        TipoPersona tipoPersonaFilter = TipoPersona.valueOf((String)filter);

        TipoPersona tipoPersona = (TipoPersona)value;
        return (tipoPersonaFilter.equals(tipoPersona));
    }    
    
    public boolean filtrarPorFirma(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim();
        if(filterText == null||filterText.equals("")) {
            return true;
        }         
        if(value == null) {
            return false;
        }
        
        Collection<PersonaFirma> autores = (Collection) value;
        for (PersonaFirma per : autores) {
            if (StringUtils.containsIgnoreCase(per.getFirma().getNombre(), filterText)) {
                return true;
            }
        }        
        return false;
    }
    public void quitarFirma(PersonaFirma firmaQuitar) {
        listaPersonaFirmas.remove(firmaQuitar);
    }
    
    public void quitarPersonaTitulo(PersonaTitulo personaTituloQuitar) {
        listaPersonaTitulos.remove(personaTituloQuitar);
    }
    public boolean filterByDate(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim();
        if(filterText == null||filterText.equals("")) {
            return true;
        }
         
        if(value == null) {
            return false;
        }
        
        return false;
    }
    public void inicializarManejoPersona() {
        persona = new Persona();
        fechaActual = new Date();        
        
        modo = Modo.AGREGAR;
        
        mostrarDlgTitulo = false;
        mostrarDlgUniversidad = false;
        
        idTituloGenerado = -1;
        idUniversidadGenerada = -1;
        idTituloPersonaGenerado = -1;
        idFirmaGen = -1L;
        
        listaPersonaTitulos = new ArrayList<>();
        
        GestorGeneral.getInstance().actualizarListaUniversidades();
        GestorGeneral.getInstance().actualizarListaTitulos();
        GestorGeneral.getInstance().getListaUniversidadesAgregadas().clear();
        //listaUniversidadesAgregadas.clear();
        listaTitulosAgregados.clear();
        listaPersonaFirmas.clear();        
    }
    public void cargarInformacionPersona(Long id, boolean infoCompleta) {
        if (infoCompleta) {
            persona = personaController.findPersona(id, PersonaJpaController.Incluir.INC_TODOS.value());
        } else {
            //persona = personaController.findPersona(id, false, false, true, false, false, true);
            persona = personaController.findPersona(id, PersonaJpaController.Incluir.INC_TITULOS.value() | PersonaJpaController.Incluir.INC_FIRMAS.value());
        }
        //modoModificar = true;
        
        listaPersonaFirmas.clear();
        for (PersonaFirma personaFirma : persona.getPersonaFirmasCollection()) {
            listaPersonaFirmas.add(personaFirma);
        }
        
        listaPersonaTitulos = new ArrayList<>(persona.getPersonaTitulosCollection());

        //BeanUtils.copyProperties(tesista, (Tesista) persona);
    }
    public String initVerPersona(Long id) {
        inicializarManejoPersona();
        cargarInformacionPersona(id, true);     
        modo = Modo.VER;
        return "verPersona";        
    }

    public String initListarPersonas() {
        inicializarManejoPersona();
        actualizarListado();
        return "index";
    }
    
    public String initModificarPersona(Long id) {
        inicializarManejoPersona();
        cargarInformacionPersona(id, false);     
        modo = Modo.EDITAR;
        return "manejoPersona";
    }

    public String initCrearPersona() {
        inicializarManejoPersona();
        
        //modoModificar = false;
        modo = Modo.AGREGAR;
        
        /*LocalDate dtFechaNacDefault = LocalDateTime.now().toLocalDate();
        dtFechaNacDefault = dtFechaNacDefault.minusYears(28).withDayOfYear(1);
        
        persona.setFechaNacimiento(FechaUtils.asDate(dtFechaNacDefault));        */
        persona.setFechaNacimiento(null);
        
        return "manejoPersona";
    }
    
    public TipoPersona[] getTiposPersona() {
        return TipoPersona.values();
    }
    
    public void agregarFirma() {
        Firma nuevaFirma = new Firma();
        nuevaFirma.setId(idFirmaGen);
        nuevaFirma.setNombre("Apellido/s, Nombre/s.");
        
        PersonaFirma perFirma = new PersonaFirma();
        perFirma.setId(idPersonaFirmaGen);
        perFirma.setFirma(nuevaFirma);
        
        listaPersonaFirmas.add(perFirma);
        
        idFirmaGen--;
        idPersonaFirmaGen--;
    }
    
    //Agrega un título por defecto a la lista de títulos de la persona actual
    public String agregarTitulo() {
        PersonaTitulo personaTituloNuevo = new PersonaTitulo();        
        
        List<Universidad> listaUniversidades = GestorGeneral.getInstance().getListaUniversidades();
        List<Titulo> listaTitulos = GestorGeneral.getInstance().getListaTitulos();
        if (listaUniversidades.size() > 0) {
            personaTituloNuevo.setUniversidad(listaUniversidades.get(0));
        }
        
        if (listaTitulos.size() > 0) {
            personaTituloNuevo.setTitulo(listaTitulos.get(0));
        }
        personaTituloNuevo.setAnio(LocalDate.now().getYear());
        
        personaTituloNuevo.setId(idTituloPersonaGenerado);
        listaPersonaTitulos.add(personaTituloNuevo);

        //listaTitulos.add(nuevoTitulo);
        universidad = new Universidad();
        titulo = new Titulo();
        
        idTituloPersonaGenerado--;
        
        return "";
    }
    
    public void onTabChange(TabChangeEvent event) {        
        String id = event.getTab().getId();
        //DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formContenido:personasTable");
        if (id.equals("tabPersonal")) {
            /*BeansUtils.ejecutarJS("PF('personasTable').clearFilters();");
            if (!dataTable.getFilters().isEmpty()) {
                dataTable.getFilters().clear();
            }*/
            tabActivo = 0;
            //listaPersonasSel = listaPersonas;
        } else {
            tabActivo = 1;
            //listaPersonasSel = listaPersonasSoloExternos;
        }

        //FacesMessage msg = new FacesMessage("Tab Changed", "Active Tab: " + event.getTab().getTitle());
        //FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    //actualizar los listados de personas
    public String actualizarListado() {
        try {
            listaPersonasConExternos = personaController.listTodasPersonas();
            listaPersonas.clear();
            listaPersonasSoloExternos.clear();
            for (Persona per : listaPersonasConExternos) {
                if (per.getActivo() != null) {
                    listaPersonas.add(per);
                } else {
                    listaPersonasSoloExternos.add(per);
                }
            }
            //listaPersonasSel = listaPersonas;
            //listaPersonas = personaController.listPersonas();            
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

    //Devuelve una lista de las universidades que contengan el texto indicado
    public List<Universidad> completarUniversidad(String query) {
        List<Universidad> list = GestorGeneral.getInstance().getListaUniversidades();
        List<Universidad> listResults = new ArrayList<>();
        
        for (Universidad uni : list) {
            if (StringUtils.containsIgnoreCase(uni.getNombre(), query)) {
                listResults.add(uni);
            }
        }
        for (Universidad uni : GestorGeneral.getInstance().getListaUniversidadesAgregadas()) {
            if (StringUtils.containsIgnoreCase(uni.getNombre(), query)) {
                listResults.add(uni);
            }
        }
        return listResults;
    }
    //Convierte una lista de personas en String (separado por comas)
    public String convertirListaPersonas(Collection<Persona> listaConvertir) {
        String r = "";
        int c = 0;
        for (Persona per : listaConvertir) {
            r += per.getNombres() + " " + per.getApellidos();
            if (c < listaConvertir.size() - 1) {
                r += ", ";
            }
            c++;
        }
        return r;
    }

    public String guardar() {
        try {            
            
            persona.setPersonaTitulosCollection(listaPersonaTitulos);
            persona.setPersonaFirmasCollection(listaPersonaFirmas);
            
            //un Set para revisar que no se repitan las firmas
            Set<String> strFirmas = new HashSet<>();
            for (PersonaFirma perFirma : listaPersonaFirmas) {
                String strFirma = perFirma.getFirma().getNombre();
                if (strFirma.trim().isEmpty()) {
                    //si el texto de la firma es vacío, no es firma válida
                    GestorMensajes.getInstance().mostrarMensajeError("Existen firmas vacías en la lista.");
                    return "";
                }                
                strFirmas.add(strFirma.toLowerCase());
            }
            if (strFirmas.size() != listaPersonaFirmas.size()) {
                //si el tamaño del Set (que se llena solo con firmas diferentes) es diferente al de firmas ingresadas
                // indica que hay firmas repetidas
                GestorMensajes.getInstance().mostrarMensajeError("Existen firmas repetidas en la lista.");
                return "";
            }
            
            if (modo == Modo.EDITAR) {
                personaController.edit(persona);
            } else {
                personaController.create(persona);
            }
     
            actualizarListado();
            return "index";
        } catch (Exception ex) {
            Logger.getLogger(GestorPersona.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public void onDateSelectFilter(SelectEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
    }

    public boolean esModoVer() {
        return modo == Modo.VER;
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
    
    public Date getFechaActual() {
        return fechaActual;
    }
    
    public Universidad getUniversidad() {
        return universidad;
    }
    
    public void setUniversidad(Universidad universidad) {
        this.universidad = universidad;
    }
    
    public List<PersonaTitulo> getListaPersonaTitulos() {
        return listaPersonaTitulos;
    }
    
    public void setListaPersonaTitulos(List<PersonaTitulo> listaPersonaTitulos) {
        this.listaPersonaTitulos = listaPersonaTitulos;
    }
    
    public Titulo getTitulo() {
        return titulo;
    }
    
    public void setTitulo(Titulo titulo) {
        this.titulo = titulo;
    }
    
    public List<Titulo> getListaTitulosAgregados() {
        return listaTitulosAgregados;
    }
    
    public void setListaTitulosAgregados(List<Titulo> listaTitulosAgregados) {
        this.listaTitulosAgregados = listaTitulosAgregados;
    }
    
    public boolean isMostrarDlgUniversidad() {
        return mostrarDlgUniversidad;
    }
    
    public void setMostrarDlgUniversidad(boolean mostrarDlgUniversidad) {
        this.mostrarDlgUniversidad = mostrarDlgUniversidad;
    }
    
    public boolean isMostrarDlgTitulo() {
        return mostrarDlgTitulo;
    }
    
    public void setMostrarDlgTitulo(boolean mostrarDlgTitulo) {
        this.mostrarDlgTitulo = mostrarDlgTitulo;
    }
    
    public Date getFiltroFechaInicio() {
        return filtroFechaInicio;
    }

    public void setFiltroFechaInicio(Date filtroFechaInicio) {
        this.filtroFechaInicio = filtroFechaInicio;
    }

    public Date getFiltroFechaFin() {
        return filtroFechaFin;
    }

    public void setFiltroFechaFin(Date filtroFechaFin) {
        this.filtroFechaFin = filtroFechaFin;
    }

    public boolean isModoModificar() {
        return modo == Modo.EDITAR;
    }    

    public List<PersonaFirma> getListaPersonaFirmas() {
        return listaPersonaFirmas;
    }

    public void setListaPersonaFirmas(List<PersonaFirma> listaPersonaFirmas) {
        this.listaPersonaFirmas = listaPersonaFirmas;
    }

    public List<Persona> getListaPersonasConExternos() {
        return listaPersonasConExternos;
    }

    public void setListaPersonasConExternos(List<Persona> listaPersonasConExternos) {
        this.listaPersonasConExternos = listaPersonasConExternos;
    }

    public List<Persona> getListaPersonasSoloExternos() {
        return listaPersonasSoloExternos;
    }

    public void setListaPersonasSoloExternos(List<Persona> listaPersonasSoloExternos) {
        this.listaPersonasSoloExternos = listaPersonasSoloExternos;
    }

    public int getTabActivo() {
        return tabActivo;
    }

    public void setTabActivo(int tabActivo) {
        this.tabActivo = tabActivo;
    }
    
}
