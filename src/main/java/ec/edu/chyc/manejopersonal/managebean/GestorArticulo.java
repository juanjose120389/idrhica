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
import ec.edu.chyc.manejopersonal.entity.Lugar;
import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.entity.PersonaArticulo;
import ec.edu.chyc.manejopersonal.entity.PersonaFirma;
import ec.edu.chyc.manejopersonal.entity.Proyecto;
import ec.edu.chyc.manejopersonal.managebean.util.BeansUtils;
import ec.edu.chyc.manejopersonal.util.ServerUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
    
    private List<Articulo> listaArticulos = new ArrayList<>(); //lista de artículos que se muestran en la vista principal
    private List<PersonaArticulo> listaPersonaArticulo = new ArrayList<>(); //Los autores del artículo
    private List<Proyecto> listaProyectos = new ArrayList<>(); //Lista de proyectos relacionados al artículo
    private List<Institucion> listaAgradecimientos = new ArrayList<>(); //Lista de instituciones que el artículo agradece
    private Articulo articulo;
    private String tamanoArchivo; //Tamaño del archivo (artículo) en formato visible al usuario
    private String tamanoArchivoBibtex; //Tamaño del bibtex en formato visible al usuario
    private boolean modoModificar = false;
    //ids generados para nuevos elementos
    private Long idPersonaArticuloGen = -1L;
    private Long idPersonaFirmaGen = -1L;
    private Long idFirmaGen = -1L;
    
    private boolean soloSubirBibtex = false; //si es true, solo se sube el archivo y todos los campos quedan intactos, si vale false,
                                             // se llenarán todos los campos de acuerdo a lo leído en el bibtex
    
    public GestorArticulo() {
    }

    @PostConstruct
    public void init() {

    }

    //mover una posición arriba el puesto de un autor
    public void moverArriba(PersonaArticulo personaArticuloMover, Integer indexActual) {
        if (indexActual != 0) {
            //Collections.swap intercambia el contenido de las posiciones dadas
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
        //buscar si la persona ya se encuentra en la lista de autores
        for (PersonaArticulo perart : listaPersonaArticulo) {
            if (perart.getPersona().getId().equals(per.getId())) {
                encontrado = true;
                break;
            }
        }

        if (!encontrado) { 
            //Agregar la nueva persona 
            PersonaArticulo perArt = new PersonaArticulo();
            perArt.setPersona(per);
            per.getListaFirmas().clear();
            int c = 0;
            
            //Obtener el objeto firma para indicar que el autor eligió la firma
            for (PersonaFirma perFirma : per.getPersonaFirmasCollection()) {
                if (firmaSel == null) { //Si no eligió una firma de la persona, usar la primera por defecto
                    if (c == 0) {
                        perArt.setFirma(perFirma.getFirma());
                    }
                } else if (StringUtils.equalsIgnoreCase(perFirma.getFirma().getNombre(), firmaSel)) { //Si la firma coincide con la seleccionada
                    perArt.setFirma(perFirma.getFirma()); //usar esa firma
                }
                per.getListaFirmas().add(perFirma.getFirma());
                c++;
            }
            
            perArt.setId(idPersonaArticuloGen);

            //Si la persona no posee ninguna firma o la firma es nueva, crear nueva firma y asignarla a la persona
            if (firmaSel == null && perArt.getFirma() == null) {
                
                Firma firmaNueva = new Firma();
                firmaNueva.setId(idFirmaGen);
                firmaNueva.setNombre(per.getApellidos() + ", " + per.getNombres());
            
                PersonaFirma perFirmaNuevo = new PersonaFirma();
                perFirmaNuevo.setId(idPersonaFirmaGen);
                perFirmaNuevo.setPersona(per);
                perFirmaNuevo.setFirma(firmaNueva);
                
                perArt.setPersonaFirma(perFirmaNuevo);
                perArt.setPersona(per);
                perArt.setFirma(firmaNueva);
                
                per.getListaFirmas().add(perFirmaNuevo.getFirma());
                
                idPersonaFirmaGen--;
                idFirmaGen--;
            }
            
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

    
    /***
     * Agrega http:// a la url en caso de no tener
     * @param url Texto correspondiente a la url a verificar
     * @return Url corregida o la misma si es que ya poseía http://
     */
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

    /***
     * Conviert la lista de instituciones en una cadena de texto separada por comas
     * @param instituciones
     * @return 
     */
    public String convertirListaInstitucion(List<Institucion> instituciones) {
        String r = "";
        for (Iterator<Institucion> iter = instituciones.iterator(); iter.hasNext();) {
            Institucion inst = iter.next();
            r += inst.getNombre() + (iter.hasNext() ? ", " : "");
        }
        return r;
    }
    /***
     * Conviert la lista de personas en una cadena de texto separada por comas
     * @param listaConvertir
     * @return 
     */
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
                        listaPersonaArticulo.clear();
                        leerBibtex(pathArchivo);
                    }
                } else {
                    articulo.setArchivoArticulo(nombreArchivoSubido);
                    tamanoArchivo = ServerUtils.humanReadableByteCount(file.getSize());
                }
            } catch (IOException ex) {
                GestorMensajes.getInstance().mostrarMensajeError(ex.getLocalizedMessage());
                Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            System.err.println("Error al subir archivo");
        }

    }

    /**
     * Lee el bibtex en un String, quitando los saltos de línea
     * @param pathBibtex Path del archivo a leer
     * @return String que contiene todo el bibtex sin saltos de línea
     * @throws FileNotFoundException En caso de no encontrar el archivo 
     */
    private String unirStringBibtex(Path pathBibtex) throws FileNotFoundException {
        try {
            byte[] bytesArchivo = Files.readAllBytes(pathBibtex);
            
            //usar utf-8 por defecto para pasar el contenido del archivo a String
            String text = new String(bytesArchivo, StandardCharsets.UTF_8);
            char[] charArray = text.toCharArray();
            for (char c : charArray) {
                if (c == 65533) { 
                    //en caso de encontrar un caracter que no se pudo codificar, intentar la codificación "Windows-1252" y transformarla de nuevo a utf-8
                    String windows1252 = new String(bytesArchivo, "Windows-1252");
                    text = new String(windows1252.getBytes(StandardCharsets.UTF_8));
                    break;
                }
            }
            return text;
        } catch (IOException ex) {
            Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
/*        
        InputStreamReader isr = null;
        try {
            FileInputStream is = new FileInputStream(pathBibtex.toFile());
            //isr = new InputStreamReader(is, StandardCharsets.ISO_8859_1);
            isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            //BufferedReader br = new BufferedReader(new FileReader(pathBibtex.toFile()));
            StringBuilder stringBuf = new StringBuilder();
            String linea;
            try {
                while ((linea = br.readLine()) != null) {
                    stringBuf.append(linea).append(" ");
                }
                return stringBuf.toString();
            } catch (IOException ex) {
                Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }/* catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                isr.close();
            } catch (IOException ex) {
                Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
        return "";*/
    }

    /**
     * Lee el archivo bibtex y llena los campos del artículo según exista en el bibtex
     * @param pathBibtex Ruta del archivo bibtex
     */    
    public void leerBibtex(Path pathBibtex) {
        try {
            BibTeXParser bibtexParser = new BibTeXParser();

            String stringUnida = unirStringBibtex(pathBibtex);
            if (stringUnida.indexOf("\0") >= 0) {
                try {
                    stringUnida = stringUnida.replace("\0", "");
                    String decoded = new String(stringUnida.getBytes("ISO-8859-1"), "UTF-8");
                    stringUnida = decoded;
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //stringUnida = stringUnida.replace("\0", "");
            StringReader stringReader = new StringReader(stringUnida);
            
            BibTeXDatabase database = bibtexParser.parse(stringReader);

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
                        //buscar personas que lleven las firmas indicadas en el bibtex
                        List<Persona> listaPersonasConFirmaExistentes = personaController.findPersonaConFirma(firmasAutores);
                        
                        for (String strFirmaBibtex : firmasAutores) {
                            boolean encontrado = false;//valdrá true al encontrar la persona que tenga la firma que está en el bibtex
                            for (Persona per : listaPersonasConFirmaExistentes) {
                                for (PersonaFirma perFirma : per.getPersonaFirmasCollection()) {
                                    if (StringUtils.equalsIgnoreCase(perFirma.getFirma().getNombre(), strFirmaBibtex)) {
                                        encontrado = true;
                                        if (per.getId().equals(personaVacia.getId())) {//si la firma pertenece al id=1 (general), agregar sin colocar datos de autor
                                            agregarFirmaSinAutor(personaVacia, strFirmaBibtex);
                                        } else { //agregar los datos del autor
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

        //Asignar a cada autor, la firma que se eligió para el artículo
        for (PersonaArticulo perArticulo : articulo.getPersonasArticuloCollection()) {
            if (perArticulo.getPersonaFirma() == null || !perArticulo.getFirma().getId().equals(perArticulo.getPersonaFirma().getFirma().getId())) {
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
            return initListarArticulos();// "index";
        } catch (Exception ex) {
            Logger.getLogger(GestorArticulo.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    public TipoArticulo[] getTiposArticulo() {
        return TipoArticulo.values();
    }

    /**
     * Ordena la lista de PersonaArticulo de acuerdo al atributo orden, orden ascendente
     * @param listaPersonaArticulo Lista de PersonaArticulo
     */
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
        GestorPersona.getInstance().actualizarListado();
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
        
        //llenar cada ítem de autor con su lista de firmas
        for (PersonaArticulo perArt : listaPersonaArticulo) {
            Persona perDeArticulo = perArt.getPersonaFirma().getPersona();
            //cargar datos del autor incluyendo todas sus firmas
            Persona personaDetalle = personaController.findPersona(perDeArticulo.getId(), PersonaJpaController.Incluir.INC_FIRMAS.value());
            //Persona personaDetalle = personaController.findPersona(perDeArticulo.getId(), false, false, false, false, false, true);
            
            //agregar todas las firmas a la lista
            perDeArticulo.getListaFirmas().clear();
            for (PersonaFirma perFirma : personaDetalle.getPersonaFirmasCollection()) {
                perDeArticulo.getListaFirmas().add(perFirma.getFirma());
            }
            perArt.setFirma(perArt.getPersonaFirma().getFirma());
            perArt.setPersona(perDeArticulo);            
        }
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
        GestorProyecto.getInstance().actualizarListaLugares();
        GestorProyecto.getInstance().getListaLugaresAgregados().clear();        
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

    public boolean filtrarPorLugar(Object value, Object filter, Locale locale) {
        Lugar lugarFilter = (Lugar) filter;
        if (lugarFilter == null) {
            return true;
        }

        List<Lugar> lugares = (List<Lugar>) value;
        return (lugares.contains(lugarFilter));
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
