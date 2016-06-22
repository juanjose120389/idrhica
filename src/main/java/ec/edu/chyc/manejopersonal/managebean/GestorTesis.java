/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.TesisJpaController;
import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.entity.Proyecto;
import ec.edu.chyc.manejopersonal.entity.Tesis;
import ec.edu.chyc.manejopersonal.entity.Tesis.TipoTesis;
import ec.edu.chyc.manejopersonal.entity.Universidad;
import ec.edu.chyc.manejopersonal.managebean.util.BeansUtils;
import static ec.edu.chyc.manejopersonal.managebean.util.BeansUtils.ejecutarJS;
import ec.edu.chyc.manejopersonal.util.ServerUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Juan José
 */
@Named(value = "gestorTesis")
@SessionScoped
public class GestorTesis implements Serializable {

    private final TesisJpaController tesisController = new TesisJpaController();
    private Tesis tesis = new Tesis();
    private List<Tesis> listaTesis = new ArrayList<>();
    private List<Persona> listaAutoresTesis = new ArrayList<>();
    private List<Proyecto> listaProyectos = new ArrayList<>();
    private List<Persona> listaCodirectores = new ArrayList<>();
    private List<Persona> listaTutores = new ArrayList<>();
    private Universidad universidad = null;
    private long idUniversidadGen = -1L;

    private boolean modoModificar = false;
    private String tamanoArchivo = "";

    public GestorTesis() {
    }

    @PostConstruct
    public void init() {

    }

    public String initVerTesis(Long id) {
        inicializarManejoTesis();
        
        cargarDatosTesis(id);
        
        return "verTesis";
    }
    
