/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.PersonaJpaController;
import ec.edu.chyc.manejopersonal.entity.Pasante;
import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.entity.PersonaTitulo;
import ec.edu.chyc.manejopersonal.entity.Tesista;
import ec.edu.chyc.manejopersonal.entity.Titulo;
import ec.edu.chyc.manejopersonal.entity.Universidad;
import ec.edu.chyc.manejopersonal.util.DateUtils;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;

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
    
    private boolean mostrarDlgUniversidad;
    private boolean mostrarDlgTitulo;
    
    private Date filtroFechaInicio;
    private Date filtroFechaFin;
    
    private List<PersonaTitulo> listaPersonaTitulos = null;
    
    private List<Persona> listaPersonas = null;
    private List<Persona> listaPersonasConContrato = new ArrayList<>();
    
    private Universidad universidad = null;
    private Titulo titulo = null;
    
    private List<Universidad> listaUniversidadesAgregadas = new ArrayList<>();
    private List<Titulo> listaTitulosAgregados = new ArrayList<>();
    
    private Tesista tesista;
    private Pasante pasante;
    
    private long idTituloGenerado = -1;
    private long idUniversidadGenerada = -1;
    private long idTituloPersonaGenerado = -1;
    private PersonaTitulo tituloDePersona;
    
    private boolean esTesista = false;
    private boolean esContratado = false;
    private boolean esPasante = false;
    
    private boolean modoModificar = false;

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
        titulo = new Titulo();
    }    

    public String abrir() {
        
        ejecutarJS("PF('dlgUniversidad').show()");
        return "";
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
        listaUniversidadesAgregadas.add(universidad);
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
    
    public void ejecutarJS(String codigo) {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute(codigo);
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
    public String initModificarPersona(Long id) {
        persona = personaController.findPersona(id);
        modoModificar = true;
        
        listaPersonaTitulos = new ArrayList<>(persona.getPersonaTitulosCollection());
        pasante = new Pasante();
        tesista = new Tesista();
        esTesista = false;
        esPasante = false;
        
        try {
            if (persona instanceof Tesista) {
                BeanUtils.copyProperties(tesista, (Tesista) persona);
                esTesista = true;
            } else if (persona instanceof Pasante) {
                BeanUtils.copyProperties(pasante, (Pasante) persona);
                esPasante = true;
            }
        } catch (IllegalAccessException | InvocationTargetException ex) {
            Logger.getLogger(GestorPersona.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "manejoPersona";
    }

    public String initCrearPersona() {
        persona = new Persona();
        fechaActual = new Date();
        
        modoModificar = false;
        
        mostrarDlgTitulo = false;
        mostrarDlgUniversidad = false;
        
        idTituloGenerado = -1;
        idUniversidadGenerada = -1;
        idTituloPersonaGenerado = -1;
        
        pasante = new Pasante();
        tesista = new Tesista();
        esTesista = false;
        esPasante = false;
        esContratado = false;
        
        LocalDate dtFechaNacDefault = LocalDateTime.now().toLocalDate();
        dtFechaNacDefault = dtFechaNacDefault.minusYears(25);
        
        persona.setFechaVinculacion(fechaActual);
        persona.setFechaNacimiento(DateUtils.asDate(dtFechaNacDefault));
        
        listaPersonaTitulos = new ArrayList<>();
        
        GestorGeneral.getInstance().actualizarListaUniversidades();
        GestorGeneral.getInstance().actualizarListaTitulos();
        listaUniversidadesAgregadas.clear();
        listaTitulosAgregados.clear();
        
        return "manejoPersona";
    }
    
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
        
        personaTituloNuevo.setId(idTituloPersonaGenerado);
        listaPersonaTitulos.add(personaTituloNuevo);

        //listaTitulos.add(nuevoTitulo);
        universidad = new Universidad();
        titulo = new Titulo();
        
        idTituloPersonaGenerado--;
        
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
    
    public void actualizarListaPersonasConContrato() {
        try {
            listaPersonasConContrato = personaController.listPersonasConContrato();
        } catch (Exception ex) {
            Logger.getLogger(GestorPersona.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            if (StringUtils.containsIgnoreCase(uni.getNombre(), query)) {
                listResults.add(uni);
            }
        }
        for (Universidad uni : listaUniversidadesAgregadas) {
            if (StringUtils.containsIgnoreCase(uni.getNombre(), query)) {
                listResults.add(uni);
            }
        }
        return listResults;
    }

    public List<Titulo> completarTitulo(String query) {
        List<Titulo> list = GestorGeneral.getInstance().getListaTitulos();
        List<Titulo> listResults = new ArrayList<>();
        
        for (Titulo titu : list) {
            if (StringUtils.containsIgnoreCase(titu.getNombre(), query)) {
                listResults.add(titu);
            }
        }
        for (Titulo titu : listaTitulosAgregados) {
            if (StringUtils.containsIgnoreCase(titu.getNombre(), query)) {
                listResults.add(titu);
            }
        }
        return listResults;
    }
    
    public void onCheckEsTesista() {
        if (esTesista) {
            esPasante = false;
        }
    }

    public void onCheckEsPasante() {
        if (esPasante) {
            esTesista = false;
        }
    }
    
    public String guardar() {
        try {            
            Persona personaGuardar = persona;
            
            if (esTesista) {
                //tesista = null;
                BeanUtils.copyProperties(tesista, persona);
                personaGuardar = tesista;
            } else if (esPasante) {
                //pasante = null;
                BeanUtils.copyProperties(pasante, persona);
                personaGuardar = pasante;
            }
            
            personaGuardar.setPersonaTitulosCollection(listaPersonaTitulos);
            
            if (modoModificar) {
                personaController.edit(personaGuardar);
            } else {
                personaController.create(personaGuardar);
            }
            
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
    
    public List<Universidad> getListaUniversidadesAgregadas() {
        return listaUniversidadesAgregadas;
    }
    
    public void setListaUniversidadesAgregadas(List<Universidad> listaUniversidadesAgregadas) {
        this.listaUniversidadesAgregadas = listaUniversidadesAgregadas;
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
    
    public boolean isEsTesista() {
        return esTesista;
    }
    
    public void setEsTesista(boolean esTesista) {
        this.esTesista = esTesista;
    }
    
    public boolean isEsContratado() {
        return esContratado;
    }
    
    public void setEsContratado(boolean esContratado) {
        this.esContratado = esContratado;
    }
    
    public boolean isEsPasante() {
        return esPasante;
    }
    
    public void setEsPasante(boolean esPasante) {
        this.esPasante = esPasante;
    }   
   
    public Tesista getTesista() {
        return tesista;
    }
    
    public void setTesista(Tesista tesista) {
        this.tesista = tesista;
    }
    
    public Pasante getPasante() {
        return pasante;
    }
    
    public void setPasante(Pasante pasante) {
        this.pasante = pasante;
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
    
    public List<Persona> getListaPersonasConContrato() {
        return listaPersonasConContrato;
    }
    
    public void setListaPersonasConContrato(List<Persona> listaPersonasConContrato) {
        this.listaPersonasConContrato = listaPersonasConContrato;
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
    
}
