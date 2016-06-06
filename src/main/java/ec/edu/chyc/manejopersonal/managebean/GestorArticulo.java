/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.ArticuloJpaController;
import ec.edu.chyc.manejopersonal.controller.PersonaJpaController;
import ec.edu.chyc.manejopersonal.entity.Articulo;
import ec.edu.chyc.manejopersonal.entity.Articulo.TipoArticulo;
import ec.edu.chyc.manejopersonal.entity.Firma;
import ec.edu.chyc.manejopersonal.entity.Institucion;
import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.entity.PersonaArticulo;
import ec.edu.chyc.manejopersonal.entity.PersonaFirma;
import ec.edu.chyc.manejopersonal.entity.Proyecto;
import ec.edu.chyc.manejopersonal.entity.Usuario;
import ec.edu.chyc.manejopersonal.managebean.util.BeansUtils;
import ec.edu.chyc.manejopersonal.util.FechaUtils;
import ec.edu.chyc.manejopersonal.util.ServerUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXParser;
import org.jbibtex.Key;
import org.jbibtex.ParseException;
import org.jbibtex.TokenMgrException;
import org.jbibtex.Value;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
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
    private final PersonaJpaController personaController = new PersonaJpaController();
    
    private List<Articulo> listaArticulos = new ArrayList<>();
    private List<PersonaArticulo> listaPersonaArticulo = new ArrayList<>();
    private List<Proyecto> listaProyectos = new ArrayList<>();
    private List<Institucion> listaAgradecimientos = new ArrayList<>();
    private Articulo articulo;
    private String tamanoArchivo;
    private String tamanoArchivoBibtex;
    private boolean modoModificar = false;
    private Long idPersonaArticuloGen = -1L;
    private Long idPersonaFirmaGen = -1L;
    private Long idFirmaGen = -1L;
    private boolean soloSubirBibtex = false;
    
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

    public void quitarAgradecimiento(Institucion institucionQuitar) {
        listaAgradecimientos.remove(institucionQuitar);
    }

    /**
     * Agrega la persona a la lista de autores solo si no existe
     * @param per Persona a agregar
     */
    private void agregarNuevoAutor(Persona per, String firmaSel) {
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
            per.getListaFirmas().clear();
            int c = 0;
            for (PersonaFirma perFirma : per.getPersonaFirmasCollection()) {
                if (firmaSel == null) {
                    if (c == 0) {
                        perArt.setFirma(perFirma.getFirma());
                    }
                } else if (StringUtils.equalsIgnoreCase(perFirma.getFirma().getNombre(), firmaSel)) {
                    perArt.setFirma(perFirma.getFirma());
                }
                per.getListaFirmas().add(perFirma.getFirma());
                c++;
            }

            perArt.setId(idPersonaArticuloGen);
            listaPersonaArticulo.add(perArt);
            idPersonaArticuloGen--;
        }
    }
    
    private void agregarNuevaFirmaAVacio(Persona personaVacia, String nuevaFirma) {
        boolean existeFirma = false;
        for (PersonaFirma perFirma : personaVacia.getPersonaFirmasCollection()) {
            if (StringUtils.equalsIgnoreCase(perFirma.getFirma().getNombre(),nuevaFirma)) {
                existeFirma = true;
                break;
            }
        }
        if (!existeFirma) {
            
        }
    }
    
    /**
     * Agrega la firma de la persona vacía al listado de autores
     * @param personaVacia Entidad que contiene la persona sin datos pero con firmas de desconocidos
     * @param firma firma a agregarse
     */
    private boolean agregarFirmaSinAutor(Persona personaVacia, String strNuevaFirma) {
        boolean encontrado = false;
        for (PersonaArticulo perart : listaPersonaArticulo) {
            //revisar si ya existe la persona y la firma en la lista de autores
            if (perart.getPersona().getId().equals(personaVacia.getId()) && StringUtils.equalsIgnoreCase(perart.getFirma().getNombre(), strNuevaFirma) ) {
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            PersonaFirma personaFirmaUsar = null;
            for (PersonaFirma perFirma : personaVacia.getPersonaFirmasCollection()) {
                //revisar si ya existe la firma que se va a ingresar en la lista de firmas de la personavacia
                if (StringUtils.equalsIgnoreCase(perFirma.getFirma().getNombre(), strNuevaFirma)) {
                    personaFirmaUsar = perFirma;
                    break;
                }
            }
            
            if (personaFirmaUsar == null) {
                //si la firma no pertenece a la personavacia es porque es una nueva firma no existente en la base de datos, 
                // por lo tanto hay que crear una nueva firma para agregarla
                Firma nuevaFirma = new Firma();
                nuevaFirma.setNombre(strNuevaFirma);
                nuevaFirma.setId(idFirmaGen);
                
                idFirmaGen--;
                
                PersonaFirma nuevaPersonaFirma = new PersonaFirma();
                nuevaPersonaFirma.setFirma(nuevaFirma);
                nuevaPersonaFirma.setId(idPersonaFirmaGen);
                nuevaPersonaFirma.setPersona(personaVacia);    
                
                idPersonaFirmaGen--;
                
                personaFirmaUsar = nuevaPersonaFirma;
            }            
                                
            PersonaArticulo perArt = new PersonaArticulo();
            perArt.setPersona(personaVacia);
            perArt.setFirma(personaFirmaUsar.getFirma());
            perArt.setPersonaFirma(personaFirmaUsar);
            perArt.setId(idPersonaArticuloGen);
            
            personaVacia.getPersonaFirmasCollection().add(personaFirmaUsar);
            
            listaPersonaArticulo.add(perArt);
            
            idPersonaArticuloGen--;            
            
            return true;
        }
        return false;
    }
   
    public void onPersonaChosen(SelectEvent event) {
        List<Persona> listaPersonasSel = (List<Persona>) event.getObject();
        //FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Car Selected", "Id:" + car.getId());
        if (listaPersonasSel != null) {
            for (Persona per : listaPersonasSel) {
                /*boolean encontrado = false;
                for (PersonaArticulo perart : listaPersonaArticulo) {
                    if (perart.getPersona().getId().equals(per.getId())) {
                        encontrado = true;
                        break;
                    }
                }*/
                //if (!encontrado) {
                agregarNuevoAutor(per, null);
                //}
            }

            //listaAutores.addAll(listaPersonasSel);
            RequestContext.getCurrentInstance().update("formContenido:dtAutores");
        }
    }
    public void onInstitucionChosen(SelectEvent event) {
        List<Institucion> listaSel = (List<Institucion>) event.getObject();
        if (listaSel != null) {
            for (Institucion inst : listaSel) {
                if (!listaAgradecimientos.contains(inst)) {
                    listaAgradecimientos.add(inst);
                }
            }
            RequestContext.getCurrentInstance().update("formContenido:dtAutores");
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
            RequestContext.getCurrentInstance().update("formContenido:dtAutores");
        }
    }

    
    public void agregarAutor() {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", true);
        options.put("draggable", true);
        options.put("width", "75%");
        options.put("modal", true);
        options.put("contentWidth", "100%");
        GestorDialogListaPersonas.getInstance().prepararApertura();
        RequestContext.getCurrentInstance().openDialog("dialogListaPersonas", options, null);
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

    public void agregarAgradecimiento() {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", true);
        options.put("draggable", true);
        options.put("width", "50%");
        options.put("modal", true);
        options.put("contentWidth", "100%");
        GestorDialogListaInstituciones.getInstance().clearListaSeleccionados();
        RequestContext.getCurrentInstance().openDialog("dialogListaInstituciones", options, null);
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

    public StreamedContent streamParaDescargaBibtex(Articulo articuloDescarga) {
        String nombreArchivo = articuloDescarga.getArchivoBibtex();
        try {
            return BeansUtils.streamParaDescarga(ServerUtils.getPathArticulos().resolve(nombreArchivo), "bibtex_" + articuloDescarga.getNombre());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }    
    
    
    public StreamedContent streamParaDescarga(Articulo articuloDescarga) {
        String nombreArchivo = articuloDescarga.getArchivoArticulo();
        try {
            return BeansUtils.streamParaDescarga(ServerUtils.getPathArticulos().resolve(nombreArchivo), "articulo_" + articuloDescarga.getNombre());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String convertirListaInstitucion(List<Institucion> instituciones) {
        String r = "";
        for (Iterator<Institucion> iter = instituciones.iterator(); iter.hasNext();) {
            Institucion inst = iter.next();
            r += inst.getNombre() + (iter.hasNext() ? ", " : "");
        }
        return r;
    }

    public String convertirListaPersonas(Collection<PersonaArticulo> listaConvertir) {
        String r = "";
        int c = 0;
        for (PersonaArticulo per : listaConvertir) {
            r += per.getPersonaFirma().getFirma().getNombre();
            if (c < listaConvertir.size() - 1) {
                r += ", ";
            }
            c++;
        }
        /*if (!r.isEmpty()) {
            r = r.substring(0, r.length() - 2);
        }*/

        return r;
    }

    public static GestorArticulo getInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext context = facesContext.getELContext();
        ValueExpression ex = facesContext.getApplication().getExpressionFactory().createValueExpression(context, "#{gestorArticulo}", GestorArticulo.class);
        return (GestorArticulo) ex.getValue(context);
    }
    public void fileUploadListener(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        boolean isBibtex = event.getComponent().getAttributes().get("bibtex") != null;

        //nombre del archivo que ya se encuentra almacenado en las propiedades
        String nombreArchivoGuardado;
        if (isBibtex) {
            nombreArchivoGuardado = articulo.getArchivoBibtex();
        } else {
            nombreArchivoGuardado = articulo.getArchivoArticulo();
        }

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

                if (isBibtex) {
                    articulo.setArchivoBibtex(nombreArchivoSubido);
                    tamanoArchivoBibtex = ServerUtils.humanReadableByteCount(file.getSize());
                    if (!soloSubirBibtex) {
                        leerBibtex(pathArchivo);
                    }
                } else {
                    articulo.setArchivoArticulo(nombreArchivoSubido);
                    tamanoArchivo = ServerUtils.humanReadableByteCount(file.getSize());
                }
            } catch (IOException ex) {
                Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            System.err.println("Error al subir archivo");
        }

    }

    public void leerBibtex(Path pathBibtex) {
        try {
            BibTeXParser bibtexParser = new BibTeXParser();

            BufferedReader br = new BufferedReader(new FileReader(pathBibtex.toFile()));
            BibTeXDatabase database = bibtexParser.parse(br);

            Map<Key, BibTeXEntry> entryMap = database.getEntries();
            BibTeXEntry firstEntry = null;
            for (Map.Entry<Key, BibTeXEntry> entry : entryMap.entrySet()) {
                if (firstEntry == null) {
                    firstEntry = entry.getValue();
                    break;
                }
                //break;
                //System.out.println(entry.getKey() + "/" + entry.getValue());
            }
            if (firstEntry != null) {
                Value valTitulo = firstEntry.getField(BibTeXEntry.KEY_TITLE);
                Value valRevista = firstEntry.getField(BibTeXEntry.KEY_JOURNAL);
                Value valPublicado = firstEntry.getField(BibTeXEntry.KEY_YEAR);
                Value valAutores = firstEntry.getField(BibTeXEntry.KEY_AUTHOR);                
                //Value valAutores = firstEntry.getField(BibTeXEntry.KEY_ADDRESS)
                Key keyAbstract = new Key("abstract");
                Value valAbstract = firstEntry.getField(keyAbstract);
                
                Key keyUrl = new Key("url");
                Value valUrl = firstEntry.getField(keyUrl);
                
                if (firstEntry.getType().equals(BibTeXEntry.TYPE_ARTICLE)) {
                    articulo.setTipo(TipoArticulo.SCOPUS);
                } else if (firstEntry.getType().equals(BibTeXEntry.TYPE_BOOK)) {
                    articulo.setTipo(TipoArticulo.LIBRO);
                } else if (firstEntry.getType().equals(BibTeXEntry.TYPE_TECHREPORT)) {
                    articulo.setTipo(TipoArticulo.NOTA_TECNICA);
                } else if (firstEntry.getType().equals(BibTeXEntry.TYPE_MASTERSTHESIS)) {
                    articulo.setTipo(TipoArticulo.TESIS);
                }                
                articulo.setNombre(BeansUtils.value2String(valTitulo));
                articulo.setRevista(BeansUtils.value2String(valRevista));                
                articulo.setResumen(BeansUtils.value2String(valAbstract));
                articulo.setEnlace(BeansUtils.value2String(valUrl));
                String strAutoresBibtex = BeansUtils.value2String(valAutores);
                
                if (valPublicado != null) {
                    int anio = Integer.parseInt(valPublicado.toUserString());
                    articulo.setAnioPublicacion(anio);
                }
                if (valAutores != null) {                    
                    Persona personaVacia = personaController.obtenerPersonaVacia();
                    String autores[] = strAutoresBibtex.split(" and ");
                    Arrays.stream(autores).forEach(autor -> System.out.println("Autor: " + autor));
                    List<String> firmasAutores = Arrays.asList(autores);
                    try {
                        //List<Firma> listaFirmasExistentes = personaController.findFirmas(firmasAutores);
                        List<Persona> listaPersonasConFirmaExistentes = personaController.findPersonaConFirma(firmasAutores);
                        
                        for (String strFirmaBibtex : firmasAutores) {
                            boolean encontrado = false;
                            for (Persona per : listaPersonasConFirmaExistentes) {
                                for (PersonaFirma perFirma : per.getPersonaFirmasCollection()) {
                                    if (StringUtils.equalsIgnoreCase(perFirma.getFirma().getNombre(), strFirmaBibtex)) {
                                        encontrado = true;
                                        if (per.getId().equals(personaVacia.getId())) {
                                            agregarFirmaSinAutor(personaVacia, strFirmaBibtex);
                                        } else {
                                            agregarNuevoAutor(per, strFirmaBibtex);
                                        }
                                        break;
                                    }
                                    
                                }
                            }
                            
                            if (!encontrado) {
                                //cuando no se encontro la firma del bibtex en la base de datos, por lo tanto es una firma de 
                                // un desconocido (persona externa al departamento)
                                
                                agregarFirmaSinAutor(personaVacia, strFirmaBibtex);
                            }
                        }
                        
                    } catch (Exception ex) {
                        Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                //Value valTitulo = firstEntry.getField(BibTeXEntry.KEY_TITLE);

                /*Collection<BibTeXEntry> entries = entryMap.values();
                for (BibTeXEntry entry : entries) {
                    Value value = entry.getField(BibTeXEntry.KEY_TITLE);
                    if (value == null) {
                        continue;
                    }

                    // Do something with the title value
                }*/
            }
        } catch (ParseException | TokenMgrException | FileNotFoundException ex) {
            Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean existeAutorEnLista(Persona autorComprobar) {
        for (PersonaArticulo perArticulo : listaPersonaArticulo) {
            if (Objects.equals(perArticulo.getPersona().getId(), autorComprobar.getId())) {
                return true;
            }
        }
        return false;
    }

    public String guardar() {
        //articulo.setAutoresCollection(new HashSet(listaAutores));
        articulo.setPersonasArticuloCollection(listaPersonaArticulo);
        articulo.setProyectosCollection(new HashSet(listaProyectos));
        articulo.setAgradecimientosCollection(new HashSet(listaAgradecimientos));
        //articuloController.create(articulo);
        
        for (PersonaArticulo perArticulo : articulo.getPersonasArticuloCollection()) {
            if (perArticulo.getPersonaFirma() == null) {
                PersonaFirma perFirma = personaController.findPersonaFirma(perArticulo.getPersona().getId(), perArticulo.getFirma().getId());
                perArticulo.setPersonaFirma(perFirma);
            }
        }
        
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

    public void inicializarManejoArticulo() {
        articulo = new Articulo();
        listaPersonaArticulo.clear();
        listaAgradecimientos.clear();
        listaProyectos.clear();
        tamanoArchivo = "";
        tamanoArchivoBibtex = "";
        soloSubirBibtex = false;
        modoModificar = false;
        idPersonaArticuloGen = -1L;
        idPersonaFirmaGen = -1L;
        idFirmaGen = -1L;
        GestorPersona.getInstance().actualizarListaPersonasConContrato();
        GestorInstitucion.getInstance().actualizarListaInstituciones();
        GestorProyecto.getInstance().actualizarListaProyecto();
        GestorDialogListaPersonas.getInstance().resetearDialog();
    }

    public String initModificarArticulo(Long id) {
        inicializarManejoArticulo();
        articulo = articuloController.findArticulo(id);
        if (articulo.getArchivoBibtex().isEmpty()) {
            soloSubirBibtex = true;
        }
        listaPersonaArticulo = new ArrayList<>(articulo.getPersonasArticuloCollection());
        ordenarListaPersonaArticulo(listaPersonaArticulo);
        listaAgradecimientos = new ArrayList<>(articulo.getAgradecimientosCollection());
        listaProyectos = new ArrayList<>(articulo.getProyectosCollection());

        Path pathArchivoArticulo = ServerUtils.getPathArticulos().resolve(articulo.getArchivoArticulo());
        tamanoArchivo = ServerUtils.tamanoArchivo(pathArchivoArticulo);
        
        Path pathArchivoBibtex = ServerUtils.getPathArticulos().resolve(articulo.getArchivoBibtex());
        tamanoArchivoBibtex = ServerUtils.tamanoArchivo(pathArchivoBibtex);

        modoModificar = true;

        return "manejoArticulo";
    }
    public String initCrearArticulo() {
        inicializarManejoArticulo();

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
        GestorInstitucion.getInstance().actualizarListaInstituciones();
        return "listaArticulos";
    }

    public boolean filtrarPorInstFinanciamiento(Object value, Object filter, Locale locale) {
        Institucion institucionFiltro = (Institucion) filter;
        if (institucionFiltro == null) {
            return true;
        }

        List<Institucion> instituciones = (List<Institucion>) value;
        return (instituciones.contains(institucionFiltro));
        /*
        for (Institucion inst : instituciones) {
            if (StringUtils.containsIgnoreCase(inst.getNombre(),filterText)) {
                return true;
            }
        }
        return false;*/
    }

    
    public boolean filtrarPorAutores(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim();
        if (filterText == null || filterText.equals("")) {
            return true;
        }

        if (value == null) {
            return false;
        }

        Collection<PersonaArticulo> autores = (Collection) value;
        for (PersonaArticulo per : autores) {
            if (StringUtils.containsIgnoreCase(per.getPersona().getNombres(), filterText) || StringUtils.containsIgnoreCase(per.getPersona().getApellidos(), filterText)) {
                return true;
            }
        }

        return false;
    }

    public boolean filtrarPorAutorPrincipal(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim();
        if (filterText == null || filterText.equals("")) {
            return true;
        }

        if (value == null) {
            return false;
        }

        //como el autor principal es siempre el primer elemento, solo revisar el primer elemento
        Optional<PersonaArticulo> primerElem = ((Collection<PersonaArticulo>) value).stream().findFirst();
        //PersonaArticulo perArticuloPrincipal = ((Collection<PersonaArticulo>) value).stream().findFirst().get();
        if (primerElem.isPresent()) {
            Persona autorPrincipal = primerElem.get().getPersona();
            return (StringUtils.containsIgnoreCase(autorPrincipal.getNombres(), filterText)
                    || StringUtils.containsIgnoreCase(autorPrincipal.getApellidos(), filterText));
        }

        return false;
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

    public List<Proyecto> getListaProyectos() {
        return listaProyectos;
    }

    public void setListaProyectos(List<Proyecto> listaProyectos) {
        this.listaProyectos = listaProyectos;
    }

    public List<Institucion> getListaAgradecimientos() {
        return listaAgradecimientos;
    }

    public void setListaAgradecimientos(List<Institucion> listaAgradecimientos) {
        this.listaAgradecimientos = listaAgradecimientos;
    }

    public boolean isModoModificar() {
        return modoModificar;
    }

    public void setModoModificar(boolean modoModificar) {
        this.modoModificar = modoModificar;
    }

    public String getTamanoArchivoBibtex() {
        return tamanoArchivoBibtex;
    }

    public void setTamanoArchivoBibtex(String tamanoArchivoBibtex) {
        this.tamanoArchivoBibtex = tamanoArchivoBibtex;
    }

    public String initVerArticulo(Long id) {
        inicializarManejoArticulo();
        articulo = articuloController.findArticulo(id, true, true);
        listaProyectos = new ArrayList<>(articulo.getProyectosCollection());
        listaAgradecimientos = new ArrayList<>(articulo.getAgradecimientosCollection());
        listaPersonaArticulo = new ArrayList<>(articulo.getPersonasArticuloCollection());
        modoModificar = false;

        return "verArticulo";
    }

    public boolean isSoloSubirBibtex() {
        return soloSubirBibtex;
    }

    public void setSoloSubirBibtex(boolean soloSubirBibtex) {
        this.soloSubirBibtex = soloSubirBibtex;
    }


}
