/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.AcuerdoConfidencialJpaController;
import ec.edu.chyc.manejopersonal.entity.AcuerdoConfidencial;
import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.managebean.util.BeansUtils;
import ec.edu.chyc.manejopersonal.util.ServerUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author CHYC-DK-005
 */
@Named(value = "gestorAcuerdoConfidencialidad")
@SessionScoped
public class GestorAcuerdoConfidencialidad implements Serializable {

    private final AcuerdoConfidencialJpaController acuerdoConfController = new AcuerdoConfidencialJpaController();
    private AcuerdoConfidencial acuerdoConfidencial;
    private List<AcuerdoConfidencial> listaAcuerdos;
    boolean modoModificar;
    private Persona beneficiario;
    private String tamanoArchivo;

    public GestorAcuerdoConfidencialidad() {
        actualizarListaAcuerdos(); //TODO: Quitar cuando se acceda desde el menu
        initCrearAcuerdoConfidencialidad();
        modoModificar = false;
    }

    @PostConstruct
    public void init() {
    }

    public String corregirUrl(String url) {
        if (!url.contains("://")) {
            url = "http://" + url;
        }
        return url;
    }

    public StreamedContent streamParaDescarga(AcuerdoConfidencial acuerdoConfDescarga) {
        String nombreArchivo = acuerdoConfDescarga.getArchivoAcuerdoConf();
        try {
            return BeansUtils.streamParaDescarga(ServerUtils.getPathAcuerdosConfidenciales().resolve(nombreArchivo), "acuerdo_confidencialidad_" + acuerdoConfDescarga.getId());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GestorAcuerdoConfidencialidad.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String initListarAcuerdos() {
        actualizarListaAcuerdos();
        return "listaAcuerdosConfidencialidad";
    }
    
    public void actualizarListaAcuerdos() {
        try {
            listaAcuerdos = acuerdoConfController.listAcuerdosConf();
        } catch (Exception ex) {
            Logger.getLogger(GestorProyecto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<AcuerdoConfidencial> getListaAcuerdos() {
        return listaAcuerdos;
    }

    public boolean isModoModificar() {
        return modoModificar;
    }

    public void setModoModificar(boolean modoModificar) {
        this.modoModificar = modoModificar;
    }

    public AcuerdoConfidencial getAcuerdoConfidencial() {
        return acuerdoConfidencial;
    }

    public void setAcuerdoConfidencial(AcuerdoConfidencial acuerdoConfidencial) {
        this.acuerdoConfidencial = acuerdoConfidencial;
    }

    public String initCrearAcuerdoConfidencialidad() {
        inicializarManejoAcuerdoConf();
        modoModificar = false;
        return "manejoAcuerdoConfidencialidad";
    }

    private void inicializarManejoAcuerdoConf() {
        acuerdoConfidencial = new AcuerdoConfidencial();        
        tamanoArchivo = "";     
        modoModificar = false;        
    }
    
    public String initModificarAcuerdoConf(Long id) {
        inicializarManejoAcuerdoConf();
        acuerdoConfidencial = acuerdoConfController.findAcuerdoConfidencial(id);       
        Path pathArchivoAcuerdoConf = ServerUtils.getPathAcuerdosConfidenciales().resolve(acuerdoConfidencial.getArchivoAcuerdoConf());
        tamanoArchivo = ServerUtils.tamanoArchivo(pathArchivoAcuerdoConf);               
        modoModificar = true;
        return "manejoAcuerdoConfidencialidad";
    }
    
    public void seleccionarBeneficiario() {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", true);
        options.put("draggable", true);
        options.put("width", "75%");
        options.put("modal", true);
        options.put("contentWidth", "100%");
        GestorDialogListaPersonasSimple.getInstance().prepararApertura();
        RequestContext.getCurrentInstance().openDialog("dialogListaPersonasSimple", options, null);
    }

    public void onPersonaChosen(SelectEvent event) {
        List<Persona> listaPersonasSel = (List<Persona>) event.getObject();
        if (listaPersonasSel != null) {
            for (Persona per : listaPersonasSel) {                
                seleccionarBeneficiarioFinal(per);
            }
        }
    }

    private void seleccionarBeneficiarioFinal(Persona per) {
        beneficiario = per;
    }

    public static GestorAcuerdoConfidencialidad getInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorAcuerdoConfidencialidad}", GestorAcuerdoConfidencialidad.class);
        return (GestorAcuerdoConfidencialidad) ex.getValue(context);
    }

    public void fileUploadListener(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        //nombre del archivo que ya se encuentra almacenado en las propiedades
        String nombreArchivoGuardado;
        nombreArchivoGuardado = acuerdoConfidencial.getArchivoAcuerdoConf();

        if (nombreArchivoGuardado.isEmpty() && !modoModificar) {
            //si la propiedad esta llena, significa que antes ya se subió un archivo y ahora está subiendo uno nuevo para reemplazarlo
            // por lo tanto hay que eliminar el archivo anterior
            Path pathArchivoAnterior = ServerUtils.getPathTemp().resolve(nombreArchivoGuardado).normalize();
            File archivoEliminar = pathArchivoAnterior.toFile();
            //borrar el archivo anterior en caso de existir
            if (archivoEliminar.isFile()) {
                archivoEliminar.delete();
            }
        }
        if (file != null) {
            String extensionSubida = FilenameUtils.getExtension(file.getFileName());
            String nombreArchivoSubido = ServerUtils.generarNombreValidoArchivo(extensionSubida);
            Path pathArchivo = ServerUtils.getPathTemp().resolve(nombreArchivoSubido).normalize();

            File newFile = pathArchivo.toFile();

            try {
                BeansUtils.subirArchivoPrimefaces(file, newFile);

                
                    acuerdoConfidencial.setArchivoAcuerdoConf(nombreArchivoSubido);
                    tamanoArchivo = ServerUtils.humanReadableByteCount(file.getSize());
                
            } catch (IOException ex) {
                GestorMensajes.getInstance().mostrarMensajeError(ex.getLocalizedMessage());
                Logger.getLogger(GestorAcuerdoConfidencialidad.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.err.println("Error al subir archivo");
        }
    }
    
    public String guardar() {
                
        try {            
                        
            if (modoModificar) {
                acuerdoConfController.edit(acuerdoConfidencial);
            } else {
                acuerdoConfController.create(acuerdoConfidencial);
            }
            return initListarAcuerdos();
        } catch (Exception ex) {
            Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    public String initVerAcuerdoConfidencialidad(Long id) {
        inicializarManejoAcuerdoConf();
        acuerdoConfidencial = acuerdoConfController.findAcuerdoConfidencial(id);
        modoModificar = false;
        return "verAcuerdoConfidencialidad";
    }

    public void setTamanoArchivo(String tamanoArchivo) {
        this.tamanoArchivo = tamanoArchivo;
    }

    public String getTamanoArchivo() {
        return tamanoArchivo;
    }
    
    
    

}
