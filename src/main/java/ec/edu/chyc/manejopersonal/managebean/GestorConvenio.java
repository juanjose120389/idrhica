/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.ConvenioJpaController;
import ec.edu.chyc.manejopersonal.entity.Convenio;
import ec.edu.chyc.manejopersonal.entity.Institucion;
import ec.edu.chyc.manejopersonal.managebean.util.BeansUtils;
import ec.edu.chyc.manejopersonal.util.FechaUtils;
import ec.edu.chyc.manejopersonal.util.ServerUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author marcelocaj
 */
@Named(value = "gestorConvenio")
@SessionScoped
public class GestorConvenio implements Serializable {
    private final ConvenioJpaController convenioController = new ConvenioJpaController();
    private Convenio convenio;
    private List<Convenio> listaConvenios = new ArrayList<>();
    private boolean modoModificar;
    private Institucion institucion;
    private long idInstitucionGen = -1L;
    private String tamanoArchivo = "";
    
    public GestorConvenio() {
    }
    @PostConstruct
    public void init() {
        
    }
    
    
    public void inicializarManejoConvenio() {
        GestorInstitucion.getInstance().getListaInstitucionesAgregadas().clear();
        listaConvenios.clear();
        convenio = new Convenio();
        modoModificar = false;
        tamanoArchivo = "";
        
        GestorProyecto.getInstance().actualizarListaProyecto();
        GestorInstitucion.getInstance().actualizarListaInstituciones();
        GestorInstitucion.getInstance().getListaInstitucionesAgregadas().clear();
    }
    
    public String initCrearConvenio() {
        inicializarManejoConvenio();
        modoModificar = false;
        
        return "manejoConvenio";
    }
    public void actualizarListaConvenios() {
        try {
            listaConvenios = convenioController.listConvenio();
        } catch (Exception ex) {
            Logger.getLogger(GestorConvenio.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public String initListarConvenios() {
        inicializarManejoConvenio();
        
        actualizarListaConvenios();
        modoModificar = false;
        return "listaConvenios";
    }
    public String initModificarConvenio(Long id) {
        inicializarManejoConvenio();
        convenio = convenioController.findEntity(id);
        modoModificar = true;
        
        try {
            Path pathArchivoSubido = ServerUtils.getPathConvenios().resolve(convenio.getArchivoConvenio());
            if (Files.isRegularFile(pathArchivoSubido) && Files.exists(pathArchivoSubido)) {
                Long size = Files.size(pathArchivoSubido);
                tamanoArchivo = ServerUtils.humanReadableByteCount(size);
            } else {
                tamanoArchivo = "";
            }
        } catch (IOException ex) {
            Logger.getLogger(GestorContrato.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "manejoConvenio";
    }
    public static GestorConvenio getInstance()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorConvenio}",GestorConvenio.class);
        return (GestorConvenio)ex.getValue(context);
    }    
    
    public String guardar() {
        if (convenio.getFechaInicio() != null && convenio.getFechaFin() != null) {
            LocalDate dtInicio = FechaUtils.asLocalDate(convenio.getFechaInicio());
            LocalDate dtFin = FechaUtils.asLocalDate(convenio.getFechaFin());
            //MutableDateTime dtInicio = new MutableDateTime(convenio.getFechaInicio());
            //MutableDateTime dtFin = new MutableDateTime(convenio.getFechaFin());

            if (dtFin.isBefore(dtInicio)) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha de finalización debe ser mayor a la de inicio.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return "";
            }
        }
        
        try {
            if (modoModificar) {
                convenioController.edit(convenio);
            } else {
                convenioController.create(convenio);
            }
            return "index";
        } catch (Exception ex) {
            Logger.getLogger(GestorConvenio.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "";
    }
    public void guardarInstitucion() {
        institucion.setId(idInstitucionGen);
        idInstitucionGen--;
        GestorInstitucion.getInstance().getListaInstitucionesAgregadas().add(institucion);
        convenio.setInstitucion(institucion);
        
        institucion = new Institucion();        
        BeansUtils.ejecutarJS("PF('dlgInstitucion').hide()");
    }
    public void abrirNuevaInstitucionDlg() {
        institucion = new Institucion();
        
        RequestContext.getCurrentInstance().update("formContenido:divDialogs");
        //BeansUtils.ejecutarJS("PF('dlgInstitucion').show()");
    }
    
    public void fileUploadListener(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        if (!convenio.getArchivoConvenio().isEmpty() && !modoModificar) {
            //si la propiedad getArchivo esta llena, significa que antes ya subió un archivo y ahora está subiendo uno nuevo para reemplazarlo
            // por lo tanto hay que eliminar el archivo anterior, no realizarlo cuando está en modo modificar ya que puede darse el caso
            // que no quiera guardar los cambios (y si ya se borró el archivo, no sería posible recuperarlo en caso de cancelar la edición)
            Path pathArchivoAnterior = ServerUtils.getPathTemp().resolve(convenio.getArchivoConvenio()).normalize();
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

                convenio.setArchivoConvenio(nombreArchivo);

                tamanoArchivo = ServerUtils.humanReadableByteCount(file.getSize());
            } catch (IOException ex) {
                Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            System.err.println("Error al subir archivo");
        }

    }

    
    public StreamedContent streamParaDescarga(Convenio convenioDescarga) {
        String nombreArchivo = convenioDescarga.getArchivoConvenio();
        try {
            return BeansUtils.streamParaDescarga(ServerUtils.getPathConvenios().resolve(nombreArchivo), "convenio_" + convenioDescarga.getTitulo());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    

    public Convenio getConvenio() {
        return convenio;
    }

    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
    }

    public List<Convenio> getListaConvenios() {
        return listaConvenios;
    }

    public void setListaConvenios(List<Convenio> listaConvenios) {
        this.listaConvenios = listaConvenios;
    }

    public boolean isModoModificar() {
        return modoModificar;
    }

    public void setModoModificar(boolean modoModificar) {
        this.modoModificar = modoModificar;
    }

    public Institucion getInstitucion() {
        return institucion;
    }

    public void setInstitucion(Institucion institucion) {
        this.institucion = institucion;
    }
    
}