    private void cargarDatosTesis(Long id) {
        tesis = tesisController.findTesis(id);
        listaProyectos = new ArrayList<>(tesis.getProyectosCollection());
        listaCodirectores = new ArrayList<>(tesis.getCodirectoresCollection());
        listaTutores = new ArrayList<>(tesis.getTutoresCollection());
        listaAutoresTesis = new ArrayList<>(tesis.getAutoresCollection());

        try {
            Path pathArchivoSubido = ServerUtils.getPathActasAprobacionTesis().resolve(tesis.getArchivoActaAprobacion());
            if (Files.isRegularFile(pathArchivoSubido) && Files.exists(pathArchivoSubido)) {
                Long size = Files.size(pathArchivoSubido);
                tamanoArchivo = ServerUtils.humanReadableByteCount(size);
            } else {
                tamanoArchivo = "";
            }
        } catch (IOException ex) {
            Logger.getLogger(GestorContrato.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public String initModificarTesis(Long id) {
        inicializarManejoTesis();

        cargarDatosTesis(id);
        
        modoModificar = true;


        return "manejoTesis";
    }

    private void inicializarManejoTesis() {
        tesis = new Tesis();
        GestorContrato.getInstance().actualizarListaContrato();
        GestorProyecto.getInstance().actualizarListaProyecto();
        GestorGeneral.getInstance().actualizarListaUniversidades();
        listaAutoresTesis.clear();
        listaProyectos.clear();
        listaCodirectores.clear();
        listaTutores.clear();
        
        idUniversidadGen= -1L;
        
        GestorDialogListaPersonas.getInstance().resetearDialog();
        GestorGeneral.getInstance().getListaUniversidadesAgregadas().clear();

        tamanoArchivo = "";
    }

    public String initCrearTesis() {
        inicializarManejoTesis();

        modoModificar = false;

        return "manejoTesis";
    }

    public boolean filtrarPorAutor(Object value, Object filter, Locale locale) {
       String filterText = (filter == null) ? null : filter.toString().trim();
        if(filterText == null||filterText.equals("")) {
            return true;
        }
         
        if(value == null) {
            return false;
        }
       
        Set<Persona> autores = (Set<Persona>)value;
         
        for (Persona per : autores) {
            if (StringUtils.containsIgnoreCase(per.getNombres(), filterText) || StringUtils.containsIgnoreCase(per.getApellidos(), filterText)) {
                return true;
            }
        }
          return false;

    }
    
        public boolean filtrarCoDirector(Object value, Object filter, Locale locale) {
       String filterText = (filter == null) ? null : filter.toString().trim();
        if(filterText == null||filterText.equals("")) {
            return true;
        }
         
        if(value == null) {
            return false;
        }
       
        Set<Persona> autores = (Set<Persona>)value;
         
        for (Persona per : autores) {
            if (StringUtils.containsIgnoreCase(per.getNombres(), filterText) || StringUtils.containsIgnoreCase(per.getApellidos(), filterText)) {
                return true;
            }
        }
          return false;

    }
        
         public boolean filtrarTutor(Object value, Object filter, Locale locale) {
       String filterText = (filter == null) ? null : filter.toString().trim();
        if(filterText == null||filterText.equals("")) {
            return true;
        }
         
        if(value == null) {
            return false;
        }
       
        Set<Persona> tutores = (Set<Persona>)value;
         
        for (Persona per : tutores) {
            if (StringUtils.containsIgnoreCase(per.getNombres(), filterText) || StringUtils.containsIgnoreCase(per.getApellidos(), filterText)) {
                return true;
            }
        }
          return false;

    }
    
    
        public boolean filtrarProyecto(Object value, Object filter, Locale locale) {
       String filterText = (filter == null) ? null : filter.toString().trim();
        if(filterText == null||filterText.equals("")) {
            return true;
        }
         
        if(value == null) {
            return false;
        }
       
        Set<Proyecto> proyectos = (Set<Proyecto>)value;
         
        for (Proyecto pro : proyectos) {
            if (StringUtils.containsIgnoreCase(pro.getTitulo(), filterText)) {
                return true;
            }
        }
          return false;

    }
    
    

    public static GestorTesis getInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorTesis}", GestorTesis.class);
        return (GestorTesis) ex.getValue(context);
    }

    public void actualizarListaTesis() {
        try {
            listaTesis = tesisController.listTesis();
        } catch (Exception ex) {
            Logger.getLogger(GestorTesis.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void quitarCodirector(Persona personaQuitar) {
        listaCodirectores.remove(personaQuitar);
    }

    public void quitarTutor(Persona personaQuitar) {
        listaTutores.remove(personaQuitar);
    }

    public void quitarAutor(Persona personaQuitar) {
        listaAutoresTesis.remove(personaQuitar);
    }

    public void onCodirectoresChosen(SelectEvent event) {
        List<Persona> listaPersonasSel = (List<Persona>) event.getObject();
        agregarPersonaALista(listaPersonasSel, listaCodirectores);
    }

    public void onTutoresChosen(SelectEvent event) {
        List<Persona> listaPersonasSel = (List<Persona>) event.getObject();
        agregarPersonaALista(listaPersonasSel, listaTutores);
    }

    public void onPersonaChosen(SelectEvent event) {
        List<Persona> listaPersonasSel = (List<Persona>) event.getObject();
        agregarPersonaALista(listaPersonasSel, listaAutoresTesis);
    }

    /**
     * *
     * Agrega un listado de personas a una lista solo si no están ya en la lista
     *
     * @param listaPersonasAgregar Listado origen, personas que serán añadidas
     * en la otra lista
     * @param listadoDePersonas Listado destino, donde se añadirá la persona
     */
    private void agregarPersonaALista(List<Persona> listaPersonasAgregar, List<Persona> listadoDePersonas) {
        if (listadoDePersonas != null && listaPersonasAgregar != null) {
            for (Persona per : listaPersonasAgregar) {
                if (!listadoDePersonas.contains(per)) {
                    listadoDePersonas.add(per);
                }
            }
        }
    }

    public void onProyectoChosen(SelectEvent event) {
        List<Proyecto> listaProyectosSel = (List<Proyecto>) event.getObject();
        if (listaProyectosSel != null) {
            for (Proyecto proy : listaProyectosSel) {
                if (!listaProyectos.contains(proy)) {
                    listaProyectos.add(proy);
                }
            }
        }
    }

    public void abrirDialogPersonas() {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", true);
        options.put("draggable", true);
        options.put("width", "75%");
        options.put("modal", true);
        options.put("contentWidth", "100%");
        GestorDialogListaPersonas.getInstance().prepararApertura();
        RequestContext.getCurrentInstance().openDialog("dialogListaPersonas", options, null);
    }
    
    public void abrirUniversidadDialog() {
        universidad = new Universidad();
        RequestContext.getCurrentInstance().update("formContenido:divDialogs");
        //ejecutarJS("PF('dlgUniversidad').show()");        
    }
    public String guardarUniversidad() {
        universidad.setId(idUniversidadGen);
        tesis.setUniversidad(universidad);
        GestorGeneral.getInstance().getListaUniversidadesAgregadas().add(universidad);
        //listaUniversidadesAgregadas.add(universidad);
        
        universidad = new Universidad();
        
        idUniversidadGen--;
        ejecutarJS("PF('dlgUniversidad').hide()");
        return "";
    }

    public String convertirListaPersonas(List<Persona> listaConvertir) {
        String r = "";
        for (Persona per : listaConvertir) {
            r += String.format("%s %s, ", per.getApellidos(), per.getNombres());
        }
        if (!r.isEmpty()) {
            r = r.substring(0, r.length() - 2);
        }

        return r;
    }

    public void agregarProyecto() {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", true);
        options.put("draggable", true);
        options.put("width", "75%");
        options.put("modal", true);
        options.put("contentWidth", "100%");
        GestorDialogListaProyectos.getInstance().clearListaProyectosSel();
        RequestContext.getCurrentInstance().openDialog("dialogListaProyectos", options, null);
    }

    public void quitarProyecto(Proyecto proyectoQuitar) {
        listaProyectos.remove(proyectoQuitar);
    }

    public void fileUploadListener(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        if (!tesis.getArchivoActaAprobacion().isEmpty() && !modoModificar) {
            //si la propiedad getArchivo esta llena, significa que antes ya subió un archivo y ahora está subiendo uno nuevo para reemplazarlo
            // por lo tanto hay que eliminar el archivo anterior, no realizarlo cuando está en modo modificar ya que puede darse el caso
            // que no quiera guardar los cambios (y si ya se borró el archivo, no sería posible recuperarlo en caso de cancelar la edición)
            Path pathArchivoAnterior = ServerUtils.getPathTemp().resolve(tesis.getArchivoActaAprobacion()).normalize();
            File archivoEliminar = pathArchivoAnterior.toFile();
            //borrar el archivo anterior en caso de existir
            if (archivoEliminar.isFile()) {
                archivoEliminar.delete();
            }
        }
        if (file != null) {
            String extension = FilenameUtils.getExtension(file.getFileName());
            String nombreArchivo = ServerUtils.generarNombreValidoArchivo(extension);
            Path pathArchivo = ServerUtils.getPathTemp().resolve(nombreArchivo).normalize();

            File newFile = pathArchivo.toFile();

            try {
                BeansUtils.subirArchivoPrimefaces(file, newFile);

                tesis.setArchivoActaAprobacion(nombreArchivo);

                tamanoArchivo = ServerUtils.humanReadableByteCount(file.getSize());
            } catch (IOException ex) {
                Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            System.err.println("Error al subir archivo");
        }

    }

    public StreamedContent streamParaDescarga(Tesis tesisDescarga) {
        String nombreArchivo = tesisDescarga.getArchivoActaAprobacion();
        try {
            return BeansUtils.streamParaDescarga(ServerUtils.getPathActasAprobacionTesis().resolve(nombreArchivo), "actaTesisAprob_" + tesisDescarga.getNombre());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String initListarTesis() {
        actualizarListaTesis();

        // listaAutores.clear();
        return "listaTesis";
    }

    public TipoTesis[] getTiposTesis() {
        return TipoTesis.values();
    }

    public String guardar() {       
        tesis.setAutoresCollection(new HashSet(listaAutoresTesis));
        tesis.setProyectosCollection(new HashSet(listaProyectos));
        tesis.setCodirectoresCollection(new HashSet(listaCodirectores));
        tesis.setTutoresCollection(new HashSet(listaTutores));
        
        if (tesis.getAutoresCollection().isEmpty()) {
            GestorMensajes.getInstance().mostrarMensajeWarn("Por favor, agregar autores a la tesis.");
            return "";        
        }        

        try {
            if (modoModificar) {
                tesisController.edit(tesis);
            } else {
                tesisController.create(tesis);
            }
            return "index";
        } catch (Exception ex) {
            Logger.getLogger(GestorTesis.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public List<Proyecto> getListaProyectos() {
        return listaProyectos;
    }

    public void setListaProyectos(List<Proyecto> listaProyectos) {
        this.listaProyectos = listaProyectos;
    }

    public List<Tesis> getListaTesis() {
        return listaTesis;
    }

    public void setListaTesis(List<Tesis> listaTesis) {
        this.listaTesis = listaTesis;
    }

    public List<Persona> getListaCodirectores() {
        return listaCodirectores;
    }

    public void setListaCodirectores(List<Persona> listaCodirectores) {
        this.listaCodirectores = listaCodirectores;
    }

    public List<Persona> getListaTutores() {
        return listaTutores;
    }

    public void setListaTutores(List<Persona> listaTutores) {
        this.listaTutores = listaTutores;
    }

    public Tesis getTesis() {
        return tesis;
    }

    public void setTesis(Tesis tesis) {
        this.tesis = tesis;
    }

    public List<Persona> getListaAutoresTesis() {
        return listaAutoresTesis;
    }

    public void setListaAutoresTesis(List<Persona> listaAutoresTesis) {
        this.listaAutoresTesis = listaAutoresTesis;
    }

    public boolean isModoModificar() {
        return modoModificar;
    }

    public void setModoModificar(boolean modoModificar) {
        this.modoModificar = modoModificar;
    }

    public String getTamanoArchivo() {
        return tamanoArchivo;
    }

    public void setTamanoArchivo(String tamanoArchivo) {
        this.tamanoArchivo = tamanoArchivo;
    }

    public Universidad getUniversidad() {
        return universidad;
    }

    public void setUniversidad(Universidad universidad) {
        this.universidad = universidad;
    }

}
