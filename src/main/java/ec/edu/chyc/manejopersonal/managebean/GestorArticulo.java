/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

import ec.edu.chyc.manejopersonal.controller.ArticuloJpaController;
import ec.edu.chyc.manejopersonal.entity.Articulo;
import ec.edu.chyc.manejopersonal.entity.Persona;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

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
    
    public String guardar() {
        return "";
    }
    
    public String initCrearArticulo() {
        articulo = new Articulo();
        
        listaAutores.clear();
        
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

}
