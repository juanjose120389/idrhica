/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.ArticuloJpaController;
import ec.edu.chyc.manejopersonal.entity.Articulo;
import ec.edu.chyc.manejopersonal.entity.Articulo.TipoArticulo;
import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.util.ServerUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author marcelocaj
 */
@Named(value = "gestorArticulo")
@SessionScoped
public class GestorArticulo implements Serializable {

    private final ArticuloJpaController articuloController = new ArticuloJpaController();
    
    private List<Articulo> listaArticulos = new ArrayList<>();
    private List<Persona> listaAutores = new ArrayList<>();
    private Articulo articulo;
    private String tamanoArchivo;
    
    public GestorArticulo() {
    }
    
    @PostConstruct
    public void init() {
        
    }
    
    public void quitarAutor(Persona personaQuitar) {
        listaAutores.remove(personaQuitar);
    }
    
    public void onPersonaChosen(SelectEvent event) {
        List <Persona> listaPersonasSel = (List<Persona>) event.getObject();
        //FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Car Selected", "Id:" + car.getId());
        if (listaPersonasSel != null) {
            for (Persona per : listaPersonasSel) {
                if (listaAutores.indexOf(per) < 0) {
                    listaAutores.add(per);
                }
            }
            
            //listaAutores.addAll(listaPersonasSel);
            RequestContext.getCurrentInstance().update("formContenido:dtAutores");
        }
    }
    
    public void agregarAutor() {
        Persona personaNueva = new Persona();
        
        Map<String,Object> options = new HashMap<>();
        options.put("resizable", true);
        options.put("draggable", true);
        options.put("width", "75%");
        options.put("modal", true);
        options.put("contentWidth", "100%");
        GestorDialogListaPersonas.getInstance().clearListaPersonasSel();
        RequestContext.getCurrentInstance().openDialog("dialogListaPersonas", options, null);
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
    
    public static GestorArticulo getInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorArticulo}",GestorArticulo.class);
        return (GestorArticulo)ex.getValue(context);
    }
    public void fileUploadListener(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        if (articulo.getArchivoArticulo() != null && !articulo.getArchivoArticulo().isEmpty()) {
            //si la propiedad getArchivoArticulo() esta llena, significa que antes ya subió un archivo y ahora está subiendo uno nuevo para reemplazarlo
            // por lo tanto hay que eliminar el archivo anterior
            Path pathArchivoAnterior = ServerUtils.getPathTemp().resolve(articulo.getArchivoArticulo()).normalize();
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
                newFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
            }                     
            
            FileOutputStream fileOutputStream;
            InputStream inputStream;
            try {
                fileOutputStream = new FileOutputStream(newFile);

                byte[] buffer = new byte[ServerUtils.BUFFER_SIZE];

                int bulk;
                inputStream = file.getInputstream();

                while (true) {
                    bulk = inputStream.read(buffer);
                    if (bulk < 0) {
                        break;
                    }
                    fileOutputStream.write(buffer, 0, bulk);
                    fileOutputStream.flush();
                }

                fileOutputStream.close();
                inputStream.close();

                articulo.setArchivoArticulo(nombreArchivo);

                tamanoArchivo = ServerUtils.humanReadableByteCount(file.getSize());

                //listaImagenes.add(imagen);
                FacesMessage msg
                        = new FacesMessage("File Description", "file name: "
                                + event.getFile().getFileName() + "file size: "
                                + event.getFile().getSize() / 1024 + " Kb content type: "
                                + event.getFile().getContentType() + ".");
                FacesContext.getCurrentInstance().addMessage(null, msg);

            } catch (IOException e) {
                e.printStackTrace();

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        e.getLocalizedMessage(), ""));
            }

        } else {
            System.err.println("Error al subir archivo");
        }

    }
    
    public String guardar() {
        articulo.setAutoresCollection(new HashSet(listaAutores));
        //articuloController.create(articulo);
        
        try {
            articuloController.create(articulo);
            return "index";
        } catch (Exception ex) {
            Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }        
    }
    public TipoArticulo[] getTiposArticulo() {
        return TipoArticulo.values();
    }
    public String initCrearArticulo() {
        articulo = new Articulo();
        listaAutores.clear();
        tamanoArchivo = "";
        GestorPersona.getInstance().actualizarListaPersonasConContrato();
        
        return "manejoArticulo";
    }
    
    public void actualizarListaArticulos() {
        try {
            listaArticulos = articuloController.listArticulos();
        } catch (Exception ex) {
            Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public String initListarArticulos() {
        actualizarListaArticulos();        
        listaAutores.clear();
        return "listaArticulos";
    }

    public List<Articulo> getListaArticulos() {
        return listaArticulos;
    }

    public void setListaArticulos(List<Articulo> listaArticulos) {
        this.listaArticulos = listaArticulos;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    public List<Persona> getListaAutores() {
        return listaAutores;
    }

    public void setListaAutores(List<Persona> listaAutores) {
        this.listaAutores = listaAutores;
    }

    public String getTamanoArchivo() {
        return tamanoArchivo;
    }

    public void setTamanoArchivo(String tamanoArchivo) {
        this.tamanoArchivo = tamanoArchivo;
    }

}
