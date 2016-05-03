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
import ec.edu.chyc.manejopersonal.entity.PersonaArticulo;
import ec.edu.chyc.manejopersonal.entity.Proyecto;
import ec.edu.chyc.manejopersonal.util.ServerUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
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
    private List<PersonaArticulo> listaPersonaArticulo = new ArrayList<>();
    private List<Proyecto> listaProyectos = new ArrayList<>();
    private Articulo articulo;
    private String tamanoArchivo;
    private boolean modoModificar = false;
    private Long idPersonaArticuloGen = -1L;
    
    private StreamedContent streamParaDescarga;
    
    public GestorArticulo() {
    }
    
    @PostConstruct
    public void init() {
        
    }
    
    public void moverArriba(PersonaArticulo personaArticuloMover, Integer indexActual) {
        if (indexActual != 0) {
            Collections.swap(listaPersonaArticulo, indexActual, indexActual - 1);
        }
    }

    public void moverAbajo(PersonaArticulo personaArticuloMover, Integer indexActual) {
        if (indexActual != listaPersonaArticulo.size() - 1) {
            Collections.swap(listaPersonaArticulo, indexActual, indexActual + 1);
        }
    }
    
    public void quitarAutor(PersonaArticulo personaArticuloQuitar) {
        listaPersonaArticulo.remove(personaArticuloQuitar);
    }
    public void quitarProyecto(Proyecto proyectoQuitar) {
        listaProyectos.remove(proyectoQuitar);
    }
    
    public void onPersonaChosen(SelectEvent event) {
        List <Persona> listaPersonasSel = (List<Persona>) event.getObject();
        //FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Car Selected", "Id:" + car.getId());
        if (listaPersonasSel != null) {
            for (Persona per : listaPersonasSel) {
                boolean encontrado = false;
                for (PersonaArticulo perart : listaPersonaArticulo) {
                    if (perart.getPersona().getId().equals(per.getId())) {
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado) {
                    PersonaArticulo perArt = new PersonaArticulo();
                    perArt.setPersona(per);
                    perArt.setId(idPersonaArticuloGen);
                    listaPersonaArticulo.add(perArt);
                    idPersonaArticuloGen--;
                }                
            }
            
            //listaAutores.addAll(listaPersonasSel);
            RequestContext.getCurrentInstance().update("formContenido:dtAutores");
        }
    }
    
    public void onProyectoChosen(SelectEvent event) {
        List <Proyecto> listaProyectosSel = (List<Proyecto>) event.getObject();
        if (listaProyectosSel != null) {
            for (Proyecto proy : listaProyectosSel) {
                if (!listaProyectos.contains(proy)) {
                    listaProyectos.add(proy);
                }
            }
            RequestContext.getCurrentInstance().update("formContenido:dtAutores");
        }
    }
    
    
    public void agregarAutor() {
        Map<String,Object> options = new HashMap<>();
        options.put("resizable", true);
        options.put("draggable", true);
        options.put("width", "75%");
        options.put("modal", true);
        options.put("contentWidth", "100%");
        GestorDialogListaPersonas.getInstance().clearListaPersonasSel();
        RequestContext.getCurrentInstance().openDialog("dialogListaPersonas", options, null);
    }   
    public void agregarProyecto() {        
        Map<String,Object> options = new HashMap<>();
        options.put("resizable", true);
        options.put("draggable", true);
        options.put("width", "75%");
        options.put("modal", true);
        options.put("contentWidth", "100%");
        GestorDialogListaProyectos.getInstance().clearListaProyectosSel();
        RequestContext.getCurrentInstance().openDialog("dialogListaProyectos", options, null);
    }       
    public String corregirUrl(String url) {
        if (!url.contains("://")) {
            url = "http://" + url;
        }
        /*if (!url.toLowerCase().matches("^\\w+://.*")) {
            url = "http://" + url;
        }*/
        return url;
    }

    public StreamedContent streamParaDescarga(Articulo articuloDescarga) {
        InputStream stream = null;
        streamParaDescarga = null;
        try {
            String nombreArchivo = articuloDescarga.getArchivoArticulo();
            String extension = FilenameUtils.getExtension(nombreArchivo);
            
            
            Path pathArchivo = ServerUtils.getPathArticulos().resolve(nombreArchivo);
            stream = new BufferedInputStream(new FileInputStream(pathArchivo.toFile()));
            
            String nuevoNombre = "articulo_";
            String nombreArticulo = articuloDescarga.getNombre();
            if (articuloDescarga.getNombre().length() - extension.length() - 1 > 25) {
                nombreArticulo = nombreArticulo.substring(0, 25);
            } 
            nuevoNombre += ServerUtils.toFileSystemSafeName(nombreArticulo + "." + extension);
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            streamParaDescarga = new DefaultStreamedContent(stream, externalContext.getMimeType(nuevoNombre), nuevoNombre);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException ex2) {
                Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
            }            
        } 
        
        return streamParaDescarga;
    }    
    
    public String convertirListaPersonas(Collection<PersonaArticulo> listaConvertir) {
        String r = "";
        for (PersonaArticulo per : listaConvertir) {
            r += String.format("%s %s, ", per.getPersona().getApellidos(), per.getPersona().getNombres());
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
        //articulo.setAutoresCollection(new HashSet(listaAutores));
        articulo.setPersonasArticuloCollection(listaPersonaArticulo);
        articulo.setProyectosCollection(new HashSet(listaProyectos));
        //articuloController.create(articulo);
        
        try {            
            for (int c=0;c<listaPersonaArticulo.size();c++) {
                //guardar el orden de acuerdo al orden de la lista
                listaPersonaArticulo.get(c).setOrden(c+1);
            }
            
            if (modoModificar) {
                articuloController.edit(articulo);
            } else {
                articuloController.create(articulo);
            }
            return "index";
        } catch (Exception ex) {
            Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    public TipoArticulo[] getTiposArticulo() {
        return TipoArticulo.values();
    }
    
    private void ordenarListaPersonaArticulo(List<PersonaArticulo> listaPersonaArticulo) {
        Collections.sort(listaPersonaArticulo, (left, right) -> left.getOrden() - right.getOrden());
    }
    
    public String initModificarArticulo(Long id) {
        articulo = articuloController.findArticulo(id);
        //listaAutores = new ArrayList<>(articulo.getAutoresCollection());
        listaPersonaArticulo = new ArrayList<>(articulo.getPersonasArticuloCollection());
        ordenarListaPersonaArticulo(listaPersonaArticulo);
        GestorProyecto.getInstance().actualizarListaProyecto();
        idPersonaArticuloGen = -1L;
        if (articulo.getArchivoArticulo() == null) {
            articulo.setArchivoArticulo("");
        }        
        try {
            if (Files.exists(ServerUtils.getPathArticulos().resolve( articulo.getArchivoArticulo())) ) {
                Long size = Files.size(ServerUtils.getPathArticulos().resolve( articulo.getArchivoArticulo() ));
                tamanoArchivo = ServerUtils.humanReadableByteCount(size);
            } else {
                tamanoArchivo = "";
            }
            
        } catch (IOException ex) {
            Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        modoModificar = true;
        
        GestorPersona.getInstance().actualizarListaPersonasConContrato();
        GestorConvenio.getInstance().actualizarListaConvenios();
        
        return "manejoArticulo";
    }
    public String initCrearArticulo() {
        articulo = new Articulo();        
        listaPersonaArticulo.clear();
        listaProyectos.clear();
        tamanoArchivo = "";
        modoModificar = false;
        idPersonaArticuloGen = -1L;
        GestorPersona.getInstance().actualizarListaPersonasConContrato();
        GestorProyecto.getInstance().actualizarListaProyecto();
        
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
        return "listaArticulos";
    }

    public boolean filtrarPorAutores(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim();
        if(filterText == null||filterText.equals("")) {
            return true;
        }
         
        if(value == null) {
            return false;
        }
        
        Set<Persona> autores = (Set) value;
        for (Persona per : autores) {
            if (StringUtils.containsIgnoreCase(per.getNombres(), filterText) || StringUtils.containsIgnoreCase(per.getApellidos(), filterText)) {
                return true;
            }
        }
         
        return false;
    }    
    public boolean filtrarPorAutorPrincipal(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim();
        if(filterText == null||filterText.equals("")) {
            return true;
        }
         
        if(value == null) {
            return false;
        }
        
        Persona autorPrincipal = (Persona)value;

        return StringUtils.containsIgnoreCase(autorPrincipal.getNombres(), filterText) 
                || StringUtils.containsIgnoreCase(autorPrincipal.getApellidos(), filterText);
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

    public List<PersonaArticulo> getListaPersonaArticulo() {
        return listaPersonaArticulo;
    }

    public void setListaPersonaArticulo(List<PersonaArticulo> listaPersonaArticulo) {
        this.listaPersonaArticulo = listaPersonaArticulo;
    }

    public String getTamanoArchivo() {
        return tamanoArchivo;
    }

    public void setTamanoArchivo(String tamanoArchivo) {
        this.tamanoArchivo = tamanoArchivo;
    }

    public void setStreamParaDescarga(StreamedContent streamParaDescarga) {
        this.streamParaDescarga = streamParaDescarga;
    }

    public List<Proyecto> getListaProyectos() {
        return listaProyectos;
    }

    public void setListaProyectos(List<Proyecto> listaProyectos) {
        this.listaProyectos = listaProyectos;
    }
    
}
