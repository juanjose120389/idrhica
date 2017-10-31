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

import ec.edu.chyc.manejopersonal.controller.ContratoJpaController;
import ec.edu.chyc.manejopersonal.entity.Contrato;
import ec.edu.chyc.manejopersonal.entity.Proyecto;
import ec.edu.chyc.manejopersonal.managebean.util.BeansUtils;
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
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Juan José
 */
@Named(value = "gestorContrato")
@SessionScoped
public class GestorContrato implements Serializable {

    private final ContratoJpaController contratoController = new ContratoJpaController();

    private List<Contrato> listaContrato = new ArrayList<>();
    private List<Proyecto> listaProyectos = new ArrayList<>();
    private Contrato contrato = new Contrato();
    private boolean modoModificar = false;

    private boolean esProfesor = false;
    private String tamanoArchivo;

    public GestorContrato() {
    }

    @PostConstruct
    public void init() {
    }

    public static GestorContrato getInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorContrato}", GestorContrato.class);
        return (GestorContrato) ex.getValue(context);
    }

    public void actualizarListaContrato() {
        try {
            listaContrato = contratoController.listContrato();
        } catch (Exception ex) {
            Logger.getLogger(GestorContrato.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public StreamedContent streamParaDescarga(Contrato contratoDescarga) {
        String nombreArchivo = contratoDescarga.getArchivoContrato();
        try {
            return BeansUtils.streamParaDescarga(ServerUtils.getPathContratos().resolve(nombreArchivo), "contrato_" + contratoDescarga.getPersona().getIdentificacion());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    } 
    public void quitarProyecto(Proyecto proyectoQuitar) {
        listaProyectos.remove(proyectoQuitar);
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
    public void onProyectoChosen(SelectEvent event) {
        List<Proyecto> listaProyectosSel = (List<Proyecto>) event.getObject();
        if (listaProyectosSel != null) {
            for (Proyecto proy : listaProyectosSel) {
                if (!listaProyectos.contains(proy)) {
                    listaProyectos.add(proy);
                }
            }
            RequestContext.getCurrentInstance().update("formContenido:dtProyectos");
        }
    }
    
    private void inicializarManejoContrato() {
        contrato = new Contrato();
        esProfesor = false;
        tamanoArchivo = "";
        listaProyectos.clear();
        GestorProyecto.getInstance().actualizarListaProyecto();
        GestorPersona.getInstance().actualizarListado();
    }
    
    public String initModificarContrato(Long id) {
        //contrato = contratoController.
        inicializarManejoContrato();
        
        contrato = contratoController.findContrato(id);
        listaProyectos = new ArrayList(contrato.getProyectosCollection());
        
        if (contrato.getTipoProfesor() != null) {
            esProfesor = true;
        }
        
        /*if (!esProfesor) {
            //si no es profesor, asignar el unico proyecto relacionado al contrato, a la variable contrato.proyecto para poder obtener de una sola
            Proyecto unicoProyecto = contrato.getProyectosCollection().stream().findFirst().get();
            contrato.setProyecto(unicoProyecto);
        }*/
        modoModificar = true;
        try {
            Path pathArchivoSubido = ServerUtils.getPathContratos().resolve(contrato.getArchivoContrato());
            if (Files.isRegularFile(pathArchivoSubido) && Files.exists(pathArchivoSubido)) {
                Long size = Files.size(pathArchivoSubido);
                tamanoArchivo = ServerUtils.humanReadableByteCount(size);
            } else {
                tamanoArchivo = "";
            }
        } catch (IOException ex) {
            Logger.getLogger(GestorContrato.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "manejoContratos";
    }
    
    public String initCrearContrato() {
        inicializarManejoContrato();
        modoModificar = false;
        //actualizarListaContrato();
        return "manejoContratos";
    }

    public String initListarContratos() {
        actualizarListaContrato();
        return "listaContratos";
    }

    public String guardar() {
        if (!esProfesor) {
            contrato.setTipoProfesor(null);            
            //agregar a la lista de proyectos el único proyecto seleccionado
            listaProyectos.clear();
            listaProyectos.add(contrato.getProyecto());
        }
        contrato.setProyectosCollection(new HashSet(listaProyectos));
        try {
            if (modoModificar) {
                contratoController.edit(contrato);
            } else {
                contratoController.create(contrato);
            }
            return initListarContratos();
        } catch (Exception ex) {
            Logger.getLogger(GestorContrato.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public void fileUploadListener(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        if (!contrato.getArchivoContrato().isEmpty() && !modoModificar) {
            //si la propiedad getArchivoArticulo() esta llena, significa que antes ya subió un archivo y ahora está subiendo uno nuevo para reemplazarlo
            // por lo tanto hay que eliminar el archivo anterior
            Path pathArchivoAnterior = ServerUtils.getPathTemp().resolve(contrato.getArchivoContrato()).normalize();
            File archivoEliminar = pathArchivoAnterior.toFile();
            //borrar el archivo anterior en caso de existir
            if (archivoEliminar.isFile()) {
                archivoEliminar.delete();
            }
        }
        if (file != null) {
            String extension = FilenameUtils.getExtension(file.getFileName());
            String nombreArchivo = ServerUtils.generateB64Uuid().replace("=", "") + (new Random()).nextInt(9999) + "." + extension;
            nombreArchivo = ServerUtils.convertirNombreArchivo(nombreArchivo);
            Path pathArchivo = ServerUtils.getPathTemp().resolve(nombreArchivo).normalize();
            File newFile = pathArchivo.toFile();
            try {
                BeansUtils.subirArchivoPrimefaces(file, newFile);
                contrato.setArchivoContrato(nombreArchivo);
                tamanoArchivo = ServerUtils.humanReadableByteCount(file.getSize());
            } catch (IOException ex) {
                Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.err.println("Error al subir archivo");
        }
    }
    
    public boolean puedeSerContratoProfesor() {
        return contrato.getTipo() == Contrato.TipoContrato.SERCOP;
    }

    public boolean isEsProfesor() {
        return esProfesor;
    }

    public void setEsProfesor(boolean esProfesor) {
        this.esProfesor = esProfesor;
    }

    public Contrato.TipoContrato[] getTiposContrato() {
        return Contrato.TipoContrato.values();
    }

    public Contrato.TipoProfesor[] getTiposProfesor() {
        return Contrato.TipoProfesor.values();
    }
    
    public List<Contrato> getListaContrato() {
        return listaContrato;
    }

    public void setListaContrato(List<Contrato> listaContrato) {
        this.listaContrato = listaContrato;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
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

    public List<Proyecto> getListaProyectos() {
        return listaProyectos;
    }

    public void setListaProyectos(List<Proyecto> listaProyectos) {
        this.listaProyectos = listaProyectos;
    }    
}
